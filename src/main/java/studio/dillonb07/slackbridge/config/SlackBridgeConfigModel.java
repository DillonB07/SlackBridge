package studio.dillonb07.slackbridge.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "slackbridge")
@Config(name = "slackbridge", wrapperName = "SlackBridgeConfig")
public class SlackBridgeConfigModel {
    public String channelId;

    public String botToken;
    public String appToken;
    public String signingSecret;

    public String[] admins;
    
    public Integer port;
}
