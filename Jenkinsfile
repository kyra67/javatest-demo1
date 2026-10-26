pipeline {
    agent any
    
    options {
    	timestamps()
    }
	
    tools {
        jdk 'JDK21'           // Jenkins 里配置的 JDK 名称
        maven 'Maven3'        // Jenkins 里配置的 Maven 名称
        // allure 'Allure2'      // 对应全局工具配置里的Allure名称
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
					// 把套件名存入环境变量，后面stage/post都可以读取
            	    env.runSuite = runSuite

					// bat放到script里面，用returnStatus捕获退出码
					def mvnExitCode = bat(
						returnStatus: true,
						script: """
                            chcp 65001
                            mvn clean test -DsuiteXmlFile=test-${runSuite}.xml
                        """
					)
					// 保存mvn执行结果到环境变量
            	    env.MVN_EXIT_CODE = mvnExitCode.toString()
            	}
            }
        }

		stage('质量门禁判断') {
			steps {
				script {
					// 1. 调用allure命令输出统计json
					bat """
						chcp 65001
						allure calculateResults allure-results --json=gate-result.json
					"""

					// 2. 读取json文件，判断失败数
					def allureResult = readJson(file: 'gate-result.json')
					def failureCount = allurResult.failed
					def totalCount = allurResult.total
					def passedCount = allurResult.passed
					echo ">>> allure统计结果：总数=${totalCount}, 通过=${passedCount}, 失败=${failureCount}"

					// 门禁规则
					if(env.runSuite == 'smoke') {
						if(failureCount > 0) {
							error "❌ smoke套件有失败用例，失败数量：${failureCount}"
						}else{
							echo "✅ smoke套件全部通过, 0失败"
						}
					}else if(env.runSuite == 'regression') {
						if(failureCount > 2) {
							error "❌ regression套件失败用例超过5个，失败数量：${failureCount}"
						}else{
							echo "✅ regression套件失败用例数量在可接受范围内，失败数量：${failureCount}"
						}
					}
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