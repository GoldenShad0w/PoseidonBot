package me.goldenshadow.poseidon.applications;

import net.dv8tion.jda.api.entities.MessageEmbed;

public record ApplicationData(String messageID, MessageEmbed embed, String link) {
}
