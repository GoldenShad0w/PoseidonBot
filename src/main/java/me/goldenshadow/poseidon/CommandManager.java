package me.goldenshadow.poseidon;

import me.goldenshadow.poseidon.profile.Profile;
import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.profile.Rank;
import me.goldenshadow.poseidon.utils.DataProvider;
import me.goldenshadow.poseidon.utils.Leaderboard;
import me.goldenshadow.poseidon.utils.ShellTransaction;
import me.goldenshadow.wynnapi.exceptions.APIException;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuild;
import me.goldenshadow.wynnapi.v3.guild.data.GuildMembers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandManager extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("profile")) {
            OptionMapping option = event.getOption("user");
            if (option != null && option.getAsMember() != null) {
                Profile p = ProfileManager.getProfile(option.getAsMember().getId());
                if (p != null) {
                    MessageCreateData data = new MessageCreateBuilder()
                            .addEmbeds(Poseidon.getDataProvider().getProfileData(p))
                            .build();
                    event.reply(data).setEphemeral(true).queue();
                } else {
                    MessageEmbed embed = new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("This user doesn't have a registered profile!")
                            .setColor(Color.RED)
                            .build();

                    MessageCreateData data = new MessageCreateBuilder()
                            .addEmbeds(embed)
                            .build();
                    event.reply(data).setEphemeral(true).queue();
                }
            }  else {
                event.reply("Not a valid user!").setEphemeral(true).queue();
            }
        }
        else if (event.getName().equals("manage-shells")) {
            if (event.getSubcommandName() != null && (event.getSubcommandName().equals("add") || event.getSubcommandName().equals("remove"))) {
                OptionMapping userOption = event.getOption("user");
                OptionMapping amountOption = event.getOption("amount");
                if (amountOption == null) {
                    event.reply("Amount cannot be null!").setEphemeral(true).queue();
                    return;
                }
                OptionMapping reasonOption = event.getOption("reason");
                if (reasonOption == null) {
                    event.reply("Reason cannot be null!").setEphemeral(true).queue();
                    return;
                }
                if (userOption != null && userOption.getAsMember() != null) {
                    Profile profile = ProfileManager.getProfile(userOption.getAsMember().getId());
                    if (profile != null) {
                        int old = profile.getShells();
                        if (event.getSubcommandName().equals("add")) {
                            profile.addShells(amountOption.getAsInt(), reasonOption.getAsString());
                        } else {
                            profile.removeShells(amountOption.getAsInt(), reasonOption.getAsString());
                        }
                        event.reply("Changed " + profile.getInGameName() + "'s balance from " + old + " to " + profile.getShells() + "!").setEphemeral(true).queue();
                    } else {
                        event.reply("This user doesn't have a registered profile!").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("Not a valid user!").setEphemeral(true).queue();
                }
            }
            if (event.getSubcommandName() != null && event.getSubcommandName().equals("history")) {
                OptionMapping userOption = event.getOption("user");
                if (userOption != null && userOption.getAsMember() != null) {
                    Profile profile = ProfileManager.getProfile(userOption.getAsMember().getId());
                    if (profile != null) {
                        StringBuilder sb = new StringBuilder();
                        for (ShellTransaction t : profile.getShellHistory()) {
                            sb.append("`").append(t.toString()).append("`\n");
                        }
                        MessageEmbed embed = new EmbedBuilder()
                                .setTitle(profile.getInGameName() + "'s Transaction History")
                                .setDescription(sb.toString())
                                .setColor(Color.YELLOW)
                                .setAuthor("(Sorted newest to oldest)")
                                .build();
                        event.reply(new MessageCreateBuilder().addEmbeds(embed).build()).setEphemeral(true).queue();
                    } else {
                        event.reply("This user doesn't have a registered profile!").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("Not a valid user!").setEphemeral(true).queue();
                }
            }
        }
        else if (event.getName().equals("register-member")) {
            OptionMapping userOption = event.getOption("user");
            OptionMapping ignOption = event.getOption("ign");
            OptionMapping rankOption = event.getOption("rank");
            if (userOption == null || ignOption == null || rankOption == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            Member m = userOption.getAsMember();
            if (m != null) {
                Rank rank;
                try {
                    rank = Rank.valueOf(rankOption.getAsString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.reply("Not a valid rank!").queue();
                    return;
                }
                if (ProfileManager.getProfile(m.getId()) == null) {
                    ProfileManager.registerProfile(new Profile(m.getId(), ignOption.getAsString(), rank));
                    event.reply("Registered new profile!").setEphemeral(true).queue();
                } else {
                    event.reply("This user already has a profile!").setEphemeral(true).queue();
                }
            }
        }
        else if (event.getName().equals("new-member")) {
            OptionMapping userOption = event.getOption("user");
            OptionMapping ignOption = event.getOption("ign");
            if (userOption == null || ignOption == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            Member m = userOption.getAsMember();
            if (m != null) {

                if (ProfileManager.getProfile(m.getId()) == null) {
                    ProfileManager.registerProfile(new Profile(m.getId(), ignOption.getAsString(), Rank.STARFISH));
                    Guild guild = Poseidon.getApi().getGuildById(Constants.MAIN_SERVER_ID);
                    assert guild != null;

                    List<Role> roleList = new ArrayList<>();



                    Arrays.stream(Constants.COMMON_ROLES).forEach(r -> {
                        Role role = guild.getRoleById(r);
                        if (role != null) {
                            roleList.add(role);
                        }
                    });
                    Arrays.stream(Constants.STARFISH_ROLES).forEach(r -> {
                        Role role = guild.getRoleById(r);
                        if (role != null) {
                            roleList.add(role);
                        }
                    });

                    guild.modifyMemberRoles(m, roleList).queue();


                    guild.modifyNickname(m, Rank.STARFISH.prefix + ignOption.getAsString()).queue();

                    TextChannel channel = guild.getTextChannelById(Constants.MAIN_GUILD_GENERAL_CHANNEL_ID);
                    assert channel != null;

                    TextChannel faq = guild.getTextChannelById(Constants.GUILD_FAQ_CHANNEL_ID);
                    TextChannel role = guild.getTextChannelById(Constants.GUILD_ROLE_CHANEL_ID);
                    TextChannel warfaq = guild.getTextChannelById(Constants.GUILD_WAR_FAQ_CHANNEL_ID);
                    assert faq != null && role != null && warfaq != null;


                    channel.sendMessage(new MessageCreateBuilder().setContent("Welcome " + m.getAsMention() + " to The Aquarium!\nWe encourage you to read " + faq.getJumpUrl() + " for information on how our guild works. Remember to also grab extra roles in " + role.getJumpUrl() + "\nIf you are interested in learning how to war, " + warfaq.getJumpUrl() + " offers basic info and cheap war builds.\nFishy-business is where you can claim rewards for contributing, such as guild tomes and mythics. We even have our own unique currency and market!\nWe look forward to meeting you!").setAllowedMentions(List.of(Message.MentionType.USER)).build()).queue();

                    event.reply("Registered new member and applied roles!").setEphemeral(true).queue();
                } else {
                    event.reply("This user already has a profile!").setEphemeral(true).queue();
                }
            }
        }
        else if (event.getName().equals("reset-roles")) {
            OptionMapping userOption = event.getOption("user");
            if (userOption == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            Member m = userOption.getAsMember();
            if (m != null) {

                Guild guild = Poseidon.getApi().getGuildById(Constants.MAIN_SERVER_ID);
                assert guild != null;
                Role exMemberRole = guild.getRoleById(Constants.EX_MEMBER_ROLE);
                assert exMemberRole != null;
                guild.modifyMemberRoles(m, exMemberRole).queue();
                guild.modifyNickname(m, null).queue();

                Profile p = ProfileManager.getProfile(m.getId());
                if (p != null) {
                    p.setRank(Rank.NONE);
                    p.clearData();
                }

                event.reply("Reset roles of user!").setEphemeral(true).queue();
            }
        }
        else if (event.getName().equals("gen-uuid")) {
            OptionMapping userOption = event.getOption("ign");
            if (userOption == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            String m = userOption.getAsString();
            Profile p = ProfileManager.getProfileOverIGN(m);
            if (p != null) {
                p.genUUID();
                event.reply("UUID of " + p.getInGameName() + " is now: " + p.getMcUUID().toString()).setEphemeral(true).queue();
                return;
            }
            event.reply("No profile found!").setEphemeral(true).queue();
        }
        else if (event.getName().equals("set-rank")) {
            OptionMapping userOption = event.getOption("user");
            OptionMapping rankOption = event.getOption("rank");
            if (userOption == null || rankOption == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            Member m = userOption.getAsMember();
            if (m != null) {
                Rank rank;
                try {
                    rank = Rank.valueOf(rankOption.getAsString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.reply("Not a valid rank!").setEphemeral(true).queue();
                    return;
                }
                if (ProfileManager.getProfile(m.getId()) != null) {

                    Profile p = ProfileManager.getProfile(m.getId());
                    assert p != null;
                    p.setRank(rank);
                    Guild guild = Poseidon.getApi().getGuildById(Constants.MAIN_SERVER_ID);
                    assert guild != null;

                    List<Role> roleList = new ArrayList<>(m.getRoles());

                    for (Rank r : Rank.values()) {
                        Arrays.stream(r.getRoles()).forEach(x -> {
                            Role role = guild.getRoleById(x);
                            if (role != null) {
                                roleList.remove(role);
                            }
                        });
                    }

                    Arrays.stream(rank.getRoles()).forEach(r -> {
                        Role role = guild.getRoleById(r);
                        if (role != null) {
                            roleList.add(role);
                        }
                    });

                    guild.modifyMemberRoles(m, roleList).queue();

                    guild.modifyNickname(m, rank.prefix + p.getInGameName()).queue();

                    event.reply("Set rank!").setEphemeral(true).queue();
                } else {
                    event.reply("This user doesn't have a profile!").setEphemeral(true).queue();
                }
            }
        }
        else if (event.getName().equals("change-ign")) {
            OptionMapping userOption = event.getOption("user");
            OptionMapping ignOption = event.getOption("ign");
            if (userOption == null || ignOption == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            Member m = userOption.getAsMember();
            if (m != null) {
                if (ProfileManager.getProfile(m.getId()) != null) {

                    Profile p = ProfileManager.getProfile(m.getId());
                    assert p != null;
                    p.setInGameName(ignOption.getAsString());
                    try {
                        m.modifyNickname(p.getRank().prefix + ignOption.getAsString()).queue();
                    } catch (InsufficientPermissionException ignored) {}
                    event.reply("Changed ign!").setEphemeral(true).queue();
                } else {
                    event.reply("This user doesn't have a profile!").setEphemeral(true).queue();
                }
            }
        }
        else if (event.getName().equals("fupd")) {
            event.deferReply().setEphemeral(true).queue();
            if (event.getUser().getId().equals("494589147873542175")) {
                ProfileManager.updatePlayData();
                event.getHook().sendMessage("Updated data").setEphemeral(true).queue();
            } else {
                event.getHook().sendMessage("You are not called _GoldenShadow...").setEphemeral(true).queue();
            }

        }
        else if (event.getName().equals("fcpd")) {
            event.deferReply().setEphemeral(true).queue();
            if (event.getUser().getId().equals("494589147873542175")) {
                ProfileManager.clearPlayData();
                event.getHook().sendMessage("Cleared data").setEphemeral(true).queue();
            } else {
                event.getHook().sendMessage("You are not called _GoldenShadow...").setEphemeral(true).queue();
            }

        }
        else if (event.getName().equals("rank-mismatch")) {
            event.deferReply().setEphemeral(true).queue();
            WynncraftGuild g = getGuild();
            if (g != null) {
                StringBuilder builder = new StringBuilder();
                for (GuildMembers.Member m : g.members().getAll().values()) {
                    if (builder.toString().length() > 4000) break;
                    Optional<Profile> profile = ProfileManager.getProfiles(false).stream().filter(x -> x.getInGameName().equals(m.ign())).findFirst();
                    profile.ifPresentOrElse(p -> {
                        if (!p.getRank().getWynncraftRank().equals(m.rank())) {
                            builder.append("`").append(m.ign()).append(" is ").append(m.rank()).append(" in game, but should be ").append(p.getRank().getWynncraftRank()).append("`\n");
                        }
                    }, () -> builder.append("`").append(m.ign()).append(" is in the guild but not registered!`\n"));
                }

                MessageEmbed embed = new EmbedBuilder()
                        .setTitle("Rank Mismatch")
                        .setDescription(builder.toString())
                        .setColor(Color.orange)
                        .build();

                MessageCreateData data = new MessageCreateBuilder()
                        .addContent("")
                        .addEmbeds(List.of(embed))
                        .build();

                event.getHook().sendMessage(data).setEphemeral(true).queue();
            } else {
                event.getHook().sendMessage("Wynn API didn't respond... Please try again later.").setEphemeral(true).queue();
            }
        }
        else if (event.getName().equals("stats")) {
            OptionMapping userMapping = event.getOption("user");
            OptionMapping daysMapping = event.getOption("days");
            if (userMapping == null || daysMapping == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            int days = daysMapping.getAsInt();
            if (1 > days || days > 30) {
                event.reply("Days param must be between 1 and 30!").setEphemeral(true).queue();
                return;
            }
            Member member = userMapping.getAsMember();
            if (member != null) {
                Profile p = ProfileManager.getProfile(member.getId());
                if (p != null) {
                    MessageCreateData data = new MessageCreateBuilder()
                            .addEmbeds(Poseidon.getDataProvider().getTimespanOverviewData(p, days))
                            .build();
                    event.reply(data).setEphemeral(true).queue();
                } else {
                    event.reply("This user doesn't have a registered profile!").setEphemeral(true).queue();
                }
            } else {
                event.reply("Invalid user!").setEphemeral(true).queue();
            }
        }
        else if (event.getName().equals("leaderboard")) {
            event.deferReply().setEphemeral(true).queue();
            if (event.getSubcommandName() != null && event.getSubcommandName().equals("total")) {
                OptionMapping type = event.getOption("type");
                if (type == null) {
                    event.getHook().sendMessage("Params cannot be null!").setEphemeral(true).queue();
                    return;
                }
                DataProvider.LeaderboardType leaderboardType;
                try {
                    leaderboardType = DataProvider.LeaderboardType.valueOf(type.getAsString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.getHook().sendMessage("Not a valid type!").setEphemeral(true).queue();
                    return;
                }

                Leaderboard.getLeaderboardPage(leaderboardType, -1, 1).ifPresentOrElse(
                        embed -> {
                            MessageCreateData data = new MessageCreateBuilder()
                                    .addEmbeds(embed)
                                    .build();
                            event.getHook().sendMessage(data).setEphemeral(true)
                                    .addActionRow(
                                            Button.primary(Leaderboard.generateButtonID(leaderboardType, 1, -1, false), Emoji.fromUnicode("◀️")).asEnabled(),
                                            Button.primary(Leaderboard.generateButtonID(leaderboardType, 1, -1, true), Emoji.fromUnicode("▶️")).asEnabled()
                                    ).queue();
                        },
                        () -> event.getHook().sendMessage("Leaderboard couldn't be loaded").setEphemeral(true).queue()
                );
            } else if (event.getSubcommandName() != null && event.getSubcommandName().equals("timespan")) {
                OptionMapping type = event.getOption("type");
                OptionMapping days = event.getOption("days");
                if (type == null || days == null) {
                    event.getHook().sendMessage("Params cannot be null!").setEphemeral(true).queue();
                    return;
                }
                DataProvider.LeaderboardType leaderboardType;
                try {
                    leaderboardType = DataProvider.LeaderboardType.valueOf(type.getAsString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.getHook().sendMessage("Not a valid type!").setEphemeral(true).queue();
                    return;
                }
                if (leaderboardType == DataProvider.LeaderboardType.INACTIVITY) {
                    event.getHook().sendMessage("Inactivity cannot be displayed as a time span!").setEphemeral(true).queue();
                }
                int d = days.getAsInt();
                if (1 > d || d > 30) {
                    event.getHook().sendMessage("Days param must be between 1 and 30!").setEphemeral(true).queue();
                    return;
                }

                Leaderboard.getLeaderboardPage(leaderboardType, d, 1).ifPresentOrElse(
                        embed -> {
                            MessageCreateData data = new MessageCreateBuilder()
                                    .addEmbeds(embed)
                                    .build();
                            event.getHook().sendMessage(data).setEphemeral(true)
                                    .addActionRow(
                                        Button.primary(Leaderboard.generateButtonID(leaderboardType, 1, d, false), Emoji.fromUnicode("◀️")).asEnabled(),
                                        Button.primary(Leaderboard.generateButtonID(leaderboardType, 1, d, true), Emoji.fromUnicode("▶️")).asEnabled()
                                    ).queue();
                        },
                        () -> event.getHook().sendMessage("Leaderboard couldn't be loaded!").setEphemeral(true).queue()
                );
            }
        }
    }





    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if ((event.getName().equals("register-member") || event.getName().equals("set-rank")) && event.getFocusedOption().getName().equals("rank")) {
            List<Command.Choice> options = Stream.of(Rank.getStringValues())
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
        if ((event.getName().equals("leaderboard")) && event.getFocusedOption().getName().equals("type")) {
            List<Command.Choice> options = Stream.of(DataProvider.LeaderboardType.getStringValues())
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }


    private WynncraftGuild getGuild() {
        try {
            return Poseidon.getWynnAPI().v3().guild().guildStats("The Aquarium").run();
        } catch (APIException e) {
            return null;
        }
    }


}
