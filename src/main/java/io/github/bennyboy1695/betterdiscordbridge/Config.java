package io.github.bennyboy1695.betterdiscordbridge;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class Config {

    private File defaultCfg;
    private Logger logger;
    private Path path;
    private ProxyServer proxyServer;

    Path defaultConfig;
    File defaultConf;
    public CommentedConfigurationNode configNode;
    ConfigurationLoader<CommentedConfigurationNode> configManager;

    public Config(Path defaultConfig, String configName, Logger logger, ProxyServer proxyServer) {
        this.defaultConfig = defaultConfig;
        this.logger = logger;
        this.proxyServer = proxyServer;

        if (!defaultConfig.toFile().exists()) {
            defaultConfig.toFile().mkdir();
        }

        defaultConf = new File(defaultConfig.toFile(), configName);

        configManager = HoconConfigurationLoader.builder().setFile(defaultConf).build();

        try {
            configNode = configManager.load();
            pluginConf(configName);
            logger.info("Loading Config!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            saveConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        save(defaultConf);
    }

    public void saveConfig() {
        try {
            configManager.save(configNode);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void save(File input) {
        configManager = HoconConfigurationLoader.builder().setFile(input).build();
    }


    private void pluginConf(String fileName) {
        //Settings
        if (configNode.getNode("discord", "info").isVirtual()) {
            configNode.getNode("discord", "info", "botToken").setValue("token").setComment("Here is where you put the token for the bot you would like this plugin to use to connect to discord!");
            configNode.getNode("discord", "info", "guildId").setValue(0L).setComment("This is the id of your discord server!");
        }
        if (configNode.getNode("discord", "info", "mode").isVirtual()) {
            configNode.getNode("discord", "info", "mode").setValue("global").setComment("This decides whether the plugin sends chat to one channel, or one for each server connected to velocity! Options are: 'global' or 'separated'");
        }

        //Channels
        if (configNode.getNode("discord", "channels", "global").isVirtual()) {
            configNode.getNode("discord", "channels", "global").setValue(0L).setComment("This is the channel id of the discord channel you would like chat to go to. This is only used if mode is set to global!");
        }
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            if (configNode.getNode("discord", "channels", registeredServer.getServerInfo().getName()).isVirtual()) {
                configNode.getNode("discord", "channels", registeredServer.getServerInfo().getName()).setValue(0L).setComment("This is where you put the id of the discord channel you would like to link to " + registeredServer.getServerInfo().getName() + " . This channel will only be used if mode is set to separated and if the server still exists in velocity!");
            }
        }

        //Formats

        // To discord from minecraft
        if (configNode.getNode("format", "discord", "to", "style").isVirtual()) {
            configNode.getNode("format", "discord", "to", "style").setValue("[*<Server>*] <User>: <Message>").setComment("This is how the chat messages will look when they go into discord! <Server> is replaced by the name of the server gotten from the Velocity config.");
        }
        if (configNode.getNode("format", "discord", "to", "translateColorCodes").isVirtual()) {
            configNode.getNode("format", "discord", "to", "translateColorCodes").setValue(true).setComment("If true message will be translated eg. from `&oItalic&r &lBold&r &mStrike&r` to `*Italic* **Bold** ~~Strike~~`");
        }
        if (configNode.getNode("format", "discord", "to", "escapeDiscordFormat").isVirtual()) {
            configNode.getNode("format", "discord", "to", "escapeDiscordFormat").setValue(true).setComment("Escape discord format (*italic*, **bold**, ~~strike~~)");
        }

        // From discord to minecraft
        if (configNode.getNode("format", "discord", "from", "style").isVirtual()) {
            configNode.getNode("format", "discord", "from", "style").setValue("&f[&aDiscord&f] <RankColor><User>&7#<Discriminator>&f:&r <Message>").setComment("This is how messages from discord will get formatted as they go ingame!");
        }
        if (configNode.getNode("format", "discord", "from", "colorPing").isVirtual()) {
            configNode.getNode("format", "discord", "from", "colorPing").setValue(true).setComment("Color @ping");
        }
    }

    public String getDiscordToken() {
        String token = "";
        try {
            token = configNode.getNode("discord", "info", "botToken").getString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return token;
    }

    public long getGuildID() {
        long id = 0L;
        try {
            id = configNode.getNode("discord", "info", "guildId").getLong();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String getChatMode() {
        String mode = "";
        try {
            mode = configNode.getNode("discord", "info", "mode").getString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return mode;
    }

    public long getChannels(String channel) {
        long id = -1L;
        if (channel.toLowerCase().equals("global")) {
            id = configNode.getNode("discord", "channels", "global").getLong();
        } else {
            id = configNode.getNode("discord", "channels", channel).getLong();
        }
        return id;
    }

    public String getFormats(String type) {
        String format = "";
        switch (type) {
            case "discord_to":
                format = configNode.getNode("format", "discord", "to", "style").getString();
                break;
            case "discord_from":
                format = configNode.getNode("format", "discord", "from", "style").getString();
                break;
        }
        return format;
    }
}
