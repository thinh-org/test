def projectProperties = [
    [$class: 'BuildDiscarderProperty',strategy: [$class: 'LogRotator', numToKeepStr: '10']],
	pipelineTriggers([pollSCM('*/30 * * * *')])

]

properties(projectProperties)


node {
    try {
        def project = 'test'

        def sourceName = project + '-' + 'src-' + env.BRANCH_NAME + '.zip'
        def sourcePath = '.'
        def sourceFullPath = sourcePath + '/' + sourceName
        
        def targetCredential = ''
        def targetUsername = ''
        def targetAddress = ''
        def targetSSH = ''

        def targetName = project + '-' + 'src-' + env.BRANCH_NAME + '.zip'
        def targetPath = ''
        def targetFullPath = ''

        switch(env.BRANCH_NAME) {
            case 'master':
                targetCredential = project + '-vm-product-credential'
                targetUsername = env.USERNAME
                targetPath = '/home/' + targetUsername + '/product/' + project + '-frontend'
                targetFullPath = targetPath + '/' + targetName
                targetAddress = env.PRODUCT_SERVER
                targetSSH = targetUsername + '@' + targetAddress
                break;
            case 'develop':
                targetCredential = project + '-vm-staging-credential'
                targetUsername = env.USERNAME
                targetPath = '/home/' + targetUsername + '/staging/' + project + '-frontend'
                targetFullPath = targetPath + '/' + targetName
                targetAddress = env.STAGING_SERVER
                targetSSH = targetUsername + '@' + targetAddress
                break;
            default:
                break;
        }

        stage('Clone Source Code') {
            checkout scm
        }

        stage('Prepare Source Code') {
            sh 'git archive -v -o ' + sourceFullPath + ' HEAD'
        }

        stage('Transfer Source Code') {
            sshagent (credentials: [targetCredential]) {                          
                sh 'ssh -o StrictHostKeyChecking=no ' + targetSSH + ' uname -a'
                sh 'ssh ' + targetSSH +' mkdir -p ' + targetPath
                sh 'ssh ' + targetSSH +' rm -rf ' + targetFullPath
                sh 'scp -r ' + sourceFullPath + ' ' + targetSSH +':' + targetFullPath
                sh 'ssh ' + targetSSH + ' unzip -o ' + targetFullPath + ' -d ' + targetPath
            }
        }

        stage('Stop docker') {
            sshagent (credentials: [targetCredential]) {
                sh 'ssh -o StrictHostKeyChecking=no ' + targetSSH + ' uname -a'
                sh 'ssh ' + targetSSH + ' /bin/sh -c \'cd \$pwd && bash -l && cd ' + targetPath + ' && docker stop ' + project + '-frontend || true && docker rm ' + project + '-frontend || true\''
            }
        }

        stage('Build docker') {
            sshagent (credentials: [targetCredential]) {
                sh 'ssh -o StrictHostKeyChecking=no ' + targetSSH + ' uname -a'
                sh 'ssh ' + targetSSH + ' /bin/sh -c \'cd \$pwd && bash -l && cd ' + targetPath + ' && docker build . -t ' + project + '-frontend\''
            }
        }

        stage('Run docker') {
            sshagent (credentials: [targetCredential]) {
                sh 'ssh -o StrictHostKeyChecking=no ' + targetSSH + ' uname -a'
                sh 'ssh ' + targetSSH + ' /bin/sh -c \'cd \$pwd && bash -l && cd ' + targetPath + ' && docker run -p 80:80 -d --name ' + project + '-frontend ' + project + '-frontend \''
            }
        }

        stage('Cleanup') {
            sshagent (credentials: [targetCredential]) {
                sh 'ssh -o StrictHostKeyChecking=no ' + targetSSH + ' uname -a'
                sh 'ssh ' + targetSSH + ' rm -f ' + targetFullPath
                sh 'rm -f ' + sourceFullPath
            }            
        }
    } catch (e) {
        currentBuild.result = "FAILED"
    } 
}
