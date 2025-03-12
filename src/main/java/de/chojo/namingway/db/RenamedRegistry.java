package de.chojo.namingway.db;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenamedRegistry {
    private final Map<Long, String> newNames = new HashMap<>();
    private final Map<Long, String> originals = new HashMap<>();

    public void renamed(ISnowflake id, String original) {
        this.originals.put(id.getIdLong(), original);
    }

    public String original(ISnowflake id) {
        return originals.get(id.getIdLong());
    }

    public void rename(ISnowflake id, String newName) {
        this.newNames.put(id.getIdLong(), newName);
    }

    public Map<Long, String> newNames() {
        return Collections.unmodifiableMap(newNames);
    }

    public Collection<Long> renamedIds() {
        return newNames.keySet();
    }

    public Map<Long, String> originals() {
        return Collections.unmodifiableMap(originals);
    }

    public String newName(ISnowflake e) {
        return newNames.get(e.getIdLong());
    }
}
