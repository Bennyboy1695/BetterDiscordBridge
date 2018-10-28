package io.github.bennyboy1695.betterdiscordbridge.Commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import net.dv8tion.jda.core.entities.Game;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public class CommandGameStatus implements Command {

    private BetterDiscordBridge instance;

    public CommandGameStatus(BetterDiscordBridge instance) {
        this.instance = instance;
    }
    @Override
    public void execute(@NonNull CommandSource source, String[] args) {
        if (source.hasPermission("betterdiscordbridge.command.gamestatus")) {
            if (args.length <= 1) {
                source.sendMessage(TextComponent.of("Invalid usage!").color(TextColor.RED));
                source.sendMessage(TextComponent.of("Usage: /gamestatus <playing|listening|watching> a Velocity server!").color(TextColor.RED));
                return;
            }
            //Easier way to get the args :P//
            String fullArgs;
            String str = Arrays.toString(args);
            fullArgs = str.substring(1, str.length()-1).replace(",", "");

                if (args[0].startsWith("Playing") || args[0].startsWith("playing")) {
                instance.getLogger().info(fullArgs);
                    instance.getJDA().getPresence().setGame(Game.playing(fullArgs.replace("playing", "").replace("Playing", "")));
                    source.sendMessage(TextComponent.of("Set bots status to: " + fullArgs, TextColor.GREEN));
                    instance.getConfig().getConfigNode().getNode("discord", "info", "status").setValue(fullArgs);
                    instance.getConfig().saveConfig();
                } else if (args[0].startsWith("Watching") || args[0].startsWith("watching")) {
                    instance.getJDA().getPresence().setGame(Game.watching(fullArgs.replace("watching", "").replace("Watching", "")));
                    source.sendMessage(TextComponent.of("Set bots status to: " + fullArgs, TextColor.GREEN));
                } else if (args[0].startsWith("Listening") || args[0].startsWith("listening")) {
                    instance.getJDA().getPresence().setGame(Game.listening(fullArgs.replace("listening", "").replace("Listening", "")));
                    source.sendMessage(TextComponent.of("Set bots status to: " + fullArgs, TextColor.GREEN));
                } else {
                    source.sendMessage(TextComponent.of("Invalid usage!").color(TextColor.RED));
                    source.sendMessage(TextComponent.of("Usage: /gamestatus <playing|listening|watching> a Velocity server!").color(TextColor.RED));
                }
        }
    }
}
