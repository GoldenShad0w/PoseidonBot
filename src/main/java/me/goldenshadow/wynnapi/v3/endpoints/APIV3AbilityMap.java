package me.goldenshadow.wynnapi.v3.endpoints;



import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.player.WynncraftAbilityMap;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;


public class APIV3AbilityMap extends APIRequest<WynncraftAbilityMap> {

    public APIV3AbilityMap(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftAbilityMap run() {
        return GSON.fromJson(getResponse(), WynncraftAbilityMap.class);
    }
}
