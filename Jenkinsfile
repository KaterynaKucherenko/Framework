node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    withSonarQubeEnv() {
      sh "./gradlew sonar"
    }
  }
}
pipeline {
    agent any

    tools {
        // Укажите используемую версию Gradle
        gradle 'Gradle'
    }

    environment {
        // Настройка переменной среды для SonarQube
        SONARQUBE_SCANNER_HOME = tool 'SonarQube Scanner'
    }

    stages {
        stage('Checkout') {
            steps {
                // Проверка исходного кода из вашего репозитория
                git url: 'https://github.com/KaterynaKucherenko/stage3-module4-task.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                // Выполнение сборки проекта с использованием Gradle
                sh 'gradle clean build'
            }
        }

        stage('Code Analysis') {
            steps {
                // Запуск SonarQube анализа
                withSonarQubeEnv('SonarQube Server') {
                    sh 'gradle sonarqube'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                // Ожидание прохождения SonarQube Quality Gate
                waitForQualityGate abortPipeline: true
            }
        }

        stage('Test') {
            steps {
                // Запуск тестов и генерация отчетов по покрытию кода
                sh 'gradle test jacocoTestReport'
            }
        }
    }

    post {
        always {
            // Публикация отчетов JaCoCo
            jacoco execPattern: '/build/jacoco/*.exec', classPattern: '/build/classes', sourcePattern: '/src/main/java', inclusionPattern: '/*.class', exclusionPattern: '/build//*.class'
        }
        failure {
            // Уведомление в случае сбоя сборки
            mail to: 'estorskaya@gmail.com',
                 subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                 body: "Something went wrong with ${env.JOB_NAME}."
        }
    }
}