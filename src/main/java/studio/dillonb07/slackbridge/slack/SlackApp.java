package studio.dillonb07.slackbridge.slack;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;

import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.MessageEvent;
import com.slack.api.socket_mode.SocketModeClient;
import studio.dillonb07.slackbridge.Slackbridge;

public class SlackApp {
    public static void main() throws Exception {
        String botToken = Slackbridge.CONFIG.slackBotToken;
        String appToken = Slackbridge.CONFIG.slackAppToken;
        
        App app = new App(AppConfig.builder().singleTeamBotToken(botToken).build());
        
        app.event(MessageEvent.class, (req, ctx) -> {
            var event = req.getEvent();
            
            if (event.getText().contains("ping")) {
                ctx.say("pong");
            }
            return ctx.ack();
        });
        
        SocketModeApp socketModeApp = new SocketModeApp(appToken,
                SocketModeClient.Backend.JavaWebSocket, app);
        socketModeApp.startAsync();
        
    }
}
