package at.htlgkr.tournamaker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.Classes.Hasher;
import at.htlgkr.tournamaker.R;


public class MainActivity extends AppCompatActivity
{
    private DatabaseReference firebaseDatabase;
    private List<Benutzer> allBenutzer = new ArrayList<>();
    private Benutzer currentBenutzer;
    private final static String FILENAME_JSON = "currentBenutzerJson.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        try
        {
            currentBenutzer = loadCurrentBenutzerJSON(openFileInput(FILENAME_JSON));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("users");

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                allBenutzer.clear();
                Gson gson = new Gson();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String s = (String) ds.getValue();
                    allBenutzer.add(gson.fromJson(s, Benutzer.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("OnCancelled", "Cancelled");
            }
        });

        Intent registerIntent = getIntent();
        if(registerIntent.getExtras() != null)
        {
            Bundle extra = registerIntent.getBundleExtra("bundle");
            allBenutzer = (List<Benutzer>) extra.getSerializable("benutzer");
        }

        TextView register = findViewById(R.id.tv_welcome);
        register.setOnClickListener(v ->
        {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(v -> onClickLogin());

        if(currentBenutzer != null)
        {
            Intent i = new Intent(MainActivity.this, FragmentsActivity.class);
            Bundle extra = new Bundle();
            extra.putSerializable("benutzer", (Serializable) allBenutzer);
            extra.putSerializable("current", currentBenutzer);
            i.putExtra("bundle", extra);
            startActivity(i);
        }

    }

    public void onClickLogin()
    {
        CheckBox staylogged = findViewById(R.id.checkbox_staylogged);

        String username = ((TextView) findViewById(R.id.tv_username)).getText().toString();
        String password = ((TextView) findViewById(R.id.tv_password)).getText().toString();
        if(!username.isEmpty() || !password.isEmpty())
        {
            try
            {
                String securedPassword = Hasher.normalToHashedPassword(password);

                if(allBenutzer.stream()
                    .filter((b) -> b.getUsername().equals(username) && b.getHashedPassword().equals(securedPassword))
                    .count() == 1)
                {
                    currentBenutzer = allBenutzer.stream()
                            .filter((b) -> b.getUsername().equals(username) && b.getHashedPassword().equals(securedPassword))
                            .collect(Collectors.toList()).get(0);

                    if(staylogged.isChecked())
                    {
                        saveCurrentBenutzerJSON(openFileOutput(FILENAME_JSON, MODE_PRIVATE));
                    }

                    Intent i = new Intent(MainActivity.this, FragmentsActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("benutzer", (Serializable) allBenutzer);
                    extra.putSerializable("current", currentBenutzer);
                    i.putExtra("bundle", extra);
                    startActivity(i);
                }
                else
                {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Username or Password is wrong", Snackbar.LENGTH_SHORT);

                    View snackView = snack.getView();
                    snackView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                    snack.show();
                }

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }


        }
        else
        {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Fields are empty", Snackbar.LENGTH_SHORT);

            View snackView = snack.getView();
            snackView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
            snack.show();

        }
    }

    public void saveCurrentBenutzerJSON(FileOutputStream fos)
    {

        PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos));
        Gson gson = new Gson();
        pw.write(gson.toJson(currentBenutzer));
        pw.flush();
        pw.close();

    }

    public Benutzer loadCurrentBenutzerJSON(FileInputStream fis) {
        Benutzer b = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String json = "";
        Gson gson = new Gson();
        try 
        {
            String line = br.readLine();
            while (line != null) 
            {
                json += line;
                line = br.readLine();
            }
            b = gson.fromJson(json, Benutzer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }
}
