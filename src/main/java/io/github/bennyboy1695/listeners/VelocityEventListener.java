package io.github.bennyboy1695.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import io.github.bennyboy1695.BetterDiscordBridge;
import io.github.bennyboy1695.Utils.DiscordMethods;

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
}
