package me.goldenshadow.poseidon;

import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.profile.Profile;
import me.goldenshadow.poseidon.profile.Rank;
import me.goldenshadow.poseidon.shells.ShellManager;
import me.goldenshadow.poseidon.shells.ShellTransaction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("shells")) {
            if (event.getSubcommandName() != null && event.getSubcommandName().equals("balance")) {
                OptionMapping option = event.getOption("user");
                if (option != null && option.getAsMember() != null) {
                    Profile p = ProfileManager.getProfile(option.getAsMember().getId());
                    if (p != null) {
                        MessageEmbed embed = new EmbedBuilder()
                                .setTitle(p.getInGameName() + "'s Shells")
                                .setDescription("Balance: `" + p.getShells() + "`")
                                .setColor(Color.ORANGE)
                                .build();

                        MessageCreateData data = new MessageCreateBuilder()
                                .addEmbeds(List.of(embed))
                                .build();

                        event.reply(data).setEphemeral(true).queue();
                    } else {
                        MessageEmbed embed = new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("This user doesn't have a registered profile!")
                                .setColor(Color.RED)
                                .build();

                        MessageCreateData data = new MessageCreateBuilder()
                                .addEmbeds(List.of(embed))
                                .build();
                        event.reply(data).setEphemeral(true).queue();
                    }
                } else {
                    event.reply("Not a valid user!").setEphemeral(true).queue();
                }
            }
            else if (event.getSubcommandName() != null && event.getSubcommandName().equals("leaderboard")) {
                event.reply(new MessageCreateBuilder().addEmbeds(ShellManager.getShellLeaderboard(1)).build()).setEphemeral(true).addActionRow(
                        Button.primary("poseidon_sh_lb_back", Emoji.fromUnicode("◀️")).asEnabled(),
                        Button.primary("poseidon_sh_lb_forward", Emoji.fromUnicode("▶️")).asEnabled()
                ).queue();

            }
        } else if (event.getName().equals("manage-shells")) {
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
        } else if (event.getName().equals("new-member")) {
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
    }




    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("new-member") && event.getFocusedOption().getName().equals("rank")) {
            List<Command.Choice> options = Stream.of(Rank.getStringValues())
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
