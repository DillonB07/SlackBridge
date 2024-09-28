# Slackbridge

Slackbridge is a Fabric Minecraft <-> Slack chat relay for Minecraft 1.21.



## Download

Slackbridge is available to download on:
- [Modrinth](https://modrinth.com/mod/slackbridge)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/slackbridge)
- [GitHub Releases](https://github.com/DillonB07/Slackbridge/releases)

## Features

- Relay
  - Relay messages from Slack to Minecraft (excludes threads)
  - Relay messages from Minecraft to Slack
  - Displays player join/leave messages in Slack
  - Displays death messages in Slack
  - Displays advancement messages in Slack
  - Displays server start/stop messages in Slack
- Slack Commands
  - `/slackbridge-info` - Displays online players and performance stats
- Minecraft Commands
  - `/shrug <message>` - Appends a shrug to the end of the message

## Setup

### Create a Slack App
- Go to [Slack API](https://api.slack.com/apps) and create a new app in your workspace using the 
  following manifest. You may want to change the bot name and the command `/slackbridge-info` to something else. **If you are using this in the Hack Club Slack workspace, you must change the 
  command name.**
```json
{
    "display_information": {
        "name": "SlackBridge"
    },
    "features": {
        "bot_user": {
            "display_name": "SlackBridge",
            "always_online": false
        },
        "slash_commands": [
            {
                "command": "/slackbridge-info",
                "description": "See current server status",
                "should_escape": false
            }
        ]
    },
    "oauth_config": {
        "scopes": {
            "bot": [
                "channels:history",
                "chat:write",
                "chat:write.customize",
                "users:read",
                "commands"
            ]
        }
    },
    "settings": {
        "event_subscriptions": {
            "bot_events": [
                "message.channels"
            ]
        },
        "interactivity": {
            "is_enabled": true
        },
        "org_deploy_enabled": false,
        "socket_mode_enabled": true,
        "token_rotation_enabled": false
    }
}
```
- Once you have created the app, go to the "OAuth & Permissions" page and install the app to your workspace.
- Note down the Bot User OAuth Token.
- Go to the Basic Information page and create an app token. Note down the app token.
- In Slack, invite the bot to the channel you want to relay messages to.
- Right click on the channel and click "Copy Link". Note down the channel ID from the link. (It should look like `C01A2B3C4D5`)
- That's it on the Slack side! Let's get your server setup now.

### Setup the Mod
- Install the mod on your server by adding the jar to the mods folder.
- Restart the server to generate the config file.
- Open the config file and fill in the following fields:
  - `slackBotToken` - The Bot User OAuth Token
  - `slackAppToken` - The App Token
  - `relayChannelId` - The Channel ID
  - `infoCommand` - The command name you set in the manifest. This will default to 
    `/slackbridge-info` - if you didn't change the manifest, you can leave this alone.
- Restart the server again and you should be good to go!
