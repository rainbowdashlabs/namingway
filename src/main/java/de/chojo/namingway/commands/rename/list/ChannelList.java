package de.chojo.namingway.commands.rename.list;

import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Channels;
import dev.chojo.ocular.Configurations;

public class ChannelList extends BaseList {

    public ChannelList(Configurations<ConfigFile> config) {
        super(config, Channels.KEY, (id, guild) -> guild.getGuildChannelById(id));
    }
}
