package studio.dillonb07.slackbridge.slack;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;

import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.*;
import com.slack.api.socket_mode.SocketModeClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import studio.dillonb07.slackbridge.Slackbridge;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.System.in;

public class SlackApp {
    private static App app;
    public static void init() throws Exception {
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
                var username = user.getProfile().getDisplayName();
                sendMinecraftMessage(event.getText(), username);
            } else {
                Slackbridge.LOGGER.error("User not found for ID: " + userId);
            }
            
            return ctx.ack();
        });

        app.event(MessageChannelJoinEvent.class, (req, ctx) -> ctx.ack());
        app.event(MessageFileShareEvent.class, (req, ctx) -> ctx.ack());
        app.event(MessageChangedEvent.class, (req, ctx) -> ctx.ack());
        app.event(MessageDeletedEvent.class, (req, ctx) -> ctx.ack());
        
        app.command(Slackbridge.CONFIG.infoCommand, (req, ctx) -> {
            StringBuilder message = new StringBuilder();
            List<ServerPlayerEntity> onlinePlayers =
                    Slackbridge.serverInstance.getPlayerManager().getPlayerList();
            
            message.append("*Online players (").append(onlinePlayers.size()).append("/").append(Slackbridge.serverInstance.getMaxPlayerCount()).append("):*\n");
            for (ServerPlayerEntity player : onlinePlayers) {
                message.append("- [").append(player.networkHandler.getLatency()).append("ms] ").append(Objects.requireNonNull(player.getDisplayName()).getString()).append("\n");
            }

            double mspt =
                    ((double) Slackbridge.serverInstance.getAverageNanosPerTick()) / 1000000;

            ServerTickManager manager = Slackbridge.serverInstance.getTickManager();
            double tps = 1000.0D / Math.max(manager.isSprinting() ? 0.0 : manager.getMillisPerTick(), mspt);
            if (manager.isFrozen()) {
                tps = 0;
            }
            
            message.append("*Server TPS:*\n").append(String.format("%.2f", tps))
                    .append("\n")
                    .append("*Server MSPT:*\n").append(String.format("%.2f", mspt))
                    .append("\n")
                    .append("*Server used memory:*\n")
                    .append((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024)
                    .append("MB / ")
                    .append(Runtime.getRuntime().totalMemory() / 1024 / 1024)
                    .append("MB\n");
            
            ctx.client().chatPostMessage(r -> r
                    .channel(Slackbridge.CONFIG.relayChannelId)
                    .text(message.toString())
                    .parse("none")
            );
            return ctx.ack();
        });
        
        SocketModeApp socketModeApp = new SocketModeApp(appToken,
                SocketModeClient.Backend.JavaWebSocket, app);
        socketModeApp.startAsync();
        
    }

    private static void sendMinecraftMessage(String text, String user) {
        MinecraftServer server = Slackbridge.serverInstance;
        if (server != null) {
            Text message = Text.literal("[Slack] <" + user + "> " + text);
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
