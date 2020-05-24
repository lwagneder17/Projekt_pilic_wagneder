package at.htlgkr.tournamaker.FireBase;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.tournamaker.Benutzer;

public class asyncGetAllUsers extends AsyncTask<String, Integer, List<Benutzer>>
{

    @Override
    protected List<Benutzer> doInBackground(String... strings)
    {
        List<Benutzer> allUsers = new ArrayList<>();

        try
        {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://tournamaker-a1024.firebaseio.com/users.json").openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");


            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = br.readLine();
                String newLine = br.readLine();
                while (newLine != null) {
                    line += newLine;
                    newLine = br.readLine();
                }

                JSONObject bruh = new JSONObject(line);


            }
        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }

        return allUsers;
    }

}
