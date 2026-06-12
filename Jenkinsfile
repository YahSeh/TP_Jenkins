pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK 17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation du projet...'
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Test & Code Coverage') {
            steps {
                echo 'Execution des tests et generation du rapport JaCoCo...'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Analyse de la qualite du code avec SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=bad-practices-app'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Creation du JAR executable...'
                sh 'mvn clean package -DskipTests'
            }
        }

        //stage('Package & Docker Build') {
        //    steps {
        //        echo 'Creation du JAR executable et de l image Docker...'
        //        sh 'mvn clean package -DskipTests'
        //        sh 'docker build -t epsi/bad-practices-app:latest .'
        //    }
        //}
    }
}
