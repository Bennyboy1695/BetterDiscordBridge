package io.github.bennyboy1695.betterdiscordbridge.Listeners;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.bennyboy1695.betterdiscordbridge.BetterDiscordBridge;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.kyori.text.serializer.ComponentSerializers;

public class DiscordListener extends ListenerAdapter {

    private BetterDiscordBridge instance;

    public DiscordListener(BetterDiscordBridge instance) {
        this.instance = instance;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!instance.getConfig().getChatMode().equals("separated")) {
            if (event.getChannel().getIdLong() == instance.getConfig().getChannels("global")) {
                instance.getProxyServer().broadcast(ComponentSerializers.LEGACY.deserialize(instance.getConfig().getFormats("discord_from").replace("<User>", event.getMember().getEffectiveName()).replace("<Message>", event.getMessage().getContentDisplay()), '&'));
            }
        } else {
            for (RegisteredServer server : instance.getProxyServer().getAllServers()) {
                if (event.getChannel().getIdLong() == instance.getConfig().getChannels(server.getServerInfo().getName())) {
                    for (Player player : server.getPlayersConnected()) {
                        player.sendMessage(ComponentSerializers.LEGACY.deserialize(instance.getConfig().getFormats("discord_from").replace("<User>", event.getMember().getEffectiveName()).replace("<Message>", event.getMessage().getContentDisplay()), '&'));
                    }
                }
            }
        }
    }
}
