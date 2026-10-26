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

        stage('质量门禁判断 Quality Gate') {
            steps {
                script {
                    // 获取allure根目录，拼接allure.bat完整路径，规避Windows内置节点PATH注入bug
                    def allureHome = tool 'Allure2'
                    def allureBat = "${allureHome}\\bin\\allure.bat"

                    bat """
                        chcp 65001
                        "${allureBat}" calculateResults allure-results --json=gate-result.json
                    """

                    // 读取allure输出的json统计结果
                    def gateJson = readJSON file: 'gate-result.json'
                    def totalCount = gateJson.statistic.total
                    def passedCount = gateJson.statistic.passed
                    def failedCount = gateJson.statistic.failed
                    def skippedCount = gateJson.statistic.skipped
                    def brokenCount = gateJson.statistic.broken

                    echo ">>> Allure统计：总数=${totalCount}, 通过=${passedCount}, 失败=${failedCount}, 跳过=${skippedCount}, 异常=${brokenCount}"

                    if (env.runSuite == 'smoke') {
                        if (failedCount > 0 || brokenCount > 0) {
                            error "❌ Quality Gate不通过：Smoke套件不允许失败/异常！失败=${failedCount},异常=${brokenCount}"
                        } else {
                            echo "✅ Quality Gate 通过：Smoke套件全部用例执行成功"
                        }
                    } else if (env.runSuite == 'regression') {
                        // regression允许最多2个失败（broken也计入阻断）
                        if (failedCount + brokenCount > 2) {
                            error "❌ Quality Gate不通过：Regression失败+异常超过阈值2！失败=${failedCount},异常=${brokenCount}"
                        } else {
                            echo "✅ Quality Gate 通过：Regression失败+异常数量在阈值内"
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
    