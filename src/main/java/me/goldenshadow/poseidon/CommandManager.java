package me.goldenshadow.poseidon;

import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.profile.Profile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

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
                //TODO
            }
        } else if (event.getName().equals("new-member")) {
            OptionMapping userOption = event.getOption("user");
            OptionMapping ignOption = event.getOption("ign");
            if (userOption == null || ignOption == null) {
                event.reply("Params cannot be null!").setEphemeral(true).queue();
                return;
            }
            Member m = userOption.getAsMember();
            if (m != null) {
                if (ProfileManager.getProfile(m.getId()) == null) {
                    ProfileManager.registerProfile(new Profile(m.getId(), ignOption.getAsString()));
                    event.reply("Registered new profile!").setEphemeral(true).queue();
                } else {
                    event.reply("This user already has a profile!").setEphemeral(true).queue();
                }
            }
        }
    }
}
