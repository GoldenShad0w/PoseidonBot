package me.goldenshadow.poseidon.shells;

import me.goldenshadow.poseidon.profile.Profile;
import me.goldenshadow.poseidon.profile.ProfileManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class ShellManager {

    public static MessageEmbed getShellLeaderboard(int page) {
        List<Profile> list = ProfileManager.getProfiles();
        list.sort(Comparator.comparingInt(Profile::getShells).reversed());
        list = list.subList((page-1) * 10, Math.min(((page-1) * 10) + 10, list.size()-1));
        StringBuilder sb = new StringBuilder();
        for (Profile p : list) {
            sb.append("**").append(p.getShells()).append("** - ").append(p.getInGameName()).append("\n");
        }
        return new EmbedBuilder()
                .setTitle("Shell Leaderboard")
                .setDescription(sb.toString())
                .setColor(Color.ORANGE)
                .build();
    }
}
