def call(Map stageParams) {
    def serviceRepoMap = [
        'hrs-wld-requestor': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-wld-requestor.git',
        'hrs-wld-responder': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-wld-responder.git',
        'hrs-hazelcast-server': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-hazelcast-server.git',
        'hrs-monitoring-service': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-monitoring-service.git',
        'hrs-netprobe-service': 'https://stash.gto.internet.db.com:8081/scm/hsrsl/hrs-netprobe-service.git'
    ]

    def seviceRepoUrl = serviceRepoMap[stageParams.serviceName]
    if (!repoUrl) {
        error "Unknown service name: ${stageParams.serviceName}"
    }

    checkout([$class: 'GitSCM', 
        branches: [[name: "*/${stageParams.branchName}"]], 
        userRemoteConfigs: [[
            credentialsId: ${stageParams.credentialsId},
            url: seviceRepoUrl
        ]]
    ])
    echo "Checked out branch: ${stageParams.branchName} for service: ${stageParams.serviceName}"
}
