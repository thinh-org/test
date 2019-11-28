def repository = 'https://github.com/thinh-org/test.git'
multibranchPipelineJob('test') {
    branchSources {
        git {
            id(UUID.randomUUID().toString())
            remote(repository)
            credentialsId('github-credential')
            includes("master")
            excludes("")
        }
    }
    triggers {
        cron('@daily')
    }
}