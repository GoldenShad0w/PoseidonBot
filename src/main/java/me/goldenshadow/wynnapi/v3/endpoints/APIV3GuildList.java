package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuildList;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3GuildList extends APIRequest<WynncraftGuildList> {

    public APIV3GuildList(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftGuildList run() {
        return GSON.fromJson(getResponse(), WynncraftGuildList.class);
    }


}
