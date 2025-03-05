package de.chojo.namingway.commands.rename.list;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.util.MentionUtil;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Channels;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.stream.Collectors;

public class ChannelList implements SlashHandler {
    private final Configurations<ConfigFile> config;

    public ChannelList(Configurations<ConfigFile> config) {
        this.config = config;
    }


    @Override
    public void onSlashCommand(SlashCommandInteractionEvent slash, EventContext eventContext) {
        String collect = config.secondary(Channels.KEY).newNames().entrySet().stream()
                               .map(e -> "%s -> %s".formatted(MentionUtil.channel(e.getKey()), e.getValue()))
                               .collect(Collectors.joining("\n"));
        slash.reply(collect).setEphemeral(true).queue();
    }
}
