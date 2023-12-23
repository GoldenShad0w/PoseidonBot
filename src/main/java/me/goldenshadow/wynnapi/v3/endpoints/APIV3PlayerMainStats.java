package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.player.WynncraftPlayer;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3PlayerMainStats extends APIRequest<WynncraftPlayer> {

    public APIV3PlayerMainStats(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftPlayer run() {
        return GSON.fromJson(getResponse(), WynncraftPlayer.class);
    }
}
