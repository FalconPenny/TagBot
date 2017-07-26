package me.falconpenny.tagbot;

import me.falconpenny.tagbot.data.Tag;
import me.falconpenny.tagbot.data.TagUser;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TagLogger extends ListenerAdapter {
    private final Map<Long, TagUser> users = new HashMap<>();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Set<User> users = event.getMessage().getMentionedUsers().stream().distinct().collect(Collectors.toSet());
        Set<Long> userLongs = new HashSet<>();
        if (users.size() < 8) {
            return;
        }
        users.stream().mapToLong(User::getIdLong).forEach(userLongs::add);
        long tagEpoch = event.getMessage().getCreationTime().toInstant().toEpochMilli();
        TagUser user = this.users.getOrDefault(event.getAuthor().getIdLong(), new TagUser(event.getAuthor().getIdLong()));
        user.setLastTag(tagEpoch);
        Tag tag = new Tag(users.size(), userLongs, tagEpoch);
        user.addTag(tag);
        int lastMinute = user.getTags().stream().filter(it -> it.getEpoch() >= (tagEpoch - TimeUnit.MINUTES.toMillis(1))).mapToInt(Tag::getDistinct).sum();
        if (lastMinute >= 18 && user.getLastAlert() >= (user.getLastAlert() + TimeUnit.MINUTES.toMillis(3))) {
            TextChannel channel = event.getMember().getGuild().getPublicChannel();
            if (channel.canTalk()) {
                user.setLastAlert(System.currentTimeMillis());
                Logging.log.info("Sending alert about", user.getUserId(), "to the publicchannel", channel.getIdLong(), ',', channel.getGuild().getName());
                try {
                    channel.sendMessage(new MessageBuilder()
                            .append("**TAG ALERT** (AUTOMATED PER 3MIN UNTIL NO MORE TAGS)\n")
                            .appendCodeBlock("" + // To get rid of the IntelliJ hint below.
                                            Main.charVerify("DELETING THIS:", 16) + "90 SEC FROM SENT" +
                                            "\n" + Main.charVerify("USER ID:", 16) + user.getUserId() +
                                            "\n" + Main.charVerify("USER TAG:", 16) + event.getAuthor().getAsMention() +
                                            "\n" + Main.charVerify("TAGS LAST MSG:", 16) + users.size() +
                                            "\n" + Main.charVerify("TAGS LAST 60S:", 16) + lastMinute

                                    , "")

                            .build()
                    ).queue(msg -> {
                        Logging.log.info("Sent successfully.", user.getUserId(), "----", channel.getGuild().getName());
                        msg.delete().queueAfter(90, TimeUnit.SECONDS);
                    });
                } catch (Exception ex) {
                    Logging.log.warn("Couldn't send warning to", channel.getGuild().getIdLong(), " [" + channel.getGuild().getName() + "] about", user.getUserId());
                }
            }
        }
        this.users.put(event.getAuthor().getIdLong(), user);
    }
}
