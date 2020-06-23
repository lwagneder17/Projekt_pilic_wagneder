package at.htlgkr.tournamaker.Classes;

import java.io.Serializable;

public class Benutzer implements Serializable
{
    private String username;
    private String hashedPassword;
    private boolean privateSettings;
    private Statistic statistics;
    private Friends friends;

    public Benutzer() {
    }

    public Benutzer(String username, String hashedPassword)
    {
        this.username = username;
        this.hashedPassword = hashedPassword;
        statistics = new Statistic(0,0,0);
        friends = new Friends();
        privateSettings = false;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }


    public boolean getPrivateSettings() {
        return privateSettings;
    }

    public void setPrivateSettings(boolean privateSettings) {
        this.privateSettings = privateSettings;
    }

    public Statistic getStatistics() {
        return statistics;
    }

    public Friends getFriends() {
        return friends;
    }
}
