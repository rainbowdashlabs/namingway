package de.chojo.namingway.db;

import de.chojo.jdautil.configuratino.Configuration;
import de.chojo.jdautil.util.SysVar;
import de.chojo.namingway.util.config.ConfigKey;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Users {
    public static ConfigKey<Users> KEY = ConfigKey.of(
            "users",
            Path.of(SysVar.get("bot.usersdb", "BOT_USERS_DB").orElse("config/users.yaml")),
            Users.class,
            Users::new);
    private Map<Long, String> names = new HashMap<>();

    public static Configuration<Users> load() {
        return Configuration.create(new Users());
    }

    public boolean addIfAbsent(ISnowflake id, String nickname) {
        return names.putIfAbsent(id.getIdLong(), nickname) == null;
    }

    public boolean addIfAbsent(Member member) {
        return addIfAbsent(member, member.getEffectiveName());
    }

    public Map<Long, String> names() {
        return Collections.unmodifiableMap(names);
    }

    public void rolledBack(Long id) {
        names.remove(id);
    }

    public String randomName() {
        return new ArrayList<>(names.values()).get(ThreadLocalRandom.current().nextInt(names.values().size()));
    }
}
