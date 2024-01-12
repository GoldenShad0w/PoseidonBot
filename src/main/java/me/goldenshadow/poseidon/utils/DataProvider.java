package me.goldenshadow.poseidon.utils;

import me.goldenshadow.poseidon.Poseidon;
import me.goldenshadow.poseidon.profile.PlayData;
import me.goldenshadow.poseidon.profile.Profile;
import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.profile.Rank;
import me.goldenshadow.wynnapi.exceptions.APIException;
import me.goldenshadow.wynnapi.v3.guild.data.GuildMembers;
import me.goldenshadow.wynnapi.v3.player.WynncraftPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class DataProvider {

    private HashMap<UUID, GuildMembers.Member> guildMap = new HashMap<>();

    public DataProvider() {
        updateGuildData();
    }

    public void updateGuildData() {
        guildMap = Poseidon.getWynnAPI().v3().guild().guildStats("The Aquarium").run().members().getAll();
    }

    public HashMap<UUID, GuildMembers.Member> getGuildMap() {
        return guildMap;
    }

    public boolean isInGuild(@Nullable UUID uuid) {
        if (uuid == null) return false;
        return guildMap.containsKey(uuid);
    }

    public void updateData(Profile profile) {
        System.out.println("Attempting to update profile of " + profile.getInGameName());
        try {
            WynncraftPlayer player = Poseidon.getWynnAPI().v3().player().mainStatsUUID(profile.getMcUUID().toString()).run();
            profile.setLastSeen(player.lastJoin());
            profile.addData(new PlayData(player.globalData().wars(), (long) player.playtime(), guildMap.get(profile.getMcUUID()).contributed(), profile.getShells()));
            System.out.println("\033[0;32m" + "Success!");
            //Poseidon.sendDebugMessage(new MessageCreateBuilder().addContent("Updated data of " + profile.getInGameName()).build());
        } catch (APIException | NullPointerException e) {
            System.out.println("\033[0;31m" + "Failed!");
            //Poseidon.sendDebugMessage(new MessageCreateBuilder().addContent("Failed to update data of " + profile.getInGameName()).build());
        }
    }


    public List<String> getGlobalLeaderboardData(LeaderboardType type) {
        List<String> list = new ArrayList<>();
        List<Profile> profileList = ProfileManager.getGuildMemberProfiles(false);
        String format = "`%d. %s - %s`\n";

        switch (type) {
            case SHELLS -> {
                profileList.sort(Comparator.comparingInt(Profile::getShells).reversed());
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), String.valueOf(profileList.get(i).getShells())));
                }
            }
            case WARS -> {
                profileList.sort(Comparator.comparingInt(p -> p.getPlayData(-1).wars()));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), String.valueOf(profileList.get(i).getPlayData(-1).wars())));
                }
            }
            case XP -> {
                profileList.sort(Comparator.comparingLong(p -> p.getPlayData(-1).xp()));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), String.valueOf(profileList.get(i).getPlayData(-1).xp())));
                }
            }
            case PLAYTIME -> {
                profileList.sort(Comparator.comparingLong(p -> p.getPlayData(-1).playtime()));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), (int) (profileList.get(i).getPlayData(-1).playtime()) + "h"));
                }
            }
            case INACTIVITY -> {
                profileList.sort(Comparator.comparing(Profile::getLastSeen).reversed());
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), formatDate(profileList.get(i).getLastSeen())));
                }
            }
            case CONTRIBUTION -> {
                profileList.removeIf(p -> p.getRank().ordinal() > 3);
                profileList.sort(Comparator.comparingDouble(p -> {
                    PlayData d = p.getPlayData(-1);
                    return (d.wars() * 10) + ((double) p.getShells() / 10) + ((double) d.xp() / 1000000) + d.playtime();
                }));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    PlayData d = profileList.get(i).getPlayData(-1);
                    String s = "Wars: " + d.wars() + " | Shells: " + profileList.get(i).getShells() + " | Playtime: " + d.playtime() + "h | XP: " + d.getFriendlyXP() + " | Rank: " + profileList.get(i).getRank().toString();
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), s));
                }
            }
        }
        return list;
    }

    public List<String> getTimespanLeaderboardData(LeaderboardType type, int days) {
        if (1 > days || days > 30) throw new IllegalArgumentException("days must be between 1 and 30");
        if (type == LeaderboardType.INACTIVITY) throw new IllegalArgumentException("Inactivity cannot be displayed as a timespan");
        List<String> list = new ArrayList<>();
        List<Profile> profileList = ProfileManager.getGuildMemberProfiles(false);
        String format = "`%d. %s - %s`\n";
        switch (type) {
            case SHELLS -> {
                profileList.sort(Comparator.comparingInt(p -> p.getPlayData(days).shells()));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), String.valueOf(profileList.get(i).getPlayData(days).shells())));
                }
            }
            case WARS -> {
                profileList.sort(Comparator.comparingInt(p -> p.getPlayData(days).wars()));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), String.valueOf(profileList.get(i).getPlayData(days).wars())));
                }
            }
            case XP -> {
                profileList.sort(Comparator.comparingLong(p -> p.getPlayData(days).xp()));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), String.valueOf(profileList.get(i).getPlayData(days).xp())));
                }
            }
            case PLAYTIME -> {
                profileList.sort(Comparator.comparingLong(p -> p.getPlayData(days).playtime()));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), (int) (profileList.get(i).getPlayData(days).playtime()) + "h"));
                }
            }
            case CONTRIBUTION -> {
                profileList.removeIf(p -> p.getRank().ordinal() > 3);
                profileList.sort(Comparator.comparingDouble(p -> {
                    PlayData d = p.getPlayData(days);
                    return (d.wars() * 10) + ((double) d.shells() / 10) + ((double) d.xp() / 1000000) + d.playtime();
                }));
                Collections.reverse(profileList);
                for (int i = 0; i < profileList.size(); i++) {
                    PlayData d = profileList.get(i).getPlayData(days);
                    String s = "Wars: " + d.wars() + " | Shells: " + d.shells() + " | Playtime: " + d.playtime() + "h | XP: " + d.getFriendlyXP() + " | Rank: " + profileList.get(i).getRank().toString();
                    list.add(format.formatted((i + 1), profileList.get(i).getInGameName(), s));
                }
            }
        }
        return list;
    }

    public MessageEmbed getProfileData(Profile profile) {
        if (!getGuildMap().containsKey(profile.getMcUUID())) return new EmbedBuilder().setTitle("Error").setDescription("The specified user isn't in the guild or isn't registered!").build();

        GuildMembers.Member m = getGuildMap().get(profile.getMcUUID());
        String s = "";
        s = s.concat("**Rank: **" + profile.getRank().prefix + "(" + profile.getRank().getWynncraftRank().getStars() + ")\n");
        s = s.concat("**Joined: **" + formatDate(m.joined()) + "\n");
        s = s.concat("**Shells: **" + profile.getShells() + "\n");
        s = s.concat("**Wars: **" + profile.getPlayData(-1).wars()) + "\n";
        s = s.concat("**XP Donated: **" + m.contributed() + "\n");
        s= s.concat("**Playtime: **" + (int) (profile.getPlayData(-1).playtime()  * 4.7 / 60) + "h\n");


        return new EmbedBuilder()
                .setTitle(profile.getInGameName() + "'s Profile")
                .setDescription(s)
                .setColor(Color.cyan)
                .build();
    }

    public MessageEmbed getTimespanOverviewData(Profile profile, int days) {
        if (1 > days || days > 30) throw new IllegalArgumentException("days must be between 1 and 30");

        if (!isInGuild(profile.getMcUUID())) return new EmbedBuilder().setTitle("Error").setDescription("The specified user isn't in the guild or isn't registered!").build();

        String s = "";

        PlayData data = profile.getPlayData(days);

        s = s.concat("**Shells: **" + data.shells() + "\n");
        s = s.concat("**Wars: **" + data.wars() + "\n");
        s = s.concat("**XP Donated: **" + data.xp() + "\n");
        s = s.concat("**Playtime: **" + (int) (data.playtime()) + "h\n");


        return new EmbedBuilder()
                .setTitle(profile.getInGameName() + "'s Stats (Last " + days + " days)")
                .setDescription(s)
                .setColor(Color.yellow)
                .build();
    }

    public enum LeaderboardType {
        WARS(Color.red, "Wars"),
        PLAYTIME(Color.blue, "Playtime"),
        SHELLS(Color.orange, "Shells"),
        INACTIVITY(Color.magenta, "Inactivity"),
        XP(Color.green, "Guild XP"),
        CONTRIBUTION(Color.yellow, "Contribution");


        private final Color c;
        private final String displayName;
        LeaderboardType(Color embedColor, String displayName) {
            c = embedColor;
            this.displayName = displayName;
        }

        public Color getEmbedColor() {
            return c;
        }

        public static String[] getStringValues() {
            List<String> list = new ArrayList<>();
            for (LeaderboardType t :values()) {
                list.add(t.toString());
            }
            return list.toArray(String[]::new);
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private String formatDate(Date date) {
        Instant i = Instant.now();
        long d = Duration.between(date.toInstant(), i).abs().toDays();
        return d > 0 ? d + " days" : "Less than a day";
    }

}
