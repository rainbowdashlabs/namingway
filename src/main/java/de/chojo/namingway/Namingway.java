package de.chojo.namingway;

import de.chojo.jdautil.interactions.dispatching.InteractionHub;
import de.chojo.namingway.commands.Rollback;
import de.chojo.namingway.commands.Status;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.services.MentionListener;
import de.chojo.namingway.util.Threading;
import dev.chojo.ocular.Configurations;
import dev.chojo.ocular.dataformats.YamlDataFormat;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.Executors;

public class Namingway {
    ShardManager shardManager;
    public static Namingway namingway;

    public static void main(String[] args) {
        namingway = new Namingway();
        namingway.start();
    }


    public void start() {
        Configurations<ConfigFile> config = Configurations.builder(ConfigFile.KEY, new YamlDataFormat()).build();
        shardManager = DefaultShardManagerBuilder.createDefault(config.main().token())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setEnableShutdownHook(false)
                .setEventPool(Executors.newThreadPerTaskExecutor(Threading.createVirtualThreadFactory(new ThreadGroup("jda worker"))))
                .setThreadFactory(Threading.createVirtualThreadFactory(new ThreadGroup("jda worker")))
                .addEventListeners(new MentionListener(config))
                .build();

        InteractionHub.builder(shardManager)
                .testMode()
                .withCommands(new Rollback(config),
                        new Status(config))
                .build();
    }
}
