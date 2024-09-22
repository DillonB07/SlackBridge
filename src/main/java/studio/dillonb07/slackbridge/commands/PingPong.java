package studio.dillonb07.slackbridge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

public class PingPong {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ping_pong").then(CommandManager.argument(
                "message", StringArgumentType.greedyString()).executes(context -> {
                    String message = StringUtils.normalizeSpace(StringArgumentType.getString(context, "message"));
                    context.getSource().sendFeedback(() -> Text.literal(String.format("Pong! %s", message)),
                            false);
            return 1;
        })));
    }
}
