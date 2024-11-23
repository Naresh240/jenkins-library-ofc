@Library('jenkins-shared-library') _

pipeline {
    parameters {
        choice(name: 'SERVICE_NAME', choices: ['hrs-wld-requestor', 'hrs-wld-responder', 'hrs-hazelcast-server', 'hrs-monitoring-service', 'hrs-netprobe-service'], description: 'Select the component to build and deploy')
        choice(name: 'ENV_NAME', choices: ['dk2406-m', 'dk2406-o', 'dk2406-w'], description: 'INT Environment name')
        choice(name: 'BACKEND_FLOW', choices: ['regp', 'hfss', 'pbiso', 'sepcl', 'rterp'], description: 'Set the backend flow')
        choice(name: 'REPLICA_COUNT', choices: ['1', '2', '3', '4', '5'], description: 'Set the replica count')
        choice(name: 'APPLICATION_RUNTIME', choices: ['regp', 'hfss', 'pbiso', 'sepa', 'rp'], description: 'Application runtime name')
    }
    agent any
    environment {
        JENKINS_CREDENTIALS_ID = "seai_build"
    }
    stages {
        stage ('checkout') {
            steps {
                withCredentialsWrapper(env.JENKINS_CREDENTIALS_ID) {
                    gitCheckout(
                        serviceName: params.SERVICE_NAME,
                        branchName: '*/develop',
                        credentialsId: env.JENKINS_CREDENTIALS_ID
                    )
                }
            }
        }
    }
}