package at.htlgkr.tournamaker;

import java.io.Serializable;

public class Benutzer implements Serializable
{
    private String username;
    private String hashedPassword;

    public Benutzer() {
    }

    public Benutzer(String username, String hashedPassword)
    {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

}
