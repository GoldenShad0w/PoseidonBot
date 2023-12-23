import me.goldenshadow.poseidon.Poseidon;
import me.goldenshadow.poseidon.profile.Profile;
import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.profile.Rank;
import me.goldenshadow.poseidon.utils.DataProvider;
import me.goldenshadow.wynnapi.WynncraftAPI;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class DataTests {

    @Test
    void getDataWorksCorrectly() {
        //Poseidon.wynnAPI = new WynncraftAPI();
        //Poseidon.dataProvider = new DataProvider();
        ProfileManager.registerProfile(new Profile("494589147873542175", "_GoldenShadow", Rank.NARWHAL));
        ProfileManager.registerProfile(new Profile("1154411794836754542", "PokemonGamer11", Rank.NARWHAL));
        ProfileManager.registerProfile(new Profile("244187630462435338", "Barled", Rank.NARWHAL));
        ProfileManager.updatePlayData();
        System.out.println((ProfileManager.getProfile("494589147873542175").getPlayData(-1).wars()));
    }
}
