package de.chojo.namingway.commands;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashProvider;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.util.config.JacksonConfig;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class Status implements SlashProvider<Slash>, SlashHandler {
    private final JacksonConfig<ConfigFile> config;

    public Status(JacksonConfig<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public Slash slash() {
        return Slash.of("status", "Set the bot status")
                .adminCommand()
                .guildOnly()
                .unlocalized()
                .command(this)
                .argument(Argument.bool("active", "active or not").asRequired())
                .build();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        boolean active = event.getOption("active", OptionMapping::getAsBoolean);
        config.main().active(active);
        config.save();
        event.reply("State changed").setEphemeral(true).queue();
    }
}
