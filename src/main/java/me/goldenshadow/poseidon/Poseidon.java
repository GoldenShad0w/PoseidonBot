package me.goldenshadow.poseidon;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.goldenshadow.poseidon.applications.ApplicationManager;
import me.goldenshadow.poseidon.profile.GuildEventManager;
import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.utils.DataProvider;
import me.goldenshadow.poseidon.utils.Leaderboard;
import me.goldenshadow.wynnapi.WynncraftAPI;
import me.goldenshadow.wynnapi.exceptions.APIException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Poseidon {

    private static JDA api;
    private static WynncraftAPI wynnAPI;
    private static GuildEventManager guildEventManager;
    private static DataProvider dataProvider;
    private static final String[] activityArray = {"the seven seas!", "the fishes!", "the ocean waves!", "a very big fish!", "barled!", "you!", "a clownfish!", "a whale!", "atlantis!", "the aquarium!"};

    public static void main(String[] args) {
        wynnAPI = new WynncraftAPI();

        try {
            initConstants();
            ProfileManager.loadFromFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        api = JDABuilder.createDefault(Constants.BOT_TOKEN)
                .setActivity(Activity.watching(activityArray[ThreadLocalRandom.current().nextInt(0, activityArray.length)]))
                .addEventListeners(new ApplicationManager(), new CommandManager(), new ProfileManager(), new Leaderboard())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        dataProvider = new DataProvider();


        initCommands();

        Timer t = new Timer();


        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (guildEventManager != null) {
                        guildEventManager.checkForUpdates();
                    } else {
                        guildEventManager = new GuildEventManager();
                    }
                } catch (APIException e) {
                    sendErrorMessage(new MessageCreateBuilder().addContent(getStringBuilder(e).toString()).build());
                }

            }

            @NotNull
            private static StringBuilder getStringBuilder(APIException e) {
                StringBuilder b = new StringBuilder();
                b.append("**An Exception was thrown while checking for guild events!**");
                b.append("\n```");
                b.append(e.getMessage());
                b.append("\n");
                for (StackTraceElement s : e.getStackTrace()) {
                    b.append(s.toString());
                }
                b.append("\n\n");
                b.append("Is rate limited: ").append(getWynnAPI().isRateLimited());
                b.append("```");
                return b;
            }
        }, 100, 30000);


        Instant currentTime = Instant.now();
        // Create a LocalDateTime object for 12:00 AM UTC
        LocalDateTime midnightUTC = LocalDateTime.ofInstant(currentTime, ZoneId.of("UTC"))
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // Calculate the time difference in milliseconds
        long millisecondsUntilMidnight = midnightUTC.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() - currentTime.toEpochMilli();
        System.out.println("Will update player data in " + (Math.abs(millisecondsUntilMidnight) / 60000) + " minutes");
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendDebugMessage(new MessageCreateBuilder().addContent("**Attempting to update player data...**").build());
                ProfileManager.updatePlayData();
                sendDebugMessage(new MessageCreateBuilder().addContent("**Finished updating player data!**").build());
                getApi().getPresence().setActivity(Activity.watching(activityArray[ThreadLocalRandom.current().nextInt(0, activityArray.length)]));
                sendDebugMessage(new MessageCreateBuilder().addContent("**Set new activity status!**").build());


            }
        }, Math.abs(millisecondsUntilMidnight), TimeUnit.DAYS.toMillis(1));
    }



    public static JDA getApi() {
        return api;
    }

    public static WynncraftAPI getWynnAPI() {
        return wynnAPI;
    }

    private static void initCommands() {

        SlashCommandData leaderboardCommand = Commands.slash("leaderboard", "A command for seeing leaderboards")
                .addSubcommands(new SubcommandData("total", "Get all time leaderboards")
                        .addOption(OptionType.STRING, "type", "The type of leaderboard", true, true))
                .addSubcommands(new SubcommandData("timespan", "Get time span leaderboards")
                        .addOption(OptionType.STRING, "type", "The type of leaderboard", true, true)
                        .addOption(OptionType.INTEGER, "days", "The amount of days", true))
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);


        SlashCommandData manageCommand = Commands.slash("manage-shells", "A command for managing shells")
                .addSubcommands(new SubcommandData("add", "Add shells of a user")
                        .addOption(OptionType.USER, "user", "The user you want to add shells to", true)
                        .addOption(OptionType.INTEGER, "amount", "The amount of shells you want to add", true)
                        .addOption(OptionType.STRING, "reason", "The reason for this change", true)
                )
                .addSubcommands(new SubcommandData("remove", "Remove shells of a user")
                        .addOption(OptionType.USER, "user", "The user you want to remove shells from", true)
                        .addOption(OptionType.INTEGER, "amount", "The amount of shells you want to remove", true)
                        .addOption(OptionType.STRING, "reason", "The reason for this change", true)
                )
                .addSubcommands(new SubcommandData("history", "Shows the shell transaction history of a user")
                        .addOption(OptionType.USER, "user", "The user you to see the history of", true)
                )
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER));

        SlashCommandData newMemberCommand = Commands.slash("new-member", "Used to register a new guild member")
                .addOption(OptionType.USER, "user", "The user that should be registered",true)
                .addOption(OptionType.STRING, "ign", "The users in game name",true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        SlashCommandData registerMemberCommand = Commands.slash("register-member", "Used to register an existing guild member")
                .addOption(OptionType.USER, "user", "The user that should be registered",true)
                .addOption(OptionType.STRING, "ign", "The users in game name",true)
                .addOption(OptionType.STRING, "rank", "The rank the new user should have", true, true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        SlashCommandData resetRolesCommand = Commands.slash("reset-roles", "Used to reset the roles of a user")
                .addOption(OptionType.USER, "user", "The user that should have their roles reset",true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        SlashCommandData setRankCommand = Commands.slash("set-rank", "Used to set the rank of a user")
                .addOption(OptionType.USER, "user", "The user that should have their roles reset",true)
                .addOption(OptionType.STRING, "rank", "The rank the new user should have", true, true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        SlashCommandData changeIgnCommand = Commands.slash("change-ign", "Used to change a registered users ign")
                .addOption(OptionType.USER, "user", "The user whose ign should be changed",true)
                .addOption(OptionType.STRING, "ign", "The new ign", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        SlashCommandData rankMismatchCommand = Commands.slash("rank-mismatch", "Used to check for any disparities between in game ranks and discord ranks")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        SlashCommandData profileCommand = Commands.slash("profile", "Used to see info about a user")
                .addOption(OptionType.USER, "user", "The user",true)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);

        SlashCommandData statsCommand = Commands.slash("stats", "Used to see stats of a player in a time span")
                .addOption(OptionType.USER, "user", "The user", true)
                .addOption(OptionType.INTEGER, "days", "The span of time", true)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);

        SlashCommandData genUUIDCommand = Commands.slash("gen-uuid", "Debug command to force a uuid generation. Don't use this unless you're called _GoldenShadow")
                        .addOption(OptionType.STRING, "ign", "The ign", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        SlashCommandData forceUpdateCommand = Commands.slash("fupd", "Debug command to force a player data update. Don't use this unless you're called _GoldenShadow")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));
        SlashCommandData forceClearCommand = Commands.slash("fcpd", "Debug command to force a player data clearance. Don't use this unless you're called _GoldenShadow")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));

        api.updateCommands().addCommands(manageCommand, newMemberCommand, leaderboardCommand, registerMemberCommand, resetRolesCommand, setRankCommand, changeIgnCommand, rankMismatchCommand, profileCommand, statsCommand, genUUIDCommand, forceUpdateCommand, forceClearCommand).queue();
    }

    private static void initConstants() throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = Poseidon.class.getClassLoader().getResourceAsStream("config.yml");
        Map<Object, Object> map = yaml.load(inputStream);
        Constants.BOT_TOKEN = (String) map.get("BOT_TOKEN");
        Constants.MAIN_APPLICATION_CATEGORY_ID = (String) map.get("MAIN_APPLICATION_CATEGORY_ID");
        Constants.STAFF_APPLICATION_ROLE_ID = (String) map.get("STAFF_APPLICATION_ROLE_ID");
        Constants.MAIN_SERVER_ID = (String) map.get("MAIN_SERVER_ID");
        Constants.STAFF_SERVER_ID = (String) map.get("STAFF_SERVER_ID");
        Constants.STAFF_APPLICATION_CHANNEL_ID = (String) map.get("STAFF_APPLICATION_CHANNEL_ID");

        Constants.STARFISH_ROLES = readArray(map.get("STARFISH_ROLES"));
        Constants.MANATEE_ROLES = readArray(map.get("MANATEE_ROLES"));
        Constants.PIRANHA_ROLES = readArray(map.get("PIRANHA_ROLES"));
        Constants.BARRACUDA_ROLES = readArray(map.get("BARRACUDA_ROLES"));
        Constants.ANGLER_ROLES = readArray(map.get("ANGLER_ROLES"));
        Constants.HAMMERHEAD_ROLES = readArray(map.get("HAMMERHEAD_ROLES"));
        Constants.SAILFISH_ROLES = readArray(map.get("SAILFISH_ROLES"));
        Constants.DOLPHIN_ROLES = readArray(map.get("DOLPHIN_ROLES"));
        Constants.NARWHAL_ROLES = readArray(map.get("NARWHAL_ROLES"));
        Constants.COMMON_ROLES = readArray(map.get("COMMON_ROLES"));
        Constants.EX_MEMBER_ROLE = (String) map.get("EX_MEMBER_ROLE");
        Constants.MAIN_GUILD_GENERAL_CHANNEL_ID = (String) map.get("MAIN_GUILD_GENERAL_CHANNEL_ID");
        Constants.GUILD_LOG_CHANNEL_ID = (String) map.get("GUILD_LOG_CHANNEL_ID");
        Constants.GUILD_FAQ_CHANNEL_ID = (String) map.get("GUILD_FAQ_CHANNEL_ID");
        Constants.GUILD_ROLE_CHANEL_ID = (String) map.get("GUILD_ROLE_CHANEL_ID");
        Constants.GUILD_WAR_FAQ_CHANNEL_ID = (String) map.get("GUILD_WAR_FAQ_CHANNEL_ID");
        assert inputStream != null;

        inputStream.close();
    }

    private static String[] readArray(Object object) {
        if (object instanceof ArrayList<?> l) {
            List<String> list = new ArrayList<>();
            for (Object o1 : l) {
                if (o1 instanceof String string) {
                    list.add(string);
                }
            }
            return list.toArray(new String[]{});
        }
        return new String[]{};
    }

    public static DataProvider getDataProvider() {
        return dataProvider;
    }

    public static void sendErrorMessage(MessageCreateData data) {
        Guild g = getApi().getGuildById("843938440391557150");
        assert g != null;
        TextChannel c = g.getTextChannelById("1148621530297815120");
        assert c != null;
        c.sendMessage(data).queue();
    }

    public static void sendDebugMessage(MessageCreateData data) {
        Guild g = getApi().getGuildById("843938440391557150");
        assert g != null;
        TextChannel c = g.getTextChannelById("1177939286310531172");
        assert c != null;
        c.sendMessage(data).queue();
    }


}