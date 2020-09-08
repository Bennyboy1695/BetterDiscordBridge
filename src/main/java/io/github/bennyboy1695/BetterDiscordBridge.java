package io.github.bennyboy1695;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.bennyboy1695.Commands.CommandGameStatus;
import io.github.bennyboy1695.Utils.DiscordMethods;
import io.github.bennyboy1695.listeners.DiscordListener;
import io.github.bennyboy1695.listeners.VelocityEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.util.EnumSet;
import org.slf4j.Logger;



/*
 Setting up the Plugins information.
 The id, Name, dependencies required if any, the version of the plugin, description, and author(s)
 To credit the Origin Author and Contributors, I have changed the Author = to "Bennyboy1695 & Contributors"
 */

@Plugin(id = "betterdiscordbridge", name = "BetterDiscordBridge", version = BetterDiscordBridge.VERSION, description = "A Velocity Proxy Discord Bridge", authors = {"Bennyboy1695 & Contributors"})
public final class BetterDiscordBridge {

    // Changing VERSION = will update the whole plugins version number.
    static final String VERSION = "1.2.4";

    private final ProxyServer server;

    // The Logger class that we use is being changed toLowerCase logger.
    // Use lowercase "logger" to call the "org.slf4j.Logger" class.
    private final Logger logger;
    private final Path configDirectory;

    // Changing to lowercase config when calling to the Config class.
    private Config config;

    // JDA(Java Discord Api), to lowercase jda.
    private JDA jda;

    // DiscordMethods only used for jda shutdown method, Line 140.
    private DiscordMethods discordMethods;

    @Inject
    public BetterDiscordBridge(ProxyServer server, Logger logger, @DataDirectory Path configDirectory) {
        this.server = server;
        this.logger = logger;
        this.configDirectory = configDirectory;
    }


    // When the server/proxy starts, this is the init method to start the plugin & load/register resources.
    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        logger.info("Enabling BetterDiscordBridge, version " + VERSION + "!");

        this.config = new Config(configDirectory, "betterdiscordbridge.conf", logger, server);

        try {
            loadDiscord();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // This will get the Game Status that was set in the config.json file
        if (getConfig().getUseStatus()) {
            updateGameStatus(getConfig().getGameStatus());
        }

        server.getEventManager().register(this, new VelocityEventListener(this));

        // This Registration method is deprecated and will need to be updated eventually.
        server.getCommandManager().register(new CommandGameStatus(this), "gamestatus", "gs");
    }


    // loadDiscord will load the bot token, channel info, guild info, and run DiscordListener
    private void loadDiscord() {
        try {
            setupDiscord(getConfig().getDiscordToken(), getConfig().getGuildID(), new DiscordListener(this));

            // This will send a message to the console as to indicate that the loadDiscord event is loading
            logger.info("Loading Discord Bot!");

            /*
             If loadDiscord does fail, we catch the error, log it in console with an understanding message.
             We then print the StackTrace to allow for reading on why and where the error occurred.
             For those that report issues in GitHub, the StackTrace is a little bit of a help.
             That is why I choose to have it print after the clear console error message.
             The StackTrace is caught and printed in the ProxyInit on Line 70/71.
            */
        } catch (Exception e) {
            logger.error("ERROR DURING 'loadDiscord' EVENT!");
        }
    }

    // Get the Config.
    public Config getConfig() {
        return config;
    }

    // Get the Proxy Server.
    public ProxyServer getProxyServer() {
        return server;
    }

    // Get the Logger.
    public Logger getLogger() {
        return logger;
    }


    /*
    I like to look at this as the Connection process to discords api.
    We get the token as a string, and Object eventListener.
    From here we build the jda connection and asses the parameters that it requires for us to effectively run this plugin.

    In a lot of ways, I like to look at this like the heart of the project.

    All Gateway Intents are not really needed but it was easier when building the code.
    If a better method is applicable here, Suggestions are open.

    */
    private void setupDiscord(String token, long GuildID, Object eventListener) {
        // Java Discord API Builder
        try
        {
            // Grab token as string, enable all intents.
            jda = JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class))
                    .setAutoReconnect(true)
                    .addEventListeners(eventListener)
                    .setMemberCachePolicy(MemberCachePolicy.DEFAULT)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .build().awaitReady();
            // Log that this was successful
            logger.info("JDA Initialized Successfully");

        } catch (LoginException e) {
            logger.error("ERROR STARTING THE PLUGIN:");
            logger.error("You probably didn't set the token yet, edit your config!");
            e.printStackTrace();

        } catch (Exception e) {
            logger.error("Error connecting to discord. This is NOT a plugin error");
            e.printStackTrace();
        }
    }

    public JDA getJDA() {
        return jda;
    }

    // Plugin Reload event.
    public void doReload() {
        try {
            discordMethods.doShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getConfig().saveConfig();
            this.config = new Config(configDirectory, "betterdiscordbridge.conf", logger, server);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setupDiscord(getConfig().getDiscordToken(), getConfig().getGuildID(), new DiscordListener(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This is how the GameStatus for the bot is created and set.
    public void updateGameStatus(String status) {
        if (status.startsWith("Playing") || status.startsWith("playing")) {
            getJDA().getPresence().setActivity(Activity.playing(status.replace("playing", "").replace("Playing", "")));
            getLogger().info("Set bots status to: " + status);
        } else if (status.startsWith("Watching") || status.startsWith("watching")) {
            getJDA().getPresence().setActivity(Activity.watching(status.replace("watching", "").replace("Watching", "")));
            getLogger().info("Set bots status to: " + status);
        } else if (status.startsWith("Listening") || status.startsWith("listening")) {
            getJDA().getPresence().setActivity(Activity.listening(status.replace("listening", "").replace("Listening", "")));
            getLogger().info("Set bots status to: " + status);
        }
    }
}
