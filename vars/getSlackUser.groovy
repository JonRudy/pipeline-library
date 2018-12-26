#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput
import java.net.URLEncoder

def call(Email) {
  //echo Email
  //def email = java.net.URLEncoder.encode(Email, "UTF-8")
  //echo email
  //def param = "email=${email}"
  def param = "email=${Email}"
  def m = sh(returnStdout: true, script: "curl -G --silent -X GET -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/x-www-form-urlencoded\" --data-urlencode \"${param}\" ${env.SLACK_WEBHOOK_URL}/api/users.lookupByEmail").trim() 
  echo m
  def json = readJSON text: m

  return json
}
