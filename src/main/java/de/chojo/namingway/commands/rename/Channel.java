package de.chojo.namingway.commands.rename;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Channels;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class Channel implements SlashHandler {
    private final Configurations<ConfigFile> config;

    public Channel(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent slash, EventContext eventContext) {
        try (var conf = config.secondaryWrapped(Channels.KEY)) {
            ISnowflake snowflake = slash.getOption("channel", OptionMapping::getAsChannel);
            String name = slash.getOption("name", OptionMapping::getAsString);
            conf.config().rename(snowflake, name);
            slash.reply("Saved").queue();
        }
    }
}
