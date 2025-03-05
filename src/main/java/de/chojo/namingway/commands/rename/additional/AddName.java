package de.chojo.namingway.commands.rename.additional;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Users;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class AddName implements SlashHandler {
    private final Configurations<ConfigFile> config;

    public AddName(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        try (var conf = config.secondaryWrapped(Users.KEY)){
            conf.config().addAdditionalName(event.getOption("name").getAsString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
