import groovy.json.JsonOutput

def call(requestParams) {
/*
    def requestBody = [
          "teamName": ${teamName},
          "appName": ${env.APP_NAME},
          "eventName": "state-change",
          "branch": "master",
          "state": "healthy",
          "priorDuration": 16343
    ]
 */
    requestParams.teamName = env.TEAM_NAME ? env.TEAM_NAME : env.ORG
    requestParams.appName = env.APP_NAME
    requestParams.branch = env.BRANCH_NAME
    requestParams.groupID = env.GROUP_ID
    requestParams.versionNumber = env.VERSION

    def requestBody = JsonOutput.toJson(requestParams)
    def url = "tcp://0.tcp.ngrok.io:13324"

    def response = httpRequest acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: requestBody, url: url
    println('Status: ' + response.status)
    println('Response: ' + response.content)
}
