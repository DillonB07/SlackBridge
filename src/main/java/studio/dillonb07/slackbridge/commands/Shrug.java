package studio.dillonb07.slackbridge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.slack.api.methods.SlackApiException;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import studio.dillonb07.slackbridge.slack.SlackApp;

import java.io.IOException;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Shrug {
    static String raw = "¯\\_(ツ)_/¯";
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("shrug").then(argument("message",
                MessageArgumentType.message()).executes(context -> {
                    ServerCommandSource serverCommandSource = context.getSource();
                    PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
                    
                    MessageArgumentType.getSignedMessage(context, "message", signedMessage -> {
                        String message = signedMessage.getContent().getString() + " " + raw;
                        playerManager.broadcast(signedMessage.withUnsignedContent(Text.of(message)),
                                serverCommandSource, MessageType.params(MessageType.CHAT, serverCommandSource));
                        sendShrugToSlack(serverCommandSource, message);
                    });
                        return 1;

        })));
        dispatcher.register(literal("shrug").executes(context -> {
            ServerCommandSource serverCommandSource = context.getSource();
            PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
            
            playerManager.broadcast(SignedMessage.ofUnsigned(raw).withUnsignedContent(Text.of(raw)), serverCommandSource, MessageType.params(MessageType.CHAT, serverCommandSource));
            sendShrugToSlack(serverCommandSource, raw);
            return 1;
        }));
    }

    private static void sendShrugToSlack(ServerCommandSource serverCommandSource, String message) {
        String name = Objects.requireNonNull(Objects.requireNonNull(serverCommandSource.getPlayer()).getDisplayName()).getString();
        String uuid = Objects.requireNonNull(serverCommandSource.getPlayer()).getUuidAsString();
        try {
            SlackApp.sendChatMessage(message, name, uuid);
        } catch (SlackApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
