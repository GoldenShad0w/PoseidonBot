package me.goldenshadow.poseidon.applications;

import me.goldenshadow.poseidon.Constants;
import me.goldenshadow.poseidon.Poseidon;
import me.goldenshadow.wynnapi.exceptions.APIException;
import me.goldenshadow.wynnapi.v3.player.WynncraftPlayer;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationManager extends ListenerAdapter {


    private final HashMap<String, ApplicationData> cachedChannels = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!(event.getChannel() instanceof TextChannel)) return;
        if (event.getGuild().getId().equals(Constants.MAIN_SERVER_ID)) {
            Category c = event.getChannel().asTextChannel().getParentCategory();
            if (event.getChannel().getType() == ChannelType.TEXT) {
                if (event.getChannel().asTextChannel().getHistoryFromBeginning(10).complete().size() == 2) {
                    if (c != null && c.getId().equals(Constants.MAIN_APPLICATION_CATEGORY_ID)) {
                        if (!cachedChannels.containsKey(event.getChannel().getId())) {
                            Guild staffServer = Poseidon.getApi().getGuildById(Constants.STAFF_SERVER_ID);
                            if (staffServer != null) {
                                TextChannel applicationChannel = staffServer.getTextChannelById(Constants.STAFF_APPLICATION_CHANNEL_ID);
                                if (applicationChannel != null) {

                                    String ign = null;
                                    Pattern linkPattern = Pattern.compile("https://wynncraft\\.com/stats/player/(\\w+)");
                                    Matcher matcher = linkPattern.matcher(event.getMessage().getContentRaw());
                                    if (matcher.find()) {
                                        ign = matcher.group().replace("https://wynncraft.com/stats/player/", "");
                                    }

                                    WynncraftPlayer player = null;
                                    if (ign != null) {
                                        try {
                                            player = Poseidon.getWynnAPI().v3().player().mainStats(ign).run();
                                        } catch (APIException ignored) {}
                                    }

                                    String desc = event.getChannel().asTextChannel().getJumpUrl() + "\n**Status:** `%s`\n**Wynn First Join: ** `" + (player != null ? player.firstJoin().toString() : "Unknown") + "`\n**Wynn Total Playtime: **`" + (player != null ? (int) (player.playtime() * 4.7 / 60) + "h" : "Unknown") + "`\n**Wynn Total Wars: **`" + (player != null ? player.globalData().wars() : "Unknown") + "`" + (ign != null && Constants.BLACKLIST.contains(ign) ? "\n⚠️ **Player is on ANO blacklist** ⚠️" : "");



                                    MessageEmbed embed = new EmbedBuilder()
                                            .setTitle("Application " + event.getChannel().getName().replaceAll(".*-", ""))
                                            .setDescription(desc.formatted("open"))
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

                                    cachedChannels.put(event.getChannel().getId(), new ApplicationData(m.getId(), embed, desc));



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
                                    .setDescription(data.description().formatted(event.getNewValue().replaceAll("-.*", "")))
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
