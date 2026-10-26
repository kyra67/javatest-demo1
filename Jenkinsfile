pipeline {
    agent any
    
    options {
        timestamps()
    }
    
    tools {
        jdk 'JDK21'         // Jenkins 全局工具配置的JDK名称
        maven 'Maven3'      // Jenkins 全局工具配置的Maven名称
        allure 'Allure2'
    }
    
    parameters {
        choice(name: 'suite', choices: ['smoke', 'regression'], description: '选择要跑的测试套件')
    }

    stages {
        stage('Checkout 拉取源码') {
            steps {
                checkout scm
            }
        }

        stage('Build & 执行测试') {
            steps {
                script {
                    def runSuite
                    if (env.BRANCH_NAME == "main") {
                        runSuite = "smoke"
                        echo ">>> 当前是main分支，强制使用smoke套件，忽略页面选择参数"
                    } else {
                        runSuite = params.suite
                        echo ">>> 当前分支：${env.BRANCH_NAME}，使用选中套件：${runSuite}"
                    }
                    env.runSuite = runSuite

                    def mvnExitCode = bat(
                        returnStatus: true,
                        script: """
                            chcp 65001
                            mvn clean test -DsuiteXmlFile=test-${runSuite}.xml
                        """
                    )
                    env.MVN_EXIT_CODE = mvnExitCode.toString()
                }
            }
        }

        stage('质量门禁判断') {
            steps {
                script {
                    bat """
                        chcp 65001
                        allure calculateResults allure-results --json=gate-result.json
                    """
                    def allureResult = readJson(file: 'gate-result.json')
                    def failureCount = allureResult.failed
                    def totalCount = allureResult.total
                    def passedCount = allureResult.passed
                    echo ">>> allure统计结果：总数=${totalCount}, 通过=${passedCount}, 失败=${failureCount}"

                    if(env.runSuite == 'smoke') {
                        if(failureCount > 0) {
                            error "❌ smoke套件有失败用例，失败数量：${failureCount}"
                        }else{
                            echo "✅ smoke套件全部通过, 0失败"
                        }
                    }else if(env.runSuite == 'regression') {
                        if(failureCount > 2) {
                            error "❌ regression套件失败用例超过2个，失败数量：${failureCount}"
                        }else{
                            echo "✅ regression套件失败用例数量在可接受范围内，失败数量：${failureCount}"
                        }
                    }
                }
            }
        }
    } // ====== 这个是 stages 的闭合！！所有stage都在它内部 ======

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
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
    