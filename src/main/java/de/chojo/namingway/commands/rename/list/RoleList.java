package de.chojo.namingway.commands.rename.list;

import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Roles;
import dev.chojo.ocular.Configurations;

public class RoleList extends BaseList {
    public RoleList(Configurations<ConfigFile> config) {
        super(config, Roles.KEY, (id, guild) -> guild.getRoleById(id));
    }
}
