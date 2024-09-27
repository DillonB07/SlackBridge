package studio.dillonb07.slackbridge.slack;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;

import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.MessageEvent;
import com.slack.api.socket_mode.SocketModeClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import studio.dillonb07.slackbridge.Slackbridge;

import java.io.IOException;

public class SlackApp {
    private static App app;
    public static void main() throws Exception {
        String botToken = Slackbridge.CONFIG.slackBotToken;
        String appToken = Slackbridge.CONFIG.slackAppToken;
        
        app = new App(AppConfig.builder().singleTeamBotToken(botToken).build());
        
        app.event(MessageEvent.class, (req, ctx) -> {
            var event = req.getEvent();
            
            if (event.getSubtype() != null) {
                return ctx.ack();
            }
            var userId = event.getUser();
            var userInfoResponse = ctx.client().usersInfo(r -> r.user(userId));
            var user = userInfoResponse.getUser();

            if (user != null) {
                var username = user.getRealName();
                sendMinecraftMessage(event.getText(), username);
            } else {
                Slackbridge.LOGGER.error("User not found for ID: " + userId);
            }
            
            return ctx.ack();
        });
        
        SocketModeApp socketModeApp = new SocketModeApp(appToken,
                SocketModeClient.Backend.JavaWebSocket, app);
        socketModeApp.startAsync();
        
    }

    private static void sendMinecraftMessage(String text, String user) {
        MinecraftServer server = Slackbridge.serverInstance;
        if (server != null) {
            Text message = Text.literal("<Slack: " + user + "> " + text);
            server.getPlayerManager().broadcast(message, false);
        }
        
    }

    public static void sendChatMessage(String message, String username, String uuid) throws SlackApiException, IOException {
        app.client().chatPostMessage(r -> r
                .channel(Slackbridge.CONFIG.relayChannelId)
                .text(message)
                .username(username)
                .iconUrl(Slackbridge.CONFIG.avatarApi.replace("{player_uuid}", uuid))
        );
    }
    
    public static void sendSlackMessage(String message) throws SlackApiException, IOException {
        app.client().chatPostMessage(r -> r
                .channel(Slackbridge.CONFIG.relayChannelId)
                .text(message)
        );
    }
    
}
