

pipeline {
    agent {
        label 'master'
    }

    tools {
        maven 'maven_3.9.6'
        jdk 'jdk21'
    }
    
     environment {
        DOCKER_REGISTRY = 'kshdevbuild.ksh.hu:8082'
        APP_NAME = 'maja-worklist'
        IMAGE_NAME = 'maja/worklist'
        APP_VERSION = readMavenPom().getParent().getVersion()
        APP_PORT = 9002
        APP_DIR = "/data/${APP_NAME}"
        APP_ENVIRONMENT = "dev"
        LOG_DIR = '/data/logs/maja-worklist'
     }
    
    options {
        timeout(time: 5, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
        gitLabConnection('gitlab')
    }
    
    triggers {
        gitlab(
            triggerOnPush: true,
            triggerOnMergeRequest: false,
            triggerOpenMergeRequestOnPush: "never",
            triggerOnNoteRequest: true,
            noteRegex: "Jenkins please retry a build",
            skipWorkInProgressMergeRequest: true,
            ciSkip: false,
            setBuildDescription: true,
            addNoteOnMergeRequest: true,
            addCiMessage: true,
            addVoteOnMergeRequest: true,
            acceptMergeRequestOnSuccess: false,
            branchFilterType: "All",
            secretToken: "63cc4bfd470cfa825a04f177ad63ff4c")
    }

    stages {
   
        stage('Build') {
            steps {
                withSonarQubeEnv('SonarQube'){
                    sh 'mvn -s /devtools/settings.xml clean install sonar:sonar'
                }                
            }  
        }
        
        stage('Code analysis'){
        
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }  
        }
        
        stage ('Dockerize'){
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
                sh "docker tag ${IMAGE_NAME} ${DOCKER_REGISTRY}/${IMAGE_NAME}:${APP_VERSION}"
                withCredentials([usernamePassword(credentialsId: 'f8f93c1b-4748-41f2-bbfe-dfe13600225f', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh "docker login -u ${USERNAME} -p ${PASSWORD} ${DOCKER_REGISTRY}"
                    sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${APP_VERSION}"
                    script {
                    	if ( "${APP_VERSION}".contains("SNAPSHOT") ) {
                    		sh "docker tag ${IMAGE_NAME} ${DOCKER_REGISTRY}/${IMAGE_NAME}:snapshot"
                    		sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:snapshot"
                    		sh "docker rmi ${DOCKER_REGISTRY}/${IMAGE_NAME}:snapshot"                                        
                    	}else {
                        	sh "docker tag ${IMAGE_NAME} ${DOCKER_REGISTRY}/${IMAGE_NAME}:release"
                        	sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:release"
                        	sh "docker rmi ${DOCKER_REGISTRY}/${IMAGE_NAME}:release"
                    	}
                	}
                	sh "docker rmi ${DOCKER_REGISTRY}/${IMAGE_NAME}:${APP_VERSION}"
                	sh "docker rmi ${IMAGE_NAME}"
                }
            }
        }
        
        stage ('Deploy') {
            agent  {
                node {
                    label 'idgs-app'
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'f8f93c1b-4748-41f2-bbfe-dfe13600225f', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        
                    sh "docker login -u ${USERNAME} -p ${PASSWORD} ${DOCKER_REGISTRY}"
                    sh '''#!/bin/bash
                        set -e
                        if [ 1$(docker ps -a --filter name=${APP_NAME} --format "{{ .Names }}" | grep -x ${APP_NAME}) == 1${APP_NAME} ]
                        then
                            echo "Remove running container: ${APP_NAME}"
                            docker stop ${APP_NAME}
                            docker rm ${APP_NAME}
                        else
                            echo "Container was not removed"
                        fi
                    '''
                    sh "docker pull ${DOCKER_REGISTRY}/${IMAGE_NAME}:${APP_VERSION}"
                    sh '''docker run -itd \
                      --name ${APP_NAME} \
                      --hostname ${APP_NAME} \
                      -p ${APP_PORT}:8082 \
                      --log-opt max-size=10m \
                      --log-opt max-file=3 \
                      -v ${APP_DIR}/application.yml:/app/config/application-${APP_ENVIRONMENT}.yml \
                      -v ${APP_DIR}/logback.xml:/app/logback.xml \
                      -v ${LOG_DIR}:/app/logs \
                      -e TZ=Europe/Budapest \
                      -e spring.profiles.active=${APP_ENVIRONMENT} \
                      --restart unless-stopped \
                      ${DOCKER_REGISTRY}/${IMAGE_NAME}:${APP_VERSION} '''
                }
            }
    	}
  
    }
      
 
    
}
 