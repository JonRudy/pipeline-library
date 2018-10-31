#!/usr/bin/env groovy

package org.Slack
import groovy.json.JsonOutput



class Slack {

  public Slack() { /* empty */ }

  def sendPipelineInfo(body) {
    def payloads = []
    def pipelineTitle = [[
      title: "${body.jobName}, build #${body.buildNumber}",
      title_link: "${body.title_link}",
      color: "primary",
      text: "build started by\n${body.author}",
      "mrkdwn_in": ["fields"],
      fields: [
        [
          title: "Branch",
          value: "${body.branch}",
          short: true
        ],
        [
          title: "Last Commit",
          value: "${body.message}",
          short: true
        ]
      ]
    ]]
    def title = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: pipelineTitle 
    ])
    payloads.add(title)

    def stages = []
    for (int i = 0; i < body.stageNames.size(); i++){
      def stage = [
        color: "primary",
        "author_name": "${body.stageNames[i]}: Not started",
        "author_icon": "https://github.com/liatrio/pipeline-library/blob/rich-slack/resources/grey-circle.jpeg?raw=true"
      ]
      stages.add(stage)
    }
    def stagesMessage = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages
    ])
    payloads.add(stagesMessage)

    return payloads

  }

  def sendStageRunning(messages, channel, name, ts, stageNumber, pipelineSize) {
    def attachments = []
    for (int i = 0; i < stageNumber; i++)
      attachments.add(messages[1].message.attachments[i])
    def stage = [
      color: "#cccc00",
      "author_name": "${name}: running",
      "author_icon": "https://github.com/liatrio/pipeline-library/blob/rich-slack/resources/pulsating-circle.gif?raw=true"
    ]
    attachments.add(stage)
    for (int i = stageNumber+1; i < pipelineSize; i++)
      attachments.add(messages[1].message.attachments[i])

    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: attachments
    ])

    return payload
  }

  def sendStageSuccess(messages, channel, name, ts, stageNumber, pipelineSize) {
    def attachments = []
    for (int i = 0; i < stageNumber; i++)
      attachments.add(messages[1].message.attachments[i])
    def stage = [
      color: "#45B254",
      "author_name": "${name}: passed!",
      "author_icon": "https://github.com/liatrio/pipeline-library/blob/rich-slack/resources/check-circle.png?raw=true"
    ]
    attachments.add(stage)
    for (int i = stageNumber+1; i < pipelineSize; i++)
      attachments.add(messages[1].message.attachments[i])
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: attachments
    ])
    return payload
  }
  def sendPipelineFailure(channel, name, ts, log) {
    def stage = [[
      color: "danger",
      "author_name": "${name}: failed",
      "mrkdwn_in": ["text"],
      "author_icon": "https://github.com/liatrio/pipeline-library/blob/rich-slack/resources/red-circle.png?raw=true",
      "text": "```${log}```"
    ]]
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stage  
    ])

    return payload
  }
}

