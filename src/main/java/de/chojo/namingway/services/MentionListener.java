package de.chojo.namingway.services;

import de.chojo.jdautil.util.Consumers;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Users;
import dev.chojo.ocular.Configurations;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import static java.util.concurrent.ThreadLocalRandom.current;

public class MentionListener extends ListenerAdapter {
    private final Configurations<ConfigFile> config;

    public MentionListener(Configurations<ConfigFile> config) {
        this.config = config;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!config.main().active()) return;

        Message message = event.getMessage();
        if (!message.isFromGuild()) return;
        if (message.getAuthor().isBot()) return;
        if (message.getAuthor().isSystem()) return;

        if (message.getType() == MessageType.INLINE_REPLY) {
            if (message.getReferencedMessage() == null) return;
            Member referenced;
            try {
                referenced = event.getGuild().retrieveMember(message.getReferencedMessage().getAuthor()).complete();
            } catch (Exception e) {
                // ignore
                return;
            }
            if (referenced == null) return;
            diceNames(event.getMember(), referenced);
        } else if (!message.getMentions().getMentions(Message.MentionType.USER).isEmpty()) {
            for (IMentionable mention : message.getMentions().getMentions(Message.MentionType.USER)) {
                if (mention.getIdLong() == event.getAuthor().getIdLong()) continue;
                Member member;
                try {
                    member = event.getGuild().retrieveMemberById(mention.getIdLong()).complete();
                } catch (Exception e) {
                    // ignore
                    return;
                }
                diceNames(event.getMember(), member);
            }
        } else {
            if (current().nextDouble() < 0.025) {
                switchNames(config.secondary(Users.KEY).randomAdditionalName(), event.getMember());
            }

        }
    }

    private void diceNames(Member first, Member second) {
        if (current().nextDouble() <= 0.5) {
            if (current().nextDouble() <= 0.2) {
                // Bring back a lost name
                switchNames(config.secondary(Users.KEY).randomName(), current().nextDouble() >= 0.5 ? first : second);
            } else if (current().nextDouble() <= 0.1) {
                // Introduce an additional name
                switchNames(config.secondary(Users.KEY).randomAdditionalName(), current().nextDouble() >= 0.5 ? second : first);
            }
            return;
        }
        if (current().nextDouble() >= 0.5) {
            switchNames(first, second);
        } else {
            switchNames(second, first);
        }
    }

    private void switchNames(Member nameProvider, Member target) {
        if (target.getUser().isBot()) return;
        if (nameProvider.getUser().isBot()) return;
        switchNames(nameProvider.getEffectiveName(), target);
    }

    private void switchNames(String newName, Member target) {
        if (target.getUser().isBot()) return;
        try (var wrapper = config.secondaryWrapped(Users.KEY)) {
            wrapper.config().addIfAbsent(target);
            if (target.getGuild().getSelfMember().canInteract(target)) {
                target.modifyNickname(newName).queue(RestAction.getDefaultSuccess(), Consumers.empty());
            }
        }
    }
}
