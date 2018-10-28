package io.github.bennyboy1695.betterdiscordbridge;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.bennyboy1695.betterdiscordbridge.Commands.CommandGameStatus;
import io.github.bennyboy1695.betterdiscordbridge.Listeners.DiscordListener;
import io.github.bennyboy1695.betterdiscordbridge.Listeners.VelocityEventListener;
import io.github.bennyboy1695.betterdiscordbridge.Utils.DiscordMethods;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "betterdiscordbridge", name = "BetterDiscordBridge", version = BetterDiscordBridge.VERSION, authors = {"Bennyboy1695"})
public final class BetterDiscordBridge {


    static final String VERSION = "1.1.2";

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path configDirectory;
    private Config config;
    private JDA jda;
    private DiscordMethods discordMethods;
    private ConfigurationLoader<CommentedConfigurationNode> ConfigurationLoader;

    @Inject
    public BetterDiscordBridge(ProxyServer proxyServer, Logger logger, @DataDirectory Path configDirectory) {
        this.proxyServer = proxyServer;
        this.logger = Logger.getLogger("BetterDiscordBridge");
        this.configDirectory = configDirectory;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        logger.info("Enabling BetterDiscordBridge, version " + VERSION + "!");

        this.config = new Config(configDirectory, "betterdiscordbridge.conf", logger, proxyServer);

        try {
            loadDiscord();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getConfig().getUseStatus()) {
            updateGameStatus(getConfig().getGameStatus());
        }

        proxyServer.getEventManager().register(this, new VelocityEventListener(this));
        proxyServer.getCommandManager().register(new CommandGameStatus(this), "gamestatus", "gs");
    }

    private void loadDiscord() {
        try {
            setupDiscord(getConfig().getDiscordToken(), getConfig().getGuildID(), new DiscordListener(this));
            logger.info("Loading Discord Bot!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Config getConfig() {
        return config;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

    private void setupDiscord(String token, long guildID, Object eventListener) {
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(token).build();
            jda.addEventListener(eventListener);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public DiscordMethods getDiscordMethods() {
        return new DiscordMethods(this, getConfig().getGuildID());
    }

    public void doReload() {
        try {
            discordMethods.doShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getConfig().saveConfig();
            this.config = new Config(configDirectory, "betterdiscordbridge.conf", logger, proxyServer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setupDiscord(getConfig().getDiscordToken(), getConfig().getGuildID(), new DiscordListener(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGameStatus(String status) {
        if (status.startsWith("Playing") || status.startsWith("playing")) {
            getJDA().getPresence().setGame(Game.playing(status.replace("playing", "").replace("Playing", "")));
            getLogger().info("Set bots status to: " + status);
        } else if (status.startsWith("Watching") || status.startsWith("watching")) {
            getJDA().getPresence().setGame(Game.watching(status.replace("watching", "").replace("Watching", "")));
            getLogger().info("Set bots status to: " + status);
        } else if (status.startsWith("Listening") || status.startsWith("listening")) {
            getJDA().getPresence().setGame(Game.listening(status.replace("listening", "").replace("Listening", "")));
            getLogger().info("Set bots status to: " + status);
        }
    }
}
