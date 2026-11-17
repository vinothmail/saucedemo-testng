pipeline {
    agent any   // the agent needs Java 11+, Maven and Google Chrome installed

    tools {
        maven 'Maven-3'   // names must match Manage Jenkins > Tools
        jdk   'JDK-17'
    }

    options {
        disableConcurrentBuilds()          // UAT is shared: never run two test runs at once
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    triggers {
        // Nightly run, Mon-Fri, around 1 AM. Timer builds run BOTH suites (see 'when' blocks).
        cron('H 1 * * 1-5')
    }

    parameters {
        booleanParam(name: 'RUN_SMOKE',      defaultValue: true,
                     description: 'Run the smoke suite (3 critical tests, a few minutes)')
        booleanParam(name: 'RUN_REGRESSION', defaultValue: false,
                     description: 'Run the full regression suite (all tests)')
    }

    stages {

        stage('Validate Selection') {
            steps {
                script {
                    boolean timer = currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause').size() > 0
                    if (!timer && !params.RUN_SMOKE && !params.RUN_REGRESSION) {
                        error('Nothing selected: tick RUN_SMOKE and/or RUN_REGRESSION.')
                    }
                    echo "Timer build: ${timer} | Smoke: ${params.RUN_SMOKE} | Regression: ${params.RUN_REGRESSION}"
                }
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Smoke Tests') {
            when {
                anyOf {
                    expression { return params.RUN_SMOKE }
                    triggeredBy 'TimerTrigger'
                }
            }
            steps {
                sh 'mvn -B clean test -DsuiteXmlFile=testng-smoke.xml -Dheadless=true'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
                }
            }
        }

        // If both are selected and smoke fails, regression is skipped (smoke acts as a gate).
        stage('Regression Tests') {
            when {
                anyOf {
                    expression { return params.RUN_REGRESSION }
                    triggeredBy 'TimerTrigger'
                }
            }
            steps {
                sh 'mvn -B clean test -DsuiteXmlFile=testng.xml -Dheadless=true'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
        }
        failure {
            echo 'Tests failed: block promotion to prod and notify the team.'
            // mail to: 'qa-team@yourcompany.com',
            //      subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //      body: "See ${env.BUILD_URL}"
        }
    }
}
