package io.github.bennyboy1695.betterdiscordbridge.Utils;

import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class DiscordMethods {

    private BetterDiscordBridge instance;
    private long guildID;

    public DiscordMethods(BetterDiscordBridge instance, long guildID) {
        this.instance = instance;
        this.guildID = guildID;
    }
    /* Discord methods!! */

    public void sendMessage(long channelID, Object message) {
        if (message instanceof MessageEmbed) {
            instance.getJDA().getTextChannelById(channelID).sendMessage((MessageEmbed) message).complete();
        } else {
            instance.getJDA().getTextChannelById(channelID).sendMessage(message.toString()).complete();
        }
    }

    public void doShutdown() {
        try {
            instance.getJDA().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAutoDeleteMessage(long channelID, Object message, long seconds) {
        try {
            if (message instanceof MessageEmbed) {
                instance.getJDA().getTextChannelById(channelID).sendMessage((MessageEmbed) message).complete();
                instance.getJDA().getTextChannelById(channelID).deleteMessageById(instance.getJDA().getTextChannelById(channelID).getLatestMessageId()).completeAfter(seconds, TimeUnit.SECONDS);
            } else {
                instance.getJDA().getTextChannelById(channelID).sendMessage(message.toString()).complete();
                instance.getJDA().getTextChannelById(channelID).deleteMessageById(instance.getJDA().getTextChannelById(channelID).getLatestMessageId()).completeAfter(seconds, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser(String name) {
        return instance.getJDA().getUsersByName(name, false).get(0);
    }

    public User getUser(long id) {
        return instance.getJDA().getUserById(id);
    }

    public User getUser(String name, String discriminator) {
        for (User match : instance.getJDA().getUsersByName(name, false))
            if (match.getName().equals(name) && discriminator.equals(match.getDiscriminator()))
                return match;
        return null;
    }

    public Guild getGuild(long id) {
        return instance.getJDA().getGuildById(id);
    }

    public User getGuildUser(String name) {
        return getGuild(guildID).getMembersByName(name, false).get(0).getUser();
    }

    public User getGuildUser(long id) {
        return getGuild(guildID).getMemberById(id).getUser();
    }

    public User getGuildUser(String name, String discriminator) {
        for (Member match : getGuild(guildID).getMembersByName(name, false))
            if (match.getUser().getName().equals(name) && discriminator.equals(match.getUser().getDiscriminator()))
                return match.getUser();
        return null;
    }

    public void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(content).complete() );
    }

}
