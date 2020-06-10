package at.htlgkr.tournamaker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

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

    }

    public void onClickLogin()
    {
        String username = ((TextView) findViewById(R.id.tv_username)).getText().toString();
        String password = ((TextView) findViewById(R.id.tv_password)).getText().toString();
        if(!username.isEmpty() || !password.isEmpty())
        {
            try
            {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String securedPassword = Hasher.normalToHashedPassword(digest.digest(password.getBytes(StandardCharsets.UTF_8)));

                if(allBenutzer.stream()
                    .filter((b) -> b.getUsername().equals(username) && b.getHashedPassword().equals(securedPassword))
                    .count() == 1)
                {
                    currentBenutzer = allBenutzer.stream()
                            .filter((b) -> b.getUsername().equals(username) && b.getHashedPassword().equals(securedPassword))
                            .collect(Collectors.toList()).get(0);

                    Intent i = new Intent(MainActivity.this, FragmentsActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("benutzer", (Serializable) allBenutzer);
                    extra.putSerializable("current", currentBenutzer);
                    i.putExtra("bundle", extra);
                    startActivity(i);
                }
                else
                {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Username or Password is wrong ", Snackbar.LENGTH_SHORT);

                    View snackView = snack.getView();
                    snackView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                    snack.show();
                }

            }
            catch (NoSuchAlgorithmException e)
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
}
