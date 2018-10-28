package io.github.bennyboy1695.betterdiscordbridge;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.bennyboy1695.betterdiscordbridge.Listeners.DiscordListener;
import io.github.bennyboy1695.betterdiscordbridge.Listeners.VelocityEventListener;
import io.github.bennyboy1695.betterdiscordbridge.Utils.DiscordMethods;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "betterdiscordbridge", name = "BetterDiscordBridge", version = BetterDiscordBridge.VERSION, authors = {"Bennyboy1695", "js6pak"})
public final class BetterDiscordBridge {


    static final String VERSION = "1.0.1";

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

        proxyServer.getEventManager().register(this, new VelocityEventListener(this));
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
}
