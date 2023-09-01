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

    public Profile(String memberID, String ign) {
        this.memberID = memberID;
        rank = Rank.STARFISH;
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
        shellHistory.add(new ShellTransaction(ShellTransaction.TransactionType.ADDITION, amount, reason));
    }

    public void removeShells(int amount, String reason) {
        shells -= amount;
        shellHistory.add(new ShellTransaction(ShellTransaction.TransactionType.SUBTRACTION, amount, reason));
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
}
