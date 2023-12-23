package me.goldenshadow.poseidon.profile;


import me.goldenshadow.poseidon.Constants;
import me.goldenshadow.poseidon.Poseidon;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuild;
import me.goldenshadow.wynnapi.v3.guild.data.GuildRank;
import me.goldenshadow.wynnapi.v3.guild.data.GuildMembers;
import me.goldenshadow.wynnapi.v3.player.WynncraftPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.time.Duration;
import java.util.*;

public class GuildEventManager {


    private List<GuildMembers.Member> oldState;

    public GuildEventManager() {
        Poseidon.getDataProvider().updateGuildData();
        oldState = new ArrayList<>(Poseidon.getDataProvider().getGuildMap().values());
    }

    public void checkForUpdates() {
        Poseidon.getDataProvider().updateGuildData();
        List<GuildMembers.Member> newState = new ArrayList<>(Poseidon.getDataProvider().getGuildMap().values());
        for (GuildMembers.Member member : newState) {
            Optional<GuildMembers.Member> oldMemberProfile = oldState.stream().filter(x -> x.uuid().equals(member.uuid())).findFirst();
            oldMemberProfile.ifPresentOrElse(old -> {
                //member was and is still in the guild
                if (member.rank() != old.rank()) {
                    postRankUpdate(member.ign(), old.rank(), member.rank());
                }
                oldState.remove(old);
            }, () -> {
                //member has joined the guild since last check
                postJoin(member.ign());
            });
        }
        for (GuildMembers.Member m : oldState) {
            //any entries left are player who left the guild, as players who are still in it were removed from this list above
            postLeave(m);
        }
        oldState = new ArrayList<>(newState);
    }

    private void postRankUpdate(String ign, GuildRank oldRank, GuildRank newRank) {
        Guild main = Poseidon.getApi().getGuildById(Constants.MAIN_SERVER_ID);
        if (main != null) {
            TextChannel channel = main.getTextChannelById(Constants.GUILD_LOG_CHANNEL_ID);
            if (channel != null) {
                MessageCreateData m = new MessageCreateBuilder().addContent("🟦<t:" + (System.currentTimeMillis() / 1000L) + ":d> <t:" + (System.currentTimeMillis() / 1000L) + ":t> | **" + ign + "** | " + oldRank.toString() + " **➜** " + newRank.toString()).build();
                channel.sendMessage(m).queue();
            }
        }
    }

    private void postJoin(String ign) {
        Guild main = Poseidon.getApi().getGuildById(Constants.MAIN_SERVER_ID);
        if (main != null) {
            TextChannel channel = main.getTextChannelById(Constants.GUILD_LOG_CHANNEL_ID);
            if (channel != null) {
                MessageCreateData m = new MessageCreateBuilder().addContent("🟩<t:" + (System.currentTimeMillis() / 1000L) + ":d> <t:" + (System.currentTimeMillis() / 1000L) + ":t> | **" + ign + "** joined the guild! |").build();
                channel.sendMessage(m).queue();
            }
        }
    }


    private void postLeave(GuildMembers.Member guildMember) {
        Guild main = Poseidon.getApi().getGuildById(Constants.MAIN_SERVER_ID);
        if (main != null) {
            TextChannel channel = main.getTextChannelById(Constants.GUILD_LOG_CHANNEL_ID);
            if (channel != null) {
                WynncraftPlayer player = Poseidon.getWynnAPI().v3().player().mainStatsUUID(guildMember.uuid().toString()).run();
                MessageCreateData m = new MessageCreateBuilder().addContent("🟥<t:" + (System.currentTimeMillis() / 1000L) + ":d> <t:" + (System.currentTimeMillis() / 1000L) + ":t> | **" + player.username() + "** left the guild! | " + guildMember.rank().toString() + " | Member for **" + Duration.between(guildMember.joined().toInstant(), new Date().toInstant()).abs().toDays() + " days** | Last seen **" + Duration.between(player.lastJoin().toInstant(), new Date().toInstant()).abs().toDays() + "** days ago").build();
                channel.sendMessage(m).queue();
            }
        }
    }






}
