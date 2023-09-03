package me.goldenshadow.poseidon.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.goldenshadow.poseidon.Poseidon;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProfileManager extends ListenerAdapter {

    private static HashMap<String, Profile> profileHashMap = new HashMap<>();

    public static void registerProfile(Profile profile) {
        if (!profileHashMap.containsKey(profile.getMemberID())) {
            profileHashMap.put(profile.getMemberID(), profile);
            ProfileManager.saveToFile(profile);
        }
    }

    @Nullable
    public static Profile getProfile(String memberID) {
        return profileHashMap.getOrDefault(memberID, null);
    }

    public static List<Profile> getProfiles() {
        return new ArrayList<>(profileHashMap.values());
    }


    public static void loadFromFile() throws FileNotFoundException, URISyntaxException {
        File jar = new File(Poseidon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        File dataFolder = new File(jar.getParentFile().getPath() + "/poseidon");
        File profileFolder = new File(dataFolder.getPath() + "/profiles");
        if (dataFolder.exists() && dataFolder.isDirectory() && profileFolder.exists() && profileFolder.isDirectory()) {
            List<File> fileList = new ArrayList<>();
            File[] files = profileFolder.listFiles();
            Gson gson = new GsonBuilder().create();
            if (files != null) {
                fileList = Arrays.asList(files);
            }
            HashMap<String, Profile> map = new HashMap<>();
            for (File file : fileList) {
                if (file.getName().contains(".json")) {
                    Reader reader = new FileReader(file);
                    //Type type = new TypeToken<Profile>() {
                    //}.getType();

                    Profile profile = gson.fromJson(reader, Profile.class);

                    map.put(profile.getMemberID(), profile);
                }
            }
            profileHashMap = map;


        } else {
            System.out.println("Data folder could not be found. A new one will be created when data in memory is saved!");
        }
    }

    public static void saveToFile(Profile p) {
        try {
            Gson gson = new GsonBuilder().create();
            File jar = new File(Poseidon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            File dataFolder = new File(jar.getParentFile().getPath() + "/poseidon");
            File profileFolder = new File(dataFolder.getPath() + "/profiles");
            dataFolder.mkdir();
            profileFolder.mkdir();
            File profileFile = new File(profileFolder.getPath() + "/" + p.getMemberID() + ".json");
            profileFile.createNewFile();
            Writer writer = new FileWriter(profileFile, false);
            gson.toJson(p, writer);
            writer.close();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            for (Profile p : profileHashMap.values()) {
                saveToFile(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
