pipeline {
    agent any
    stages {
        stage ('Just Test') {
            steps {
                sh "chmod +x -R ${env.WORKSPACE}"
                sh 'It is OK!'
            }
        }
    }
}