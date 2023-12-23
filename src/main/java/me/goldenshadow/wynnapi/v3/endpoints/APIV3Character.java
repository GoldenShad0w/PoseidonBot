package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.player.WynncraftCharacter;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3Character extends APIRequest<WynncraftCharacter> {

    public APIV3Character(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftCharacter run() {
        return GSON.fromJson(getResponse(), WynncraftCharacter.class);
    }
}
