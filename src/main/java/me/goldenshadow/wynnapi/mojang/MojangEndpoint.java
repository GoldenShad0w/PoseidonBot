package me.goldenshadow.wynnapi.mojang;

import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.player.WynncraftCharacter;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class MojangEndpoint extends APIRequest<MojangPlayerData> {

    public MojangEndpoint(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public MojangPlayerData run() {
        return GSON.fromJson(getResponse(), MojangPlayerData.class);
    }
}
