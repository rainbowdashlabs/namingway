package de.chojo.namingway.db;

import de.chojo.jdautil.util.SysVar;
import dev.chojo.ocular.key.Key;
import net.dv8tion.jda.api.entities.ISnowflake;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Roles {
    public static Key<RenamedRegistry> KEY = Key.builder(
                                              Path.of(SysVar.get("bot.rolesdb", "BOT_ROLES_DB").orElse("data/roles.yaml")),
                                              RenamedRegistry::new)
                                      .build();
}
