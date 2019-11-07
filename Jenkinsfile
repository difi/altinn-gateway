pipelineWithMavenAndDocker {
    enableDependencyTrack = true
    verificationEnvironment = 'eid-verification2'
    stagingEnvironment = 'eid-staging'
    stagingEnvironmentType = 'puppet2'
    productionEnvironment = 'eid-production'
    gitSshKey = 'ssh.github.com'
    puppetModules = 'altinn_gateway'
    puppetApplyList = ['eid-systest-admin01.dmz.local baseconfig,altinn_gateway']
}
