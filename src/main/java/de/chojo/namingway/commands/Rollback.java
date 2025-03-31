package de.chojo.namingway.commands;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashProvider;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Channels;
import de.chojo.namingway.db.RenamedRegistry;
import de.chojo.namingway.db.Roles;
import de.chojo.namingway.db.Users;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.Map;

public class Rollback implements SlashProvider<Slash>, SlashHandler {
    private final Configurations<ConfigFile> config;
    private boolean active = false;

    public Rollback(Configurations<ConfigFile> config) {
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
        if (active) {
            event.reply("Rollback already in progress").setEphemeral(true).queue();
            return;
        }
        config.main().active(false);
        active = true;
        event.reply("Rolling back user named").setEphemeral(true).queue();
        Map<Long, String> names = config.secondary(Users.KEY).names();
        names = new HashMap<>(names);
        Guild guild = event.getGuild();
        for (var entry : names.entrySet()) {
            try (var users = config.secondaryWrapped(Users.KEY)) {
                Member complete = guild.retrieveMemberById(entry.getKey()).complete();
                complete.modifyNickname(entry.getValue()).complete();
                users.config().rolledBack(entry.getKey());
            } catch (Exception e) {
                // ignore
            }
        }

        event.getInteraction().getHook().editOriginal("Rolling back role names").queue();
        RenamedRegistry roles = config.secondary(Roles.KEY);
        for (Map.Entry<Long, String> entry : roles.originals().entrySet()) {
            Role role = guild.getRoleById(entry.getKey());
            if (role == null) continue;
            role.getManager().setName(entry.getValue()).complete();
        }

        event.getInteraction().getHook().editOriginal("Rolling back channel names").queue();
        RenamedRegistry channels = config.secondary(Channels.KEY);
        for (Map.Entry<Long, String> entry : channels.originals().entrySet()) {
            GuildChannel channel = guild.getGuildChannelById(entry.getKey());
            if (channel == null) continue;
            channel.getManager().setName(entry.getValue()).complete();
        }

        active = false;
        config.main().active(false);
        config.save();
        event.getInteraction().getHook().editOriginal("Rollback finished").queue();
    }
}
