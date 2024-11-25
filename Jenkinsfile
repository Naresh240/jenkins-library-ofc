@Library('jenkins-shared-library') _

pipeline {
    parameters{
        choice(
            name: 'SERVICE_NAME',
            choices: ['hrs-wld-requestor', 'hrs-wld-responder', 'hrs-hazelcast-server', 'hrs-monitoring-service', 'hrs-netprobe-service'],
            description: 'Select the component to build and deploy')
        choice(
            name: 'ENV_NAME',
            choices: ['dk2406-m', 'dk2406-o', 'dk2406-w'],
            description: 'INT Environmnet name') 
        choice(
            name: 'BACKEND_FLOW',
            choices: ['regp', 'hfss', 'pbiso', 'sepcl', 'rterp'],
            description: 'Set the backend flow')
        choice(
            name: 'REPLICA_COUNT',
            choices: ['1', '2', '3', '4', '5']
            description: 'Set the replica count')
        choice(
            name: 'APPLICATION_RUNTIME',
            choices: ['regp', 'hfss', 'pbiso', 'sepa', 'rp'],
            description: 'Application runtime name')
    }

    agent {
        kubernates {
            cloud "${params.ENV_NAME}"
            label "build-develop-${env.BUILD_ID}"
            yamlFile "../agent-containers.yaml"
            defaultContainer 'fabric-tools'
        }
    }

    environment{
        //openshift details
        DAP_SECRET = "dap_key"
        OC_PROJECT = "dk2406"

        //INT
        INT_OC_TOKEN_SECRET = "${params.ENV_NAME}-sa-edit-token-text"
        INT_OC_NAMESPACE = "${params.ENV_NAME}"

        REGISRTY_URL = "https://docker-registry.svc.uk.paas.internet.db.com"
        GCP_ARTIFACTORY_SECRET = "gcp-art-registry-credentials"
        ARTIFACTORY_REL_SECRET = "art-releaser-credentials"
        ARTIFACTORY_SECRET = "artifactory-credentials"
        GCP_ARTIFACTORY_URL = "https://artifactory.sd;c.ctl.gcp.db.com:443/artifactory/dkr-public-local"
        GCP_ARTIFACTORY_NAMESPACE = "com/db/hotscan/hsrsl"
        ARTIFACTORY_URL = "https://artifactory.internet.db.com/artifactory/hlm-public-local"
        ARTIFACTORY_NAMESPACE = "com/db/hsrsl"
        CHART_NAME = "${params.SERVICE_NAME}"
        INSTANCE_NAME = "${params.BACKEND_FLOW}"
        HELM_REPO_URL = "https://artifactory.internet.db.com/artifactory/hlm-all"

        JENKINS_CREDENTIALS_ID = "seai_build"

        IMAGE_VERSION = ""
        IMAGE_TAG = ""
        PACKAGE_NAME = ""
        PACKAGE_VERSION = ""
        CONFIG_CHANGED = ""
        CODE_CHANGED = ""
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    def serviceRepoMap = [
                        'hrs-wld-requestor': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-wld-requestor.git',
                        'hrs-wld-responder': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-wld-responder.git', 
                        'hrs-hazelcast-server': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-hazelcast-server.git', 
                        'hrs-monitoring-service': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-monitoring-service.git', 
                        'hrs-netprobe-service': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-netprobe-service.git'
                    ]

                    def serviceRepoUrl = serviceRepoMap[params.SERVICE_NAME]

                    // Use the withCredentialsClosureClosure function
                    withCredentialsClosure(env.JENKINS_CREDENTIALS_ID) { username, password ->
                            gitCheckout(
                                seviceRepoUrl: serviceRepoUrl,
                                branchName: '*/develop',
                                credentialsId: env.JENKINS_CREDENTIALS_ID
                            )
                        echo "Checking out branch: develop"
                    }
                }
            }
        }
        stage('check for change') {
            steps {
                withCredentialsClosure(env.JENKINS_CREDENTIALS_ID) { username, password -> 
                    def changes = checkChanges()
                    CODE_CHANGED = changes.codeChanged
                    CONFIG_CHANGED = changes.configChanged
                }
            }
        }
        stage('Check for Image Tag') {
            steps {
                script {
                    checkForImageTag()
                }
            }
        }
        stage('Verify latest Image Tag') {
            steps {
                container ('cb-jenkins-agent') {
                    withCredentialsClosure(env.GCP_ARTIFACTORY_SECRET) { username, password ->
                        extractLatestVersion(
                            gcpArtifactoryUrl: env.GCP_ARTIFACTORY_URL,
                            gcpArtifactoryNamespace: env.GCP_ARTIFACTORY_NAMESPACE,
                            chartName: env.CHART_NAME,
                            gcpArtifUsername: username,
                            gcpArtifPassword: password,
                            proxyUrl: env.PROXY_URL
                        )
                    }
                }
            }
        }
        stage('Update Image Tag') {
            when {
                expression { return CODE_CHANGED }
            }
            steps {
                container ('cb-jenkins-agent'){
                    withCredentialsClosure(env.JENKINS_CREDENTIALS_ID) { username, password ->
                        def result = updateImageVersion(IMAGE_VERSION)
                        IMAGE_TAG = result.imageTag
                        IMAGE_VERSION = result.imageVersion
                        echo "Updated image tag: ${IMAGE_TAG} and image version: ${IMAGE_VERSION}"
                    }
                }
            }
        }
        stage('Build and push Docker image') {
            when {
                expression { return CODE_CHANGED }
            }
            steps {
                container ('cb-jenkins-agent'){
                    withCredentialsClosure(env.JENKINS_CREDENTIALS_ID) { username, password ->
                        buildDockerImage(IMAGE_TAG, CHART_NAME)
                    }
                }
            }
        }
        stage('check for Helm package') {
            steps {
                script {
                    checkHelmPackage(CONFIG_CHANGED)
                }
            }
        }
        stage('Verify Helm Installation') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(
                        credentialsID: 'Kubeconfig_dep_secret',
                        keyFileVariable: 'KUBECONFIG')
                    ]){
                        sh 'helm version'
                    }
                }
            }
        }
        stage('Verify latest Helm package') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: "${env.ARTIFACTORY_REL_SECRET}", usernameVariable: 'ART_REL_USERNAME', passwordVariable: 'ART_REL_PASSWPRD'),
                    sshUserPrivateKey(credentialsID: 'Kubeconfig_dap_secret', keyFileVariable: 'KUBECOMFIG')
                ]) {
                    script {
                        dir("config/helm-packages") {
                        def helmInfo = verifyHelmPackage(
                                chartName: CHART_NAME,
                                artifactUrl: ARTIFACTORY_URL,
                                username: ART_REL_USERNAME,
                                password: ART_REL_PASSWORD
                            )
                        env.PACKAGE_NAME = helmInfo.packageName
                        env.PACKAGE_VERSION = helmInfo.packageVersion
                        }
                    }

                }
            }
        }
    }
}