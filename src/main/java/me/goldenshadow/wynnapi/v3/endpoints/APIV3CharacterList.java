package me.goldenshadow.wynnapi.v3.endpoints;


import me.goldenshadow.wynnapi.APIMidpoint;
import me.goldenshadow.wynnapi.APIRequest;
import me.goldenshadow.wynnapi.v3.player.WynncraftCharacterList;

import static me.goldenshadow.wynnapi.WynncraftAPI.GSON;

public class APIV3CharacterList extends APIRequest<WynncraftCharacterList> {

    public APIV3CharacterList(String requestURL, APIMidpoint midpoint) {
        super(requestURL, midpoint);
    }

    @Override
    public WynncraftCharacterList run() {
        return GSON.fromJson(getResponse(), WynncraftCharacterList.class);
    }
}
