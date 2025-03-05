package de.chojo.namingway.commands;

import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashProvider;
import de.chojo.namingway.commands.rename.Channel;
import de.chojo.namingway.commands.rename.Role;
import de.chojo.namingway.commands.rename.additional.AddName;
import de.chojo.namingway.commands.rename.additional.ListNames;
import de.chojo.namingway.commands.rename.list.ChannelList;
import de.chojo.namingway.commands.rename.list.RoleList;
import de.chojo.namingway.configuration.ConfigFile;
import dev.chojo.ocular.Configurations;

import static de.chojo.jdautil.interactions.slash.Argument.channel;
import static de.chojo.jdautil.interactions.slash.Argument.role;
import static de.chojo.jdautil.interactions.slash.Argument.text;
import static de.chojo.jdautil.interactions.slash.Group.group;
import static de.chojo.jdautil.interactions.slash.SubCommand.sub;

public class Rename implements SlashProvider<Slash> {
    private final Configurations<ConfigFile> config;

    public Rename(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public Slash slash() {
        return Slash.slash("rename", "Create a rename of something")
                    .adminCommand()
                    .guildOnly()
                    .unlocalized()
                    .subCommand(sub("channel", "Rename a channel or category")
                            .handler(new Channel(config))
                            .argument(channel("channel", "channel to rename").asRequired())
                            .argument(text("name", "new name of channel").asRequired()))
                    .subCommand(sub("role", "rename a role")
                            .handler(new Role(config))
                            .argument(role("role", "role to rename").asRequired())
                            .argument(text("name", "new name of role").asRequired())
                    )
                    .group(group("list", "list planned changes")
                            .subCommand(sub("channels", "list channel changes")
                                    .handler(new ChannelList(config)))
                            .subCommand(sub("roles", "list roles changes")
                                    .handler(new RoleList(config))))
                    .group(group("additional", "Manage additional names")
                            .subCommand(sub("add", "Add additional names")
                                    .handler(new AddName(config))
                                    .argument(text("name", "name to add").asRequired()))
                            .subCommand(sub("list", "List of additional names")
                                    .handler(new ListNames(config))))
                    .build();
    }
}
