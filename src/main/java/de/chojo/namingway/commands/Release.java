package de.chojo.namingway.commands;

import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashProvider;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Channels;
import de.chojo.namingway.db.RenamedRegistry;
import de.chojo.namingway.db.Roles;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

import java.util.Map;

import static de.chojo.jdautil.interactions.slash.Argument.bool;
import static org.slf4j.LoggerFactory.getLogger;

public class Release implements SlashProvider<Slash>, SlashHandler {
    private static final Logger log = getLogger(Release.class);
    private final Configurations<ConfigFile> config;

    public Release(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public Slash slash() {
        return Slash.of("release", "Release planned changes and activate bot features")
                    .adminCommand()
                    .guildOnly()
                    .unlocalized()
                    .command(this)
                    .argument(bool("confirm", "true to confirm").asRequired())
                    .build();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        if (!event.getOption("confirm").getAsBoolean()) {
            event.reply("Please confirm").setEphemeral(true).queue();
            return;
        }

        config.main().active(true);
        config.save();

        event.reply("Adjusting role names").setEphemeral(true).queue();

        Guild guild = event.getGuild();
        RenamedRegistry roles = config.secondary(Roles.KEY);
        for (Map.Entry<Long, String> entry : roles.newNames().entrySet()) {
            Role role = guild.getRoleById(entry.getKey());
            if (role == null) continue;
            roles.renamed(role, role.getName());
            while (true) {
                try {
                    role.getManager().setName(entry.getValue()).complete();
                } catch (Exception e) {
                    log.error("Failed to rename role", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    continue;
                }
                break;
            }
        }

        event.getInteraction().getHook().editOriginal("Adjusting channel names").queue();
        RenamedRegistry channels = config.secondary(Channels.KEY);
        for (Map.Entry<Long, String> entry : channels.newNames().entrySet()) {
            GuildChannel channel = guild.getGuildChannelById(entry.getKey());
            if (channel == null) continue;
            channels.renamed(channel, channel.getName());
            while (true) {
                try {
                    channel.getManager().setName(entry.getValue()).complete();
                } catch (Exception e) {
                    log.error("Failed to rename channel", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    continue;
                }
                break;
            }
        }

        config.save();

        event.getInteraction().getHook().editOriginal("Bot activated").queue();
    }
}
