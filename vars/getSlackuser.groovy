#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(Email) {
  def param = "email=${Email.encodeURL()}"
  def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/x-www-form-urlencoded\" --data \'${param}\' ${env.SLACK_WEBHOOK_URL}/api/users.lookupByEmail").trim() 
  def json = readJSON text: m

  return json
}
