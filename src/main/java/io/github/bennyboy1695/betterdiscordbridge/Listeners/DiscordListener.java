package io.github.bennyboy1695.betterdiscordbridge.Listeners;

import com.typesafe.config.parser.ConfigNode;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.ComponentSerializers;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DiscordListener extends ListenerAdapter {

    private BetterDiscordBridge instance;

    public DiscordListener(BetterDiscordBridge instance) {
        this.instance = instance;
    }

    double colorDistance(Color c1, Color c2)
    {
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        CommentedConfigurationNode configNode = instance.getConfig().configNode;
        RegisteredServer currentServer = null;
        for (RegisteredServer registeredServer : instance.getProxyServer().getAllServers()) {
            if (!configNode.getNode("discord", "channels", registeredServer.getServerInfo().getName()).isVirtual()
                    && String.valueOf(configNode.getNode("discord", "channels", registeredServer.getServerInfo().getName()).getLong()).equals(event.getChannel().getId())) {
                currentServer = registeredServer;
                break;
            }
        }

        if (currentServer != null) {
            HashMap<String, Color> colors = new HashMap<String, Color>(){{
                put("&4", new Color(75, 0 ,0));
                put("&c", new Color(100,25,25));
                put("&6", new Color(85,64,20));
                put("&e", new Color(100,100,25));
                put("&2", new Color(0,75,0));
                put("&a", new Color(25,100,25));
                put("&b", new Color(25,100,100));
                put("&3", new Color(0,75,75));
                put("&1", new Color(0,0,75));
                put("&9", new Color(25,25,100));
                put("&d", new Color(100,25,100));
                put("&5", new Color(75,0,75));
                put("&f", new Color(100,100,100));
                put("&7", new Color(75,75,75));
                put("&8", new Color(25,25,25));
                put("&0", new Color(0,0,0));
            }};

            HashMap<Double, String> distances = new HashMap<>();
            for (Map.Entry<String, Color> color : colors.entrySet()) {
                distances.put(colorDistance(color.getValue(), event.getMember().getColor()), color.getKey());
            }

            String text = instance.getConfig().getFormats("discord_from")
                    .replace("<User>", event.getAuthor().getName())
                    .replace("<Discriminator>", event.getAuthor().getDiscriminator())
                    .replace("<Message>", event.getMessage().getContentDisplay())
                    .replace("<RankColor>", Collections.min(distances.entrySet(), Comparator.comparingDouble(Map.Entry::getKey)).getValue());

            if(instance.getConfig().configNode.getNode("format", "discord", "from", "colorPing").getBoolean())
            {
                for(Member member: event.getMessage().getMentionedMembers())
                {
                    text = text.replace("@"+member.getEffectiveName(), "&b@"+member.getEffectiveName()+"&r");
                }
            }

            text = text.replaceAll("&", "\u00A7"); // Our text encoding mismatch ;)

            String finalText = text;
            switch (instance.getConfig().getChatMode())
            {
                case "global":
                    instance.getProxyServer().broadcast(TextComponent.of(finalText));
                    break;
                case "separate":
                    currentServer.getPlayersConnected().forEach(p -> p.sendMessage(TextComponent.of(finalText)));
                    break;
            }
        }
    }
}
