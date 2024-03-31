package de.chojo.namingway.commands;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashProvider;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Users;
import de.chojo.namingway.util.config.JacksonConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.Map;

public class Rollback implements SlashProvider<Slash>, SlashHandler {
    private final JacksonConfig<ConfigFile> config;
    private boolean active = false;

    public Rollback(JacksonConfig<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public Slash slash() {
        return Slash.of("rollback", "Rollback name changes")
                .adminCommand()
                .unlocalized()
                .guildOnly()
                .command(this)
                .argument(Argument.bool("confirm", "true to confirm").asRequired())
                .build();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        boolean confirm = event.getOption("confirm", OptionMapping::getAsBoolean);
        if (!confirm) {
            event.reply("Please confirm").setEphemeral(true).queue();
            return;
        }
        Map<Long, String> names = config.secondary(Users.KEY).names();
        names = new HashMap<>(names);
        Guild guild = event.getGuild();
        for (var entry : names.entrySet()) {
            try {
                Member complete = guild.retrieveMemberById(entry.getKey()).complete();
                complete.modifyNickname(entry.getValue()).complete();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
