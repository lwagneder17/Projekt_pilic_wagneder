package at.htlgkr.tournamaker;

import android.graphics.Bitmap;

public class Benutzer
{
    private String username;
    private String hashedPassword;
    private String encodedPicture;

    public Benutzer(String username, String hashedPassword, String picture)
    {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.encodedPicture = picture;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getPicture()
    {
        return encodedPicture;
    }

}
