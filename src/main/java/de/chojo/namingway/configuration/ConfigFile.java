package de.chojo.namingway.configuration;

import de.chojo.jdautil.configuratino.Configuration;
import de.chojo.jdautil.util.SysVar;
import de.chojo.namingway.util.config.ConfigKey;

import java.nio.file.Path;

public class ConfigFile {
    public static ConfigKey<ConfigFile> KEY = ConfigKey.of(
            "config",
            Path.of(SysVar.get("bot.config", "BOT_CONFIG").orElse("config/config.yaml")),
            ConfigFile.class,
            ConfigFile::new);
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
