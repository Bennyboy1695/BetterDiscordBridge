package io.github.bennyboy1695.betterdiscordbridge.Listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;

public class VelocityEventListener {

    private BetterDiscordBridge instance;

    public VelocityEventListener(BetterDiscordBridge instance) {
        this.instance = instance;
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        String serverName = event.getPlayer().getCurrentServer().get().getServerInfo().getName();
        if (!instance.getConfig().getChatMode().equals("separated")) {
            instance.getDiscordMethods().sendMessage(instance.getConfig().getChannels("global"), instance.getConfig().getFormats("discord_to").replace("<Server>", serverName).replace("<Message>", event.getMessage()));
        } else {
           instance.getDiscordMethods().sendMessage(instance.getConfig().getChannels(serverName), instance.getConfig().getFormats("discord_to").replace("<Server>", serverName).replace("<Message>", event.getMessage()));
        }
    }
}
