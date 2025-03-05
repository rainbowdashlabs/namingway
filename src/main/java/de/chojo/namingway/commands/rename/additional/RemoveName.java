package de.chojo.namingway.commands.rename.additional;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.util.Completion;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Users;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class RemoveName implements SlashHandler {
    private final Configurations<ConfigFile> config;

    public RemoveName(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        try (var conf = config.secondaryWrapped(Users.KEY)) {
            boolean name = conf.config().removeAdditionalName(event.getOption("name").getAsString());
            if (!name) {
                event.reply("This name does not exist.").setEphemeral(true).queue();
            } else {
                event.reply("Name removed").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
        String name = event.getOption("name", OptionMapping::getAsString);
        List<Command.Choice> completions = Completion.complete(name, config.secondary(Users.KEY).additionalNames());
        event.replyChoices(completions).queue();
    }
}
