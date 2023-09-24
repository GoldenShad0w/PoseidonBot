package me.goldenshadow.poseidon.profile;

import me.goldenshadow.poseidon.shells.ShellTransaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Profile {

    private final String memberID;
    private Rank rank;
    private String inGameName;
    private int shells;
    private final List<ShellTransaction> shellHistory;
    private int playtime;
    private Date lastSeen;

    public Profile(String memberID, String ign, Rank rank) {
        this.memberID = memberID;
        this.rank = rank;
        inGameName = ign;
        shells = 0;
        playtime = 0;
        shellHistory = new ArrayList<>();
    }

    public int getShells() {
        return shells;
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
    }
}

