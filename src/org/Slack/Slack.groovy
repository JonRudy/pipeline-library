#!/usr/bin/env groovy

package org.Slack
import groovy.json.JsonOutput



class Slack {

  public Slack() { /* empty */ }

  def sendPipelineInfo(body) {
    def attachments = []
    def pipelineTitle = [
      title: "1 new commit to ${body.jobName}",
      title_link: "${body.title_link}",
      color: "primary",
      text: "build started by\n${body.author}",
      "mrkdwn_in": ["fields"],
      author_name: "{body.user}",
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
    ]

    attachments.add(pipelineTitle)
    for (int i = 0; i < body.stageNames.size(); i++){
      def stage = [
        color: "primary",
        "text": ":not_started: ${body.stageNames[i]}: Not started"
      ]
      attachments.add(stage)
    }
    def message = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: attachments
    ])
    return message

  }

  def sendStageRunning(Message, channel, name, ts, stageNumber, pipelineSize) {
    def attachments = []
    for (int i = 0; i < stageNumber; i++)
      attachments.add(Message.message.attachments[i])
    def stage = [
      color: "#cccc00",
      "text": ":in_progress: ${name}: running"
    ]
    attachments.add(stage)
    for (int i = stageNumber+1; i < pipelineSize; i++)
      attachments.add(Message.message.attachments[i])

    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: attachments
    ])

    return payload
  }

  def sendStageSuccess(Message, channel, name, ts, stageNumber, pipelineSize, String s = null) {
    def attachments = []
    for (int i = 0; i < stageNumber; i++)
      attachments.add(Message.message.attachments[i])

    if (s == null){
      def stage = [
        color: "#45B254",
        "text": ":passed: ${name}: passed!"
      ]
      attachments.add(stage)
    }
    else {
      def stage = [
        color: "#45B254",
        "mrkdwn_in": ["author_name"],
        "text": ":passed: ${name}: ${s}"
      ]
      attachments.add(stage)
    }
    for (int i = stageNumber+1; i < pipelineSize; i++)
      attachments.add(Message.message.attachments[i])
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: attachments
    ])
    return payload
  }
  def sendPipelineFailure(Message, channel, name, ts, stageNumber, pipelineSize, log) {
    def attachments = []
    for (int i = 0; i < stageNumber; i++)
      attachments.add(Message.message.attachments[i])
    def stage = [
      color: "danger",
      "text": ":failed: ${name}: failed```${log}```",
      "mrkdwn_in": ["text"]
    ]
    attachments.add(stage)
    for (int i = stageNumber+1; i < pipelineSize; i++)
      attachments.add(Message.message.attachments[i])
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: attachments  
    ])

    return payload
  }
}

