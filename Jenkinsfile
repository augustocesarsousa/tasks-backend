pipeline {
    agent any
    stages {
        stage ('Buld Backend') {
            steps {
                sh 'mvn clean package -DskipTest=true'
            }
        }
    }
}