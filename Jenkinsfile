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
                    def allResultFiles = findFiles glob: 'allure-results/*.json'
                    int failedCount = 0
                    int brokenCount = 0
                    int passedCount = 0
                    int skippedCount = 0

                    allResultFiles.each { fileItem ->
                        def caseJson = readJSON file: fileItem.path
                        def status = caseJson.status
                        switch(status) {
                            case 'failed':
                                failedCount++
                                break
                            case 'broken':
                                brokenCount++
                                break
                            case 'passed':
                                passedCount++
                                break
                            case 'skipped':
                                skippedCount++
                                break
                        }
                    }

                    echo ">>> Allure统计：passed=${passedCount}, failed=${failedCount}, broken=${brokenCount}, skipped=${skippedCount}"

                    if (env.runSuite == 'smoke') {
                        if (failedCount > 0 || brokenCount > 0) {
                            error "❌ Quality Gate不通过：Smoke套件不允许失败/异常！failed=${failedCount}, broken=${brokenCount}"
                        } else {
                            echo "✅ Quality Gate 通过：Smoke套件全部用例执行成功"
                        }
                    } else if (env.runSuite == 'regression') {
                        if (failedCount + brokenCount > 2) {
                            error "❌ Quality Gate不通过：Regression失败+异常总数超过阈值2！failed=${failedCount}, broken=${brokenCount}"
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
    