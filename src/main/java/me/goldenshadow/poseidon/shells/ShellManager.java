package me.goldenshadow.poseidon.shells;

import me.goldenshadow.poseidon.profile.Profile;
import me.goldenshadow.poseidon.profile.ProfileManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class ShellManager extends ListenerAdapter {

    @Nullable
    public static MessageEmbed getShellLeaderboard(int page) {
        List<Profile> list = ProfileManager.getProfiles();
        list.sort(Comparator.comparingInt(Profile::getShells).reversed());
        StringBuilder sb = new StringBuilder();
        int s = (page-1)*10;
        for (int i = s; i < Math.min(s + 10, list.size()); i++) {
            sb.append("`").append(i+1).append(". ").append(list.get(i).getInGameName()).append(" - ").append(list.get(i).getShells()).append("`\n");
        }
        if (sb.isEmpty()) return null;
        return new EmbedBuilder()
                .setTitle("Shell Leaderboard - Page " + page)
                .setDescription(sb.toString())
                .setColor(Color.ORANGE)
                .build();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Button b = event.getButton();
        Message message = event.getMessage();
        if (b.getId() != null && b.getId().equals("poseidon_sh_lb_back")) {

            if (!message.getEmbeds().isEmpty()) {
                String s = message.getEmbeds().get(0).getTitle();
                if (s != null) {
                    s = s.replaceAll(".*? Page", "").trim();
                    int i = Integer.parseInt(s);
                    if (i > 1) {
                        event.reply(new MessageCreateBuilder().addEmbeds(getShellLeaderboard(i-1)).build()).setEphemeral(true).addActionRow(
                                Button.primary("poseidon_sh_lb_back", Emoji.fromUnicode("◀️")).asEnabled(),
                                Button.primary("poseidon_sh_lb_forward", Emoji.fromUnicode("▶️")).asEnabled()
                        ).queue();
                    } else {
                        event.reply("You can't scroll any further...").setEphemeral(true).queue();
                    }
                }
            }
        } else if (b.getId() != null && b.getId().equals("poseidon_sh_lb_forward")) {
            if (!message.getEmbeds().isEmpty()) {
                String s = message.getEmbeds().get(0).getTitle();
                if (s != null) {
                    s = s.replaceAll(".*? Page", "").trim();
                    int i = Integer.parseInt(s);
                    MessageEmbed embed = getShellLeaderboard(i+1);
                    if (embed != null) {
                        event.reply(new MessageCreateBuilder().addEmbeds(embed).build()).setEphemeral(true).addActionRow(
                                Button.primary("poseidon_sh_lb_back", Emoji.fromUnicode("◀️")).asEnabled(),
                                Button.primary("poseidon_sh_lb_forward", Emoji.fromUnicode("▶️")).asEnabled()
                        ).queue();
                    } else {
                        event.reply("You can't scroll any further...").setEphemeral(true).queue();
                    }
                }
            }
        }
    }
}
