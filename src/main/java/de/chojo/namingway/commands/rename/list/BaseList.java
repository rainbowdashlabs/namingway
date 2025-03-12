package de.chojo.namingway.commands.rename.list;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import de.chojo.namingway.db.RenamedRegistry;
import dev.chojo.ocular.Configurations;
import dev.chojo.ocular.key.Key;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BaseList implements SlashHandler {
    private final Configurations<?> configurations;
    private final Key<RenamedRegistry> renamed;
    private final BiFunction<Long, Guild, IMentionable> mentionable;

    public BaseList(Configurations<?> configurations, Key<RenamedRegistry> renamed, BiFunction<Long, Guild, IMentionable> mentionable) {
        this.configurations = configurations;
        this.renamed = renamed;
        this.mentionable = mentionable;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent slash, EventContext eventContext) {
        RenamedRegistry renamed = configurations.secondary(this.renamed);
        var list = renamed.renamedIds().stream()
                          .map(e -> mentionable.apply(e, slash.getGuild()))
                          .filter(Objects::nonNull)
                          .sorted()
                          .map(e -> "%s -> %s".formatted(e.getAsMention(), renamed.newName(e)))
                          .collect(Collectors.joining("\n"));
        slash.reply(list).setEphemeral(true).queue();
    }
}
