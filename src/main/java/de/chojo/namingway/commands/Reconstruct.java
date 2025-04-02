package de.chojo.namingway.commands;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashProvider;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Channels;
import de.chojo.namingway.db.Roles;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Reconstruct implements SlashProvider<Slash>, SlashHandler {
    private final Configurations<ConfigFile> config;

    public Reconstruct(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent slash, EventContext eventContext) {
        Long minutes = slash.getOption("minutes", OptionMapping::getAsLong);
        List<AuditLogEntry> channels = slash.getGuild().retrieveAuditLogs()
                                            .type(ActionType.CHANNEL_UPDATE)
                                            .user(slash.getGuild().getSelfMember())
                                            .complete();

        var conf = config.secondary(Channels.KEY);
        for (AuditLogEntry entry : channels) {
            if (entry.getTimeCreated().isAfter(OffsetDateTime.now().minus(minutes, ChronoUnit.MINUTES))) continue;
            conf.renamed(entry.getTargetIdLong(), entry.getChangeByKey(AuditLogKey.CHANNEL_NAME).getOldValue());
        }


        List<AuditLogEntry> roles = slash.getGuild().retrieveAuditLogs()
                                         .type(ActionType.ROLE_UPDATE)
                                         .user(slash.getGuild().getSelfMember())
                                         .complete();
        conf = config.secondary(Roles.KEY);
        for (AuditLogEntry entry : roles) {
            if (entry.getTimeCreated().isAfter(OffsetDateTime.now().minus(minutes, ChronoUnit.MINUTES))) continue;
            conf.renamed(entry.getTargetIdLong(), entry.getChangeByKey(AuditLogKey.ROLE_NAME).getOldValue());
        }

        config.save();
    }

    @Override
    public Slash slash() {
        return Slash.of("reconstruct", "reconstruct if something got mixed up")
                    .adminCommand()
                    .command(this)
                    .argument(Argument.integer("minutes", "minutes since now"))
                    .build();
    }
}
