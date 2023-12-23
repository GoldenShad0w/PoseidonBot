package me.goldenshadow.wynnapi.v3;


import me.goldenshadow.wynnapi.WynncraftAPI;
import me.goldenshadow.wynnapi.v3.routes.APIV3Guild;
import me.goldenshadow.wynnapi.v3.routes.APIV3Player;

import javax.annotation.CheckReturnValue;

public class APIVersion3 {

    private final APIV3Player player;
    private final APIV3Guild guild;

    public APIVersion3(WynncraftAPI api) {
        this.player = new APIV3Player(api);
        this.guild = new APIV3Guild(api);
    }

    @CheckReturnValue
    public APIV3Player player() {
        return player;
    }

    @CheckReturnValue
    public APIV3Guild guild() {
        return guild;
    }
}
