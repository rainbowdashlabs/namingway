package de.chojo.namingway.db;

import de.chojo.jdautil.util.SysVar;
import dev.chojo.ocular.key.Key;

import java.nio.file.Path;

public class Channels {
    public static Key<RenamedRegistry> KEY = Key.builder(
                                                        Path.of(SysVar.get("bot.channeldb", "BOT_CHANNEL_DB").orElse("data/channels.yaml")),
                                                        RenamedRegistry::new)
                                                .build();
}
