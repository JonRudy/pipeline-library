#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput
import java.net.URLEncoder

def call(Email) {
  sh "env"
  echo Email
  def email = java.net.URLEncoder.encode(Email, "UTF-8")
  def param = "email=${email}"
  def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/x-www-form-urlencoded\" --data \'${param}\' ${env.SLACK_WEBHOOK_URL}/api/users.lookupByEmail").trim() 
  echo m
  def json = readJSON text: m

  return json
}
