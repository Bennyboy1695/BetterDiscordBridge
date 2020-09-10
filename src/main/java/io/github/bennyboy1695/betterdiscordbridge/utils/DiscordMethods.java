package io.github.bennyboy1695.betterdiscordbridge.utils;

import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class DiscordMethods {

    private final BetterDiscordBridge bridge;

    public DiscordMethods(BetterDiscordBridge bridge, long guildID) {
        this.bridge = bridge;
    }

    public static void sendMessage(JDA jda , long channelID, Object message) {
        TextChannel textChannel = jda.getTextChannelById(channelID);
        if (textChannel == null) {
            return;
        }

        if (message instanceof MessageEmbed) {
            textChannel.sendMessage((MessageEmbed) message).queue();
        } else if (message instanceof String){
            textChannel.sendMessage((String) message).queue();
        }
    }

    public void doShutdown() {
        try {
            bridge.getJDA().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendAutoDeleteMessage(JDA jda, long channelID, Object message, long seconds) {
        TextChannel textChannel = jda.getTextChannelById(channelID);
        if (textChannel == null) {
            return;
        }
        if (message instanceof MessageEmbed) {
            textChannel.sendMessage((MessageEmbed) message).queue(sentMsg -> sentMsg.delete().queueAfter(seconds, TimeUnit.SECONDS));
        } else {
            textChannel.sendMessage((String) message).queue(sentMsg -> sentMsg.delete().queueAfter(seconds, TimeUnit.SECONDS));
        }
    }
}
