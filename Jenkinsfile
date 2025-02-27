pipeline {
    agent any
    stages {
        stage ('Build Backend') {
            steps {
                sh 'mvn clean package -DskipTest=true'
            }
        }
        stage ('Sonar Analysis') {
            environment {
                scannerHome = tool 'SONAR_SCANNER'
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
                    sh "${scannerHome}/bin/sonar-scanner -e \
                    -Dsonar.projectKey=DeplyBack \
                    -Dsonar.host.url=http://${SERVER_ADDRESS}:9000 \
                    -Dsonar.login=5884034a5bb78728e5259e914b698b97a61bf4f5 \
                    -Dsonar.java.binaries=target \
                    -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
                }
            }
        }
        stage ('Quality Gate') {
            steps {
                sleep(15)
                timeout(time: 1, unit: 'MINUTES'){
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage ('Deploy Backend') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat_login', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
        stage ('API Tests') {
            steps {
                dir ('api-test') {
                    git credentialsId: 'githut_login', url: 'https://github.com/augustocesarsousa/tasks-backend'
                    sh 'mvn test'
                }
            }
        }
        stage ('Deploy Frontend') {
            steps {
                dir ('frontend') {
                    git credentialsId: 'githut_login', url: 'https://github.com/augustocesarsousa/tasks-frontend'
                    sh 'mvn clean package'
                    deploy adapters: [tomcat8(credentialsId: 'tomcat_login', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Tests') {
            steps {
                dir ('functional-test') {
                    git branch: 'main', credentialsId: 'githut_login', url: 'https://github.com/augustocesarsousa/tasks-functional-tests'
                    sh 'mvn test'
                }
            }
        }
        stage ('Deploy Prod') {
            steps {
                sh 'docker-compose build'
                sh 'docker-compose up -d'
            }
        }
        stage ('Health Check') {
            steps {
                sleep(30)
                dir ('functional-test') {
                    sh 'mvn verify -Dskip.surefire.tests'
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: '/target/surefire-reports/*.xml, api-test/target/surefire-reports/*.xml, functional-test/target/surefire-reports/*.xml, functional-test/target/failsafe-reports/*.xml'
            archiveArtifacts artifacts: '/target/tasks-backend.war, forntend/target/tasks.war', followSymlinks: false, onlyIfSuccessful: true
        }
        unsuccessful {
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build $BUILD_NUMBER has failed', to: 'augusto.unip.cc@gmail.com'
        }
        fixed {
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build is fine!!!', to: 'augusto.unip.cc@gmail.com'
        }
    }
}