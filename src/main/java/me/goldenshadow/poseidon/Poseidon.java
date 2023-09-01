package me.goldenshadow.poseidon;

import me.goldenshadow.poseidon.applications.ApplicationManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Poseidon {

    private static JDA api;

    public static void main(String[] args) {
        try {
            initConstants();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        api = JDABuilder.createDefault(Constants.BOT_TOKEN)
                .setActivity(Activity.watching("the seven seas!"))
                .addEventListeners(new ApplicationManager(), new CommandManager())
                .build();
        initCommands();
    }

    public static JDA getApi() {
        return api;
    }

    private static void initCommands() {
        api.updateCommands().addCommands(
                Commands.slash("shells", "A command for shells")
                        .addSubcommands(new SubcommandData("balance", "See how many shells a user has")
                                .addOption(OptionType.USER, "user", "The user you want to see the balance of"))
                        .addSubcommands(new SubcommandData("leaderboard", "See the shell leaderboard"))
                ,
                Commands.slash("manage-shells", "A command for managing shells")
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
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                ,
                Commands.slash("new-member", "Used to register a new guild member")
                        .addOption(OptionType.USER, "user", "The user that should be registered")
                        .addOption(OptionType.STRING, "ign", "The users in game name")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES))

        ).queue();
    }

    private static void initConstants() throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = Poseidon.class.getClassLoader().getResourceAsStream("config.yml");
        Map<String, String> map = yaml.load(inputStream);
        Constants.BOT_TOKEN = map.get("BOT_TOKEN");
        Constants.MAIN_APPLICATION_CATEGORY_ID = map.get("MAIN_APPLICATION_CATEGORY_ID");
        Constants.STAFF_APPLICATION_ROLE_ID = map.get("STAFF_APPLICATION_ROLE_ID");
        Constants.MAIN_SERVER_ID = map.get("MAIN_SERVER_ID");
        Constants.STAFF_SERVER_ID = map.get("STAFF_SERVER_ID");
        Constants.STAFF_APPLICATION_CHANNEL_ID = map.get("STAFF_APPLICATION_CHANNEL_ID");
        assert inputStream != null;
        inputStream.close();
    }
}