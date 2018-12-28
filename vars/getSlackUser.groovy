#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput
import java.net.URLEncoder

def call() {
  def email = ""
  if ("${env.BUILD_USER_EMAIL}" != "")
    email = "${env.BUILD_USER_EMAIL}"
  else if ("${env.CHANGE_AUTHOR_EMAIL}" != "")
    email = "${env.CHANGE_AUTHOR_EMAIL}"
  sh "env"
  def param = "email=${email}"
  def m = sh(returnStdout: true, script: "curl -G --silent -X GET -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/x-www-form-urlencoded\" --data-urlencode \"${param}\" ${env.SLACK_WEBHOOK_URL}/api/users.lookupByEmail").trim() 
  def json = readJSON text: m

  return json
}
