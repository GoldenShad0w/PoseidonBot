package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.player.WynncraftOnlinePlayers;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3OnlinePlayers extends APIRequest<WynncraftOnlinePlayers> {

    public APIV3OnlinePlayers(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftOnlinePlayers run() {
        return GSON.fromJson(getResponse(), WynncraftOnlinePlayers.class);
    }
}
