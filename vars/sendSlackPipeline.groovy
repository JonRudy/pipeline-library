#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call() {

  Slack slack = new Slack()
  def jenkinsfile = readFile file: "Jenkinsfile"
  def stageNames = getStageNames(jenkinsfile)

  def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
  def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
  def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 
  def user = getSlackUser("${env.CHANGE_AUTHOR_EMAIL}")
  
  def slackMessage = slack.sendPipelineInfo([
      slackURL: "${env.SLACK_WEBHOOK_URL}",
      jobName: "${scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[4].split("\\.")[0]}",
      stageNames: stageNames,
      buildNumber: "${env.BUILD_NUMBER}",
      branch: "${env.BRANCH_NAME}",
      //title_link: "${env.BUILD_URL}",
      title_link: "${scm.getUserRemoteConfigs()[0].getUrl()}",
      author: "${author}",
      message: "${message}",
      channel: "${env.SLACK_ROOM}",
      user: "${author}"
      //user: "${user.user.name}"
    ])
  def response = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${slackMessage}\' ${env.SLACK_WEBHOOK_URL}/api/chat.postMessage").trim()
  def responseJSON = readJSON text: response
  //def response = httpRequest validResponseCodes: '409,201,200', 
  //                  customHeaders: [[name: "Authorization", value: "Bearer ${env.SLACK_TOKEN}"]], 
  //                  consoleLogResponseBody: true, 
  //                  acceptType: 'APPLICATION_JSON', 
  //                  contentType: 'APPLICATION_JSON', 
  //                  httpMode: 'POST', 
  //                  requestBody: "${slackMessage}", 
  //                  url: "${env.SLACK_WEBHOOK_URL}/api/chat.postMessage"
  //def responseJSON = readJSON text: response.content
  return responseJSON 
}

def getStageNames(jenkinsfile){

  def names = []
  def lines = jenkinsfile.readLines()

  for (int i = 0; i < lines.size(); i++){
    def line = lines[i]
    if (line.trim().size() == 0){}
    else {
      if (line.contains("stage(\'")){
        String [] tokens = line.split("\'");
        String stage = tokens[1]; 
        names.add(stage)
      }
      else if (line.contains("stage(\"")){
        String [] tokens = line.split("\"");
        String stage = tokens[1]; 
        names.add(stage)
      }
    }
  }
  return names
}
