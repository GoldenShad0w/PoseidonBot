package me.goldenshadow.poseidon.applications;

import me.goldenshadow.poseidon.Constants;
import me.goldenshadow.poseidon.Poseidon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApplicationManager extends ListenerAdapter {


    private final HashMap<String, ApplicationData> cachedChannels = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild().getId().equals(Constants.MAIN_SERVER_ID)) {
            Category c = event.getChannel().asTextChannel().getParentCategory();
            if (event.getChannel().getType() == ChannelType.TEXT) {
                if (event.getChannel().asTextChannel().getHistoryFromBeginning(10).complete().size() == 2) {
                    if (c != null && c.getId().equals(Constants.MAIN_APPLICATION_CATEGORY_ID)) {
                        Guild staffServer = Poseidon.getApi().getGuildById(Constants.STAFF_SERVER_ID);
                        if (staffServer != null) {
                            TextChannel applicationChannel = staffServer.getTextChannelById(Constants.STAFF_APPLICATION_CHANNEL_ID);
                            if (applicationChannel != null) {

                                MessageEmbed embed = new EmbedBuilder()
                                        .setTitle("Application " + event.getChannel().getName().replaceAll(".*-", ""))
                                        .setDescription(event.getChannel().asTextChannel().getJumpUrl() + "\n" + "**Status:** `open`")
                                        .setColor(Color.cyan)
                                        .build();

                                MessageCreateData data = new MessageCreateBuilder()
                                        .addContent("<@&" + Constants.STAFF_APPLICATION_ROLE_ID + ">")
                                        .setAllowedMentions(List.of(Message.MentionType.ROLE))
                                        .addEmbeds(List.of(embed))
                                        .build();

                                MessageCreateAction messageCreateAction = applicationChannel.sendMessage(data);

                                Message m = messageCreateAction.complete();
                                m.addReaction(Emoji.fromUnicode("👍")).complete();
                                m.addReaction(Emoji.fromUnicode("🤷‍♂️")).complete();
                                m.addReaction(Emoji.fromUnicode("👎")).complete();
                                ThreadChannelAction threadChannelAction = m.createThreadChannel(event.getChannel().getName().replaceAll(".*-", ""));
                                ThreadChannel threadChannel = threadChannelAction.complete();

                                cachedChannels.put(event.getChannel().getId(), new ApplicationData(m.getId(), embed, event.getChannel().asTextChannel().getJumpUrl()));

                                MessageCreateData reminder = new MessageCreateBuilder()
                                        .addContent("<@&" + Constants.STAFF_APPLICATION_ROLE_ID + "> 8 hours since app creation!")
                                        .setAllowedMentions(List.of(Message.MentionType.ROLE))
                                        .build();

                                if (threadChannel != null) {
                                    threadChannel.sendMessage(reminder).queueAfter(8, TimeUnit.HOURS);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
        Iterator<String> it = cachedChannels.keySet().iterator();
        while (it.hasNext()) {
            String s = it.next();
            Guild guild = Poseidon.getApi().getGuildById(Constants.MAIN_SERVER_ID);
            if (guild != null) {
                if (guild.getTextChannelById(s) != null) {
                    if (event.getChannel().getId().equals(s)) {
                        //cached channel was renamed
                        Guild staffGuild = Poseidon.getApi().getGuildById(Constants.STAFF_SERVER_ID);
                        assert staffGuild != null;
                        TextChannel textChannel = staffGuild.getTextChannelById(Constants.STAFF_APPLICATION_CHANNEL_ID);
                        assert textChannel != null;
                        ApplicationData data = cachedChannels.get(s);

                        MessageEmbed oldEmbed = data.embed();
                        if (oldEmbed.getDescription() != null && event.getNewValue() != null) {
                            MessageEmbed newEmbed = new EmbedBuilder()
                                    .setTitle(oldEmbed.getTitle())
                                    .setDescription(data.link() + "\n**Status: **`" + event.getNewValue().replaceAll("-.*", "") + "`")
                                    .setColor(oldEmbed.getColor())
                                    .build();
                            textChannel.editMessageEmbedsById(data.messageID(), newEmbed).queue();
                        }


                    }
                } else {
                    it.remove();
                    //cached channel doesn't exist anymore, so it can be removed from cache
                }
            }
        }

    }
}
