package io.github.bennyboy1695.betterdiscordbridge.Listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import net.kyori.text.TextComponent;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VelocityEventListener {

    private BetterDiscordBridge instance;

    public VelocityEventListener(BetterDiscordBridge instance) {
        this.instance = instance;
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        String serverName = event.getPlayer().getCurrentServer().get().getServerInfo().getName();
        instance.getProxyServer().getConsoleCommandSource().sendMessage(TextComponent.of(serverName));
        CommentedConfigurationNode configNode = instance.getConfig().configNode;
        long currentChannelId = instance.getConfig().getChannels(serverName);
        if (currentChannelId != -1) {
            String message = instance.getConfig().getFormats("discord_to");
            StringBuilder emessage = new StringBuilder(event.getMessage());

            // Simply as fuck, but working :)
            if(instance.getConfig().configNode.getNode("format", "discord", "to", "escapeDiscordFormat").getBoolean()) {
                // Italic
                int lastIndex = 0;

                while (lastIndex != -1) {

                    lastIndex = emessage.indexOf("&o", lastIndex);

                    if (lastIndex != -1) {
                        emessage.setCharAt(lastIndex, '*');
                        emessage.deleteCharAt(lastIndex + 1);
                        int rindex = emessage.indexOf("&r", lastIndex);
                        if (rindex != -1) {
                            emessage.setCharAt(rindex, '*');
                            emessage.deleteCharAt(rindex + 1);
                        } else {
                            emessage.append("*");
                        }
                        lastIndex += 1;
                    }
                }

                // Strike
                lastIndex = 0;

                while (lastIndex != -1) {

                    lastIndex = emessage.indexOf("&m", lastIndex);

                    if (lastIndex != -1) {
                        emessage.setCharAt(lastIndex, '~');
                        emessage.setCharAt(lastIndex + 1, '~');
                        int rindex = emessage.indexOf("&r", lastIndex);
                        if (rindex != -1) {
                            emessage.setCharAt(rindex, '~');
                            emessage.setCharAt(rindex + 1, '~');
                        } else {
                            emessage.append("~~");
                        }
                        lastIndex += 1;
                    }
                }

                // Bold
                lastIndex = 0;
                while (lastIndex != -1) {

                    lastIndex = emessage.indexOf("&l", lastIndex);

                    if (lastIndex != -1) {
                        emessage.setCharAt(lastIndex, '*');
                        emessage.setCharAt(lastIndex + 1, '*');
                        int rindex = emessage.indexOf("&r", lastIndex);
                        if (rindex != -1) {
                            emessage.setCharAt(rindex, '*');
                            emessage.setCharAt(rindex + 1, '*');
                        } else {
                            emessage.append("**");
                        }
                        lastIndex += 1;
                    }
                }
            }

            String output = emessage.toString();
            if(instance.getConfig().configNode.getNode("format", "discord", "to", "escapeDiscordFormat").getBoolean()) output = output.replaceAll("([*~])", "\\\\$1");

            message = message.replace("<Server>", serverName)
                    .replace("<User>", event.getPlayer().getUsername())
                    .replace("<Message>", output)
                    .replaceAll("&[a-fA-F0-9knKN]", "");
            switch (instance.getConfig().getChatMode())
            {
                case "global":
                    instance.getDiscordMethods().sendMessage(instance.getConfig().getChannels("global"), message);
                    break;
                case "separate":
                    instance.getDiscordMethods().sendMessage(currentChannelId, message);
                    break;
            }
        }
    }
}
