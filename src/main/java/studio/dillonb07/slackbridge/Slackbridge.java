package studio.dillonb07.slackbridge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import studio.dillonb07.slackbridge.commands.Ping;
import studio.dillonb07.slackbridge.commands.PingPong;
import studio.dillonb07.slackbridge.config.SlackBridgeConfig;
import studio.dillonb07.slackbridge.slack.SlackApp;

public class Slackbridge implements ModInitializer {
    public static final SlackBridgeConfig CONFIG = SlackBridgeConfig.createAndLoad();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> Ping.register(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> PingPong.register(dispatcher)));

        Thread slackBotThread = new Thread(() -> {
            try {
                SlackApp.main();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        slackBotThread.setName("Slackbridge Bot Thread");
        slackBotThread.setDaemon(true);
        slackBotThread.start();

        Identifier identifier = new Identifier("slackbridge", "chat_message");
        // Register a listener for chat messages
        ServerPlayNetworking.registerGlobalReceiver(Identifier.of("slackbridge", 
                "chat_message"), (server, player, handler, buf, sender) -> {
            String message = buf.readString(32767); // Specify the max length for the string
            server.execute(() -> {
                player.networkHandler.sendPacket(new GameMessageS2CPacket(Text.of(message), GameMessageS2CPacket.MessageType.CHAT));
            });
        });
    }
}