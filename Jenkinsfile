pipeline {
    agent any
    environment {
            WS = "${WORKSPACE}"
            IMAGE_VERSION = "v1.0"
            }
    stages {
        stage('环境检查'){
            steps {
                sh 'printenv'
                echo "正在检测基本信息"
                sh 'java -version'
                sh 'git --version'
                sh 'docker version'
                sh 'pwd && ls -alh'
            }
        }
      stage('Maven编译打包') {
                  agent {
                       docker {
                             image 'maven:3-alpine'
                             args '-v /var/jenkins_home/appconfig/maven/.m2:/root/.m2'
                          }
                       }
                  steps {
                       sh 'pwd && ls -alh'
                       sh 'mvn -v'
                       //打包，jar.。默认是从maven中央仓库下载。
                       //jenkins目录+容器目录；-s指定容器内位置
                       sh "echo 默认的工作目录：${WS}"
                       //每一行指令都是基于当前环境信息。和上下指令无关
                       sh 'cd ${WS} && mvn clean package -s "/var/jenkins_home/appconfig/maven/settings.xml"  -Dmaven.test.skip=true '
                       sh 'cd ${WS}/target && ls -alh'
                  }
              }

        stage('制作Docker镜像') {
                    steps {
                        sh 'echo 制作Docker镜像'
                        sh 'docker version'
                        sh 'pwd && ls -alh'
                        sh 'cd ${WS} && ls -alh'
                        sh 'cd ${WS} && docker build -t java-book-back .'
                    }
                }

        stage('推送镜像到DockerHub') {
             steps {
                 sh 'echo 推送镜像到DockerHub'
//                  sh '1H2G 贷款太小了 哥们推不动了'
//                  sh 'docker login -u sometingpluto -p chx200205173214'
//                  sh 'docker tag java-book-back:latest sometingpluto/java-book-back:latest'
//                  sh 'docker push sometingpluto/java-book-back:latest'
             }
        }
       stage('部署应用') {
                            steps {
                                    echo "部署Docker image"
                                    sh 'docker rm -f java-book-back-dev'
                                    sh 'docker run -d -p 8886:8088 --name java-book-back-dev java-book-back'
                           }
                    }

    }
}