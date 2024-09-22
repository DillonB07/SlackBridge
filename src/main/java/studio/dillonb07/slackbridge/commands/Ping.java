package studio.dillonb07.slackbridge.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Ping {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ping").executes(context -> {
            context.getSource().sendFeedback(() -> Text.literal("Pong!"), false);
            return 1;
        }));
    }
}
