package studio.dillonb07.slackbridge;

import com.slack.api.methods.SlackApiException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.dillonb07.slackbridge.commands.Ping;
import studio.dillonb07.slackbridge.commands.PingPong;
import studio.dillonb07.slackbridge.config.ConfigManager;
import studio.dillonb07.slackbridge.slack.SlackApp;

import java.io.File;
import java.io.IOException;

import static studio.dillonb07.slackbridge.slack.SlackApp.sendChatMessage;
import static studio.dillonb07.slackbridge.slack.SlackApp.sendSlackMessage;

public class Slackbridge implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Slackbridge");
    
    public static final File CONFIG_FILE =
            new File(FabricLoader.getInstance().getConfigDir().toFile(), "slackbridge.json");
    public static final File BACKUP_CONFIG_FILE =
            new File(FabricLoader.getInstance().getConfigDir().toFile(), "slackbridge.backup.json");
    public static final String VERSION =
            FabricLoader.getInstance().getModContainer("slackbridge").orElseThrow().getMetadata().getVersion().getFriendlyString();
    public static Config CONFIG;

    public static MinecraftServer serverInstance;


    @Override
    public void onInitialize() {
        try {
            ConfigManager.init(false);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }

        LOGGER.info("Slackbridge v" + VERSION + " has been initialized!");
        
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
        
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            serverInstance = server;
            try {
                sendSlackMessage(":white_check_mark: Server has started! !");
            } catch (SlackApiException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            try {
                sendSlackMessage(":octagonal_sign: Server has stopped!");
            } catch (SlackApiException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            String textContent = message.getSignedContent();
            
            try {
                sendChatMessage(textContent, sender.getName().copyContentOnly().getLiteralString(), 
                        sender.getUuidAsString());
            } catch (SlackApiException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}