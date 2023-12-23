package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.player.WynncraftFullPlayer;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3PlayerFullStats extends APIRequest<WynncraftFullPlayer> {

    public APIV3PlayerFullStats(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftFullPlayer run() {
        return GSON.fromJson(getResponse(), WynncraftFullPlayer.class);
    }
}
