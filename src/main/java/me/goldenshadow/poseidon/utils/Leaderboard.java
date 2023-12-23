package me.goldenshadow.poseidon.utils;

import me.goldenshadow.poseidon.Poseidon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.*;
import java.util.List;

public class Leaderboard extends ListenerAdapter {


    public static Optional<MessageEmbed> getLeaderboardPage(DataProvider.LeaderboardType type, int days, int page) {
        List<String> list = days > 0 ? Poseidon.getDataProvider().getTimespanLeaderboardData(type, days) : Poseidon.getDataProvider().getGlobalLeaderboardData(type);
        return buildEmbed(list, type, page, type.getDisplayName() + " - " + (days != -1 ? days + " days" : "Total"));
    }


    /**
     * Used to generate the button id for leaderboards
     * @param type The type of leaderboard
     * @param page The page that is currently being displayed
     * @param days The span of days the leaderboard is displaying. -1 if its global
     * @param direction True for forward, false for back
     * @return The id
     */
    public static String generateButtonID(DataProvider.LeaderboardType type, int page, int days, boolean direction) {
        return  "poseidon_" + type.toString() + "_" + page + "_" + days + "_" + (direction ? 1 : 0);
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        event.deferEdit().queue();
        Button b = event.getButton();
        String id = b.getId();
        assert id != null;
        String[] data = id.split("_");
        DataProvider.LeaderboardType type = DataProvider.LeaderboardType.valueOf(data[1]);
        int page = Integer.parseInt(data[2]);
        int days = Integer.parseInt(data[3]);
        boolean direction = Integer.parseInt(data[4]) == 1;


        if (data[0].equals("poseidon")) {
            Optional<MessageEmbed> embed = getLeaderboardPage(type, days, direction ? page + 1 : page - 1);
            embed.ifPresentOrElse(e -> {

                event.getHook().editOriginalEmbeds(e)
                        .setActionRow(
                                Button.primary(generateButtonID(type, (direction ? page + 1 : page -1), days, false), Emoji.fromUnicode("◀️")).asEnabled(),
                                Button.primary(generateButtonID(type, (direction ? page + 1 : page -1), days, true), Emoji.fromUnicode("▶️")).asEnabled()
                        ).queue();


                /*
                event.reply(new MessageCreateBuilder().addEmbeds(e).build()).setEphemeral(true)
                        .addActionRow(
                                Button.primary(generateButtonID(type, (direction ? page + 1 : page -1), days, false), Emoji.fromUnicode("◀️")).asEnabled(),
                                Button.primary(generateButtonID(type, (direction ? page + 1 : page -1), days, true), Emoji.fromUnicode("▶️")).asEnabled()
                        ).queue();

                 */


            }, () -> {
                event.getHook().sendMessage("You can't scroll any further...").setEphemeral(true).queue();
            });


        }
    }



    private static Optional<MessageEmbed> buildEmbed(List<String> list, DataProvider.LeaderboardType type,
                                           int page, String title) {
        StringBuilder sb = new StringBuilder();
        int s = (page - 1) * 10;
        if (s < 0 || s >= list.size()) return Optional.empty();


        for (int i = s; i < Math.min(s + 10, list.size()); i++) {
            sb.append(list.get(i));
        }
        if (sb.isEmpty()) return Optional.empty();
        return Optional.of(new EmbedBuilder()
                .setTitle(title + " - Page " + page)
                .setDescription(sb.toString())
                .setColor(type.getEmbedColor())
                .build());
    }


}
