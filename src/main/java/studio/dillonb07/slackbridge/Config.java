package studio.dillonb07.slackbridge;

import static studio.dillonb07.slackbridge.Slackbridge.VERSION;


public class Config {
    public String latestVersion = VERSION;
    public long latestCheckTime = 0;

    
    public String slackBotToken = "";
    public String slackAppToken = "";
        
    public String relayChannelId = "";
        
    public String[] adminUserIds = new String[0];
    
    public String infoCommand = "/slackbridge-info";
        
    public String avatarApi = "https://mc-heads.net/avatar/{player_uuid}.png";
    
}