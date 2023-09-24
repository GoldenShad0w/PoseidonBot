package me.goldenshadow.poseidon;

import me.goldenshadow.poseidon.applications.ApplicationManager;
import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.shells.ShellManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Poseidon {

    private static JDA api;

    public static void main(String[] args) {
        try {
            initConstants();
            ProfileManager.loadFromFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        api = JDABuilder.createDefault(Constants.BOT_TOKEN)
                .setActivity(Activity.watching("the seven seas!"))
                .addEventListeners(new ApplicationManager(), new CommandManager(), new ShellManager(), new ProfileManager())
                .build();
        initCommands();
    }

    public static JDA getApi() {
        return api;
    }

    private static void initCommands() {


        SlashCommandData shellCommand = Commands.slash("shells", "A command for shells")
                .addSubcommands(new SubcommandData("balance", "See how many shells a user has")
                        .addOption(OptionType.USER, "user", "The user you want to see the balance of"))
                .addSubcommands(new SubcommandData("leaderboard", "See the shell leaderboard"))
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);

        SlashCommandData manageCommand = Commands.slash("manage-shells", "A command for managing shells")
                .addSubcommands(new SubcommandData("add", "Add shells of a user")
                        .addOption(OptionType.USER, "user", "The user you want to add shells to")
                        .addOption(OptionType.INTEGER, "amount", "The amount of shells you want to add")
                        .addOption(OptionType.STRING, "reason", "The reason for this change")
                )
                .addSubcommands(new SubcommandData("remove", "Remove shells of a user")
                        .addOption(OptionType.USER, "user", "The user you want to remove shells from")
                        .addOption(OptionType.INTEGER, "amount", "The amount of shells you want to remove")
                        .addOption(OptionType.STRING, "reason", "The reason for this change")
                )
                .addSubcommands(new SubcommandData("history", "Shows the shell transaction history of a user")
                        .addOption(OptionType.USER, "user", "The user you to see the history of")
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

        api.updateCommands().addCommands(manageCommand, newMemberCommand, shellCommand, registerMemberCommand, resetRolesCommand, setRankCommand).queue();
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
}