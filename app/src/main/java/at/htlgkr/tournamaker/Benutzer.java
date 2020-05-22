package at.htlgkr.tournamaker;

import android.graphics.Bitmap;

public class Benutzer
{
    private String username;
    private String hashedPassword;
    private Bitmap picture;
    private byte[] salt;

    public Benutzer(String username, String hashedPassword, Bitmap picture, byte[] salt)
    {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Bitmap getPicture() {
        return picture;
    }
}
