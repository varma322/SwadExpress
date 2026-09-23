pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    environment {
        APP_NAME = 'food-delivery-backend'
        DOCKER_IMAGE = 'food-delivery-microservices:latest'
    }

    stages {
        stage('Checkout & Environment Inspection') {
            steps {
                echo 'Checking out source repository...'
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Build & Unit Tests') {
            steps {
                dir('backend') {
                    echo 'Compiling Java Spring Boot 4 microservice codebase...'
                    sh './mvnw clean compile'
                }
            }
        }

        stage('Automated Integration Testing (10 Use Cases)') {
            steps {
                dir('backend') {
                    echo 'Running 10/10 automated microservice integration test suite...'
                    sh './mvnw test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package Application') {
            steps {
                dir('backend') {
                    echo 'Building executable Spring Boot fat JAR...'
                    sh './mvnw package -DskipTests'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Containerization') {
            steps {
                echo 'Building production multi-stage Docker container...'
                sh 'docker build -t ${DOCKER_IMAGE} backend/'
            }
        }

        stage('Deploy to Staging') {
            steps {
                echo 'Deploying microservices and monitoring stack via Docker Compose...'
                sh 'docker compose up -d --build'
            }
        }
    }

    post {
        success {
            echo 'Food Delivery Microservices CI/CD Pipeline completed successfully!'
        }
        failure {
            echo 'Build or test verification failed. Please inspect build logs.'
        }
    }
}
