package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuild;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3GuildStats extends APIRequest<WynncraftGuild> {

    public APIV3GuildStats(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftGuild run() {
        return GSON.fromJson(getResponse(), WynncraftGuild.class);
    }
}
