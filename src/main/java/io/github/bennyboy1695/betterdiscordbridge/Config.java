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
    CommentedConfigurationNode configNode;
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

    public CommentedConfigurationNode getConfigNode() {
        return configNode;
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
        if (configNode.getNode("discord", "info", "status").isVirtual()) {
            configNode.getNode("discord", "info", "status").setValue("Playing on A Velocity Server!").setComment("This will set what the bot is playing/listening to! Currently requires a restart of the proxy to use the config to change it, but there's a command to do it too!");
        }
        if (configNode.getNode("discord", "info", "useConfigStatus").isVirtual()) {
            configNode.getNode("discord", "info", "useConfigStatus").setValue(true).setComment("If this is set to true, everytime the Velocity server starts the bots status will be set from whats written in the config!");
        }
        if (configNode.getNode("discord", "info", "useConfigServerNames").isVirtual()) {
            configNode.getNode("discord", "info", "useConfigServerNames").setValue(true).setComment("If this is set to true then <Server> will be replaced with values in this config!");
        }

        //Channels
        if (configNode.getNode("discord", "channels", "global").isVirtual()) {
            configNode.getNode("discord", "channels", "global").setValue(0L).setComment("This is the channel id of the discord channel you would like chat to go to. This is only used if mode is set to global!");
        }
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            String serverName = registeredServer.getServerInfo().getName();
            if (configNode.getNode("discord", "channels", serverName).isVirtual()) {
                configNode.getNode("discord", "channels", serverName).setValue(0L).setComment("This is where you put the id of the discord channel you would like to link to " + registeredServer.getServerInfo().getName() + " . This channel will only be used if mode is set to separated and if the server still exists in velocity!");
            }
            String str = serverName;
            String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
            if (configNode.getNode("discord", "channels", serverName, "serverName").isVirtual()) {
                configNode.getNode("discord", "channels", serverName, "serverName").setValue(cap).setComment("This is the name you want to replace <Server> in discord if useConfigServerNames is set to true!");
            }
        }

        //Formats
        if (configNode.getNode("format", "discord", "to").isVirtual()) {
            configNode.getNode("format", "discord", "to").setValue("`<Server>`: <Message>").setComment("This is how the chat messages will look when they go into discord! <Server> is replaced by the name of the server gotten from the Velocity config.");
        }
        if (configNode.getNode("format", "discord", "from").isVirtual()) {
            configNode.getNode("format", "discord", "from").setValue("&f[&1Discord&f] &2<User>&f:&r <Message>").setComment("This is how messages from discord will get formatted as they go ingame!");
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

    public String getGameStatus() {
        String status = "";
        try {
            status = configNode.getNode("discord", "info", "status").getString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return status;
    }

    public Boolean getUseConfigServerNames() {
        Boolean serverNames = null;
        try {
            serverNames = configNode.getNode("discord", "info", "useConfigServerNames").getBoolean();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return serverNames;
    }

    public Boolean getUseStatus() {
        Boolean status = null;
        try {
            status = configNode.getNode("discord", "info", "useConfigStatus").getBoolean();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return status;
    }

    public long getChannels(String channel) {
        long id = 0L;
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
                format = configNode.getNode("format", "discord", "to").getString();
                break;
            case "discord_from":
                format = configNode.getNode("format", "discord", "from").getString();
                break;
        }
        return format;
    }

    public String getServerNames(String serverName) {
        String name = "";
        try {
            name = configNode.getNode("discord", "channels", serverName, "serverName").getString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return name;
    }
}
