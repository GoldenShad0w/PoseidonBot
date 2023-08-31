package me.goldenshadow;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Main {

    public static String BOT_TOKEN = "";
    public static int MAIN_SERVER_ID = 0;
    public static int STAFF_SERVER_ID = 0;

    public static void main(String[] args) {
        JDA api = JDABuilder.createDefault(BOT_TOKEN).setActivity(Activity.watching("the ocean")).build();
    }
}