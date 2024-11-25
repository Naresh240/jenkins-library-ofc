def call(Map stageParams) {
    checkout([$class: 'GitSCM', 
        branches: [[name: "*/${stageParams.branchName}"]], 
        userRemoteConfigs: [[
            credentialsId: ${stageParams.credentialsId},
            url: stageParams.seviceRepoUrl
        ]]
    ])
    echo "Checked out branch: ${stageParams.branchName} for service: ${stageParams.serviceName}"
}
