package de.chojo.namingway.commands.rename.additional;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Users;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ListNames implements SlashHandler {
    private final Configurations<ConfigFile> config;

    public ListNames(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        String join = String.join("\n", config.secondary(Users.KEY).additionalNames());
        event.reply(join).setEphemeral(true).queue();
    }
}
