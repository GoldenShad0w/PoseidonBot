package me.goldenshadow.poseidon.profile;

import me.goldenshadow.poseidon.Poseidon;
import me.goldenshadow.poseidon.utils.ShellTransaction;
import me.goldenshadow.wynnapi.exceptions.APIException;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Profile {

    private final String memberID;
    private UUID mcUUID;
    private Rank rank;
    private String inGameName;
    private int shells;
    private final List<ShellTransaction> shellHistory;
    private List<PlayData> playDataSnapshots;
    private Date lastSeen;

    public Profile(String memberID, String ign, Rank rank) {
        this.memberID = memberID;
        this.rank = rank;
        inGameName = ign;
        shells = 0;
        shellHistory = new ArrayList<>();
        playDataSnapshots = new ArrayList<>();
        mcUUID = Poseidon.getWynnAPI().getMojangData(ign).run().getUUID();
    }

    public int getShells() {
        return shells;
    }

    @Nullable
    public UUID getMcUUID() {
        if (mcUUID != null) {
            return mcUUID;
        }
        try {
            mcUUID = Poseidon.getWynnAPI().getMojangData(inGameName).run().getUUID();
            ProfileManager.saveToFile(this);
            return mcUUID;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isUUIDPresent() {
        return mcUUID != null;
    }

    public void genUUID() {
        mcUUID = Poseidon.getWynnAPI().getMojangData(inGameName).run().getUUID();
        ProfileManager.saveToFile(this);
    }

    public void addShells(int amount, String reason) {
        shells += amount;
        shellHistory.add(0, new ShellTransaction(ShellTransaction.TransactionType.ADDITION, amount, reason));
        if (shellHistory.size() > 15) shellHistory.remove(shellHistory.size()-1);
        ProfileManager.saveToFile(this);
    }

    public void removeShells(int amount, String reason) {
        shells -= amount;
        shellHistory.add(0, new ShellTransaction(ShellTransaction.TransactionType.SUBTRACTION, amount, reason));
        if (shellHistory.size() > 10) shellHistory.remove(shellHistory.size()-1);
        ProfileManager.saveToFile(this);
    }

    public String getInGameName() {
        return inGameName;
    }

    public void setInGameName(String inGameName) {
        this.inGameName = inGameName;
        mcUUID = Poseidon.getWynnAPI().getMojangData(inGameName).run().getUUID();
        ProfileManager.saveToFile(this);
    }

    public String getMemberID() {
        return memberID;
    }

    public List<ShellTransaction> getShellHistory() {
        return shellHistory;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        ProfileManager.saveToFile(this);
    }

    public void addData(PlayData playData) {
        if (playDataSnapshots == null) playDataSnapshots = new ArrayList<>();
        playDataSnapshots.add(0, playData);
        if (playDataSnapshots.size() > 30) playDataSnapshots.remove(playDataSnapshots.size()-1);
        ProfileManager.saveToFile(this);
    }

    public PlayData getPlayData(int days) {
        if (0 < days && days <= 30) {
            PlayData p1 = playDataSnapshots.isEmpty() ? PlayData.getEmpty() : playDataSnapshots.get(0);
            PlayData p2 = days >= playDataSnapshots.size() ? PlayData.getEmpty() : playDataSnapshots.get(days);
            return new PlayData(p1.wars() - p2.wars(), p1.playtime() - p2.playtime(), p1.xp() - p2.xp(), p1.shells() - p2.shells());
        } else if (days == -1) {
            if (playDataSnapshots == null || playDataSnapshots.isEmpty()) return PlayData.getEmpty();
            PlayData p = playDataSnapshots.get(0);
            return new PlayData(p.wars(), p.playtime(), p.xp(), p.shells());
        } else {
            throw new IllegalArgumentException("Param must be between 1 and 30 or -1");
        }
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void clearData() {
        if (playDataSnapshots != null) playDataSnapshots.clear();
        ProfileManager.saveToFile(this);
    }
}

