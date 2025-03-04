package de.chojo.namingway.configuration;

import de.chojo.jdautil.util.SysVar;
import dev.chojo.ocular.key.Key;

import java.nio.file.Path;

public class ConfigFile {
    public static Key<ConfigFile> KEY = Key.builder(
                                                   Path.of(SysVar.get("bot.config", "BOT_CONFIG").orElse("config/config.yaml")),
                                                   ConfigFile::new)
                                           .build();
    private String token = "";
    private boolean active = false;

    public boolean active() {
        return active;
    }

    public void active(boolean active) {
        this.active = active;
    }

    public String token() {
        return token;
    }
}
