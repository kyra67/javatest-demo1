pipeline {
    agent any
    
    options {
    	timestamps()
    }
	
    tools {
        jdk 'JDK21'           // Jenkins 里配置的 JDK 名称
        maven 'Maven3'        // Jenkins 里配置的 Maven 名称
        allure 'Allure2'      // 对应全局工具配置里的Allure名称
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
            		if (env.BRANCH_NAME == "main") {
            			runSuite = "smoke"
            			echo ">>> 当前是main分支，强制使用smoke套件，忽略页面选择参数"
            		} else {
            			runSuite = params.suite
            			echo ">>> 当前分支：${env.BRANCH_NAME}，使用选中套件：${runSuite}"
            		}
            	}
            	// 把套件名存入环境变量，后面stage/post都可以读取
            	env.runSuite = runSuite
            	
            	// 执行mvn，returnStatus捕获返回码，测试失败不会直接终止流水线
            	def exitCode = bat returnStatus: true, script: """chcp 65001 mvn clean test - DsuiteXmlFile=test-${runSuite}.xml"""
            	
            	// 保存mvn执行结果到环境变量
            	evn.MVN_EXIT_CODE = ecitCode
            }
        }
        
    }
    
    stage('质量门禁判断') {
    	steps {
    		script {
    			// 读取allure结果统计失败用例数量
    			// allure命令读取结果，返回json，解析失败用例数
    			def allureResult = allure calculateResults "allure-results"
    			def failedCount = allureResult.failed
    			echo ">>> 测试失败用例数量：${failedCount}"
    			
    			// 质量门禁规则
    			if(env.runSuite == "smoke") {
    				if(failedCount > 0) {
    					error("❌ Smoke套件不允许任何用例失败，失败数量：${failedCount}，流水线终止！")
    				}
    			} else if (env.runSuite == "regression") {
    				// 回归套件最多允许2个失败
    				if(failedCount > 2) {
    					error("❌ Regression套件失败超过阈值！允许最多2个，实际失败：${failedCount}，流水线终止！")
    				} else {
    					echo "✅ Regression套件失败数量在允许阈值内，继续通过"
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