package io.github.bennyboy1695.betterdiscordbridge.Listeners;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//h//
public class DiscordListener extends ListenerAdapter {

    @Inject
    private Logger logger = LoggerFactory.getLogger(DiscordListener.class);

    private final BetterDiscordBridge bridge;

    public DiscordListener(BetterDiscordBridge bridge) {
        this.bridge = bridge;
    }


    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
        event.getJDA().getGuilds().forEach(Guild::loadMembers);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {


        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String msg = message.getContentDisplay();
        boolean bot = author.isBot();
        Guild guild = event.getGuild();
        Member member = event.getMessage().getMember();

        String name = member == null ? author.getName():member.getEffectiveName();



        String msgformat = bridge.getConfig().getFormats("discord_from")
                .replace("<User>", name)
                .replace("<Message>", msg);


        @NonNull TextComponent finalmessage = TextComponent.builder(msgformat).build();
        ComponentLike compfinalmessage = finalmessage.asComponent();


        if (channel.getIdLong() == bridge.getConfig().getChannels("global")) {
            bridge.getProxyServer().sendMessage(compfinalmessage);
            logger.info("[Discord] " + name + " : " + msg);
        } else {
            for (RegisteredServer server : bridge.getProxyServer().getAllServers()) {
                if (channel.getIdLong() == bridge.getConfig().getChannels(server.getServerInfo().getName())) {
                    for (Player player : server.getPlayersConnected()) {
                        player.sendMessage(compfinalmessage);
                    }
                    logger.info("[Discord] " + name + " : " + msg);
                }
            }
        }
    }
}

