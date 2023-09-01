package me.goldenshadow.poseidon.profile;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileManager {

    private static HashMap<String, Profile> profileHashMap = new HashMap<>();

    public static void registerProfile(Profile profile) {
        if (!profileHashMap.containsKey(profile.getMemberID())) {
            profileHashMap.put(profile.getMemberID(), profile);
        }
    }

    @Nullable
    public static Profile getProfile(String memberID) {
        return profileHashMap.getOrDefault(memberID, null);
    }

    public static List<Profile> getProfiles() {
        return new ArrayList<>(profileHashMap.values());
    }
}
