pipeline {
    agent any

    tools {
        jdk 'JDK11'           // Jenkins 里配置的 JDK 名称
        maven 'Maven3'        // Jenkins 里配置的 Maven 名称
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Smoke Only') {
            when {
                branch 'main'
            }
            steps {
                sh 'mvn test -Dgroups=smoke'
            }
        }
    }

    post {
        always {
            // JUnit 报告给 Jenkins 解析
            junit '**/target/surefire-reports/*.xml'
            // 归档 HTML 报告（如果有）
            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
        }
        success {
            echo '✅ 全部测试通过'
        }
        failure {
            echo '❌ 有测试失败，请检查'
        }
    }
}