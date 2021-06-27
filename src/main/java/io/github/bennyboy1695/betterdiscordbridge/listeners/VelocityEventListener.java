package io.github.bennyboy1695.betterdiscordbridge.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import io.github.bennyboy1695.betterdiscordbridge.utils.DiscordMethods;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class VelocityEventListener {
    //h//
    private final BetterDiscordBridge bridge;

    public VelocityEventListener(BetterDiscordBridge bridge) {
        this.bridge = bridge;
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        String serverName;
        if (!bridge.getConfig().getUseConfigServerNames()) {
            serverName = bridge.getConfig().getServerNames(event.getPlayer().getCurrentServer().get().getServerInfo().getName());
        } else {
            serverName = event.getPlayer().getCurrentServer().get().getServerInfo().getName();
        }

        String message = bridge.getConfig().getFormats("discord_to")
                .replaceAll("<Server>", Matcher.quoteReplacement(serverName))
                .replaceAll("<User>", event.getPlayer().getUsername())
                .replaceAll("<Message>", event.getMessage());

        if (!bridge.getConfig().getChatMode().equals("separated")) {
            DiscordMethods.sendMessage(bridge.getJDA(), bridge.getConfig().getChannels("global"), message);
        } else {
           DiscordMethods.sendMessage(bridge.getJDA(), bridge.getConfig().getChannels(serverName), message);
        }
    }

    @Subscribe
    public void onConnect(ServerConnectedEvent event) {
        // Ignore if player switches servers or function is deactivated
        if(!event.getPreviousServer().equals(Optional.empty()) || !bridge.getConfig().isJoinMsgActive())
            return;

        // Prepare Embed
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("`" + event.getPlayer().getUsername() + "` joined the game");
        builder.setColor(new Color(255, 255, 85));
        MessageEmbed embed = builder.build();

        /*
        Global Mode: Send join message into global channel
        Seperated Mode: Send join message into all channels
         */
        if(!bridge.getConfig().getChatMode().equals("separated"))
            DiscordMethods.sendMessage(bridge.getJDA(), bridge.getConfig().getChannels("global"), embed);
        else {
            bridge.getProxyServer().getAllServers().forEach(
                    server -> DiscordMethods.sendMessage(bridge.getJDA(), bridge.getConfig().getChannels(server.getServerInfo().getName()), embed)
            );
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        // Ignore if function is deactivated
        if(!bridge.getConfig().isLeaveMsgActive())
            return;

        // Prepare embed
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("`" + event.getPlayer().getUsername() + "` left the game");
        builder.setColor(new Color(255, 255, 85));
        MessageEmbed embed = builder.build();

        /*
        Global Mode: Send leave message into global channel
        Seperated Mode: Send leave message into all channels
         */
        if(!bridge.getConfig().getChatMode().equals("separated"))
            DiscordMethods.sendMessage(bridge.getJDA(), bridge.getConfig().getChannels("global"), embed);
        else {
            bridge.getProxyServer().getAllServers().forEach(
                    server -> DiscordMethods.sendMessage(bridge.getJDA(), bridge.getConfig().getChannels(server.getServerInfo().getName()), embed)
            );
        }
    }
}
