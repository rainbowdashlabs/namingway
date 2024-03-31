package de.chojo.namingway.services;

import de.chojo.jdautil.util.Consumers;
import de.chojo.namingway.configuration.ConfigFile;
import de.chojo.namingway.db.Users;
import de.chojo.namingway.util.config.JacksonConfig;
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
    private final JacksonConfig<ConfigFile> config;

    public MentionListener(JacksonConfig<ConfigFile> config) {
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
            return;
        }

        if (!message.getMentions().getMentions(Message.MentionType.USER).isEmpty()) {
            IMentionable iMentionable = message.getMentions().getMentions(Message.MentionType.USER).get(0);
            Member member;
            try {
                member = event.getGuild().retrieveMemberById(iMentionable.getIdLong()).complete();
            } catch (Exception e) {
                // ignore
                return;
            }
            diceNames(event.getMember(), member);
        }
    }

    private void diceNames(Member first, Member second) {
        if(current().nextDouble() <= 0.1){
            // Bring back a lost name
            switchNames(config.secondary(Users.KEY).randomName(), current().nextDouble() >= 0.5 ? first : second);
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
            target.modifyNickname(newName).queue(RestAction.getDefaultSuccess(), Consumers.empty());
        }
    }
}
