package me.goldenshadow.wynnapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.goldenshadow.wynnapi.config.WynncraftAPIConfig;
import me.goldenshadow.wynnapi.mojang.MojangEndpoint;
import me.goldenshadow.wynnapi.util.DateDeserializer;
import me.goldenshadow.wynnapi.v3.APIVersion3;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuildList;
import me.goldenshadow.wynnapi.v3.guild.WynncraftTerritoryList;
import me.goldenshadow.wynnapi.v3.guild.data.GuildMembers;
import me.goldenshadow.wynnapi.v3.player.WynncraftCharacterList;
import me.goldenshadow.wynnapi.v3.player.data.LegacyRankColour;

import javax.annotation.CheckReturnValue;
import java.util.Date;


public class WynncraftAPI extends APIMidpoint {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .registerTypeAdapter(WynncraftCharacterList.class, new WynncraftCharacterList.WynncraftCharacterListDeserializer())
            .registerTypeAdapter(WynncraftGuildList.class, new WynncraftGuildList.WynncraftGuildListDeserializer())
            .registerTypeAdapter(WynncraftTerritoryList.class, new WynncraftTerritoryList.WynncraftTerritoryListDeserializer())
            .registerTypeAdapter(GuildMembers.class, new GuildMembers.MembersDeserializer())
            .registerTypeAdapter(LegacyRankColour.class, new LegacyRankColour.LegacyRankColourDeserializer())
            .create();
    public static final String VERSION = "0.8.3";
    private static WynncraftAPI INSTANCE;
    private APIVersion3 v3;
    private WynncraftAPIConfig config;

    public WynncraftAPI(WynncraftAPIConfig config) {
        this.config = config;
        this.v3 = new APIVersion3(this);

        WynncraftAPI.INSTANCE = this;
    }

    public WynncraftAPI() {
        this(new WynncraftAPIConfig());
    }

    public static WynncraftAPI getApi() {
        return WynncraftAPI.INSTANCE;
    }

    @Deprecated
    public static int getUnixTimestampSeconds() {
        return (int) (System.currentTimeMillis() / 1000L);
    }


    @CheckReturnValue
    public APIVersion3 v3() {
        return v3;
    }

    public String getBaseURL() {
        return "https://api.wynncraft.com/";
    }

    public WynncraftAPIConfig getConfig() {
        return config;
    }

    @Override
    public WynncraftAPIConfig getAPIConfig() {
        return config;
    }

    @CheckReturnValue
    public MojangEndpoint getMojangData(String username) {
        return new MojangEndpoint("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + username, getApi());
    }

}
