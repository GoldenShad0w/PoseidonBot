package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.guild.WynncraftTerritoryList;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3TerritoryList extends APIRequest<WynncraftTerritoryList> {

    public APIV3TerritoryList(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftTerritoryList run() {
        return GSON.fromJson(getResponse(), WynncraftTerritoryList.class);
    }
}
