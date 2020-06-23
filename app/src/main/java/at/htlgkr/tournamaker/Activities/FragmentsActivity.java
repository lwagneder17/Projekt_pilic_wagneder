package at.htlgkr.tournamaker.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.Classes.Tournament;
import at.htlgkr.tournamaker.Preferences.NotificationService;
import at.htlgkr.tournamaker.R;

public class FragmentsActivity extends AppCompatActivity
{
    public static List<Benutzer> allBenutzer = new ArrayList<>();
    public static List<Tournament> allTournaments = new ArrayList<>();
    public static Benutzer currentBenutzer;
    public static StorageReference firebaseStorage;
    public static DatabaseReference tournamentsDataBase;
    public static DatabaseReference benutzerDataBase;
    public static View fragmentActivityView;

    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        getSupportActionBar().hide();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        preferencesChangeListener = this::preferenceChanged;
        prefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        firebaseStorage = FirebaseStorage.getInstance().getReference();
        benutzerDataBase = FirebaseDatabase.getInstance().getReference("users");
        tournamentsDataBase = FirebaseDatabase.getInstance().getReference("tournaments");
        fragmentActivityView = findViewById(android.R.id.content);

        tournamentsDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                allTournaments.clear();
                Gson gson = new Gson();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String s = (String) ds.getValue();
                    allTournaments.add(gson.fromJson(s, Tournament.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("OnCancelled", "Cancelled");
            }
        });

        benutzerDataBase.addValueEventListener(new ValueEventListener() {
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
                for(Benutzer b: allBenutzer)
                {
                    if(b.getUsername().equals(currentBenutzer.getUsername()))
                    {
                        currentBenutzer = b;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("OnCancelled", "Cancelled");
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search, R.id.navigation_create, R.id.navigation_stats, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Intent i = getIntent();
        if(i != null)
        {
            if(i.getExtras() != null)
            {
                Bundle extra = i.getBundleExtra("bundle");
                allBenutzer = (List<Benutzer>) extra.getSerializable("benutzer");
                currentBenutzer = (Benutzer) extra.getSerializable("current");
            }
        }

        preferenceChanged(prefs, "notifications");
        preferenceChanged(prefs, "private");

        showFriendRequests();
    }

    public void showFriendRequests()
    {
        if(!currentBenutzer.getFriends().getFriendRequests().isEmpty())
        {
            Gson gson = new Gson();
            AlertDialog.Builder alert = new AlertDialog.Builder(FragmentsActivity.this);
            for(String name: currentBenutzer.getFriends().getFriendRequests())
            {
                alert.setTitle("Do you want to add "+name+" as a Friend?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        currentBenutzer.getFriends().addFriend(name);

                        Benutzer friend = allBenutzer.stream().filter((b) -> b.getUsername().equals(name)).findFirst().get();
                        friend.getFriends().addFriend(currentBenutzer.getUsername());
                        currentBenutzer.getFriends().removeFriendRequest(name);

                        benutzerDataBase.child(name).setValue(gson.toJson(friend));
                        benutzerDataBase.child(currentBenutzer.getUsername()).setValue(gson.toJson(currentBenutzer));
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Benutzer friend = allBenutzer.stream().filter((b) -> b.getUsername().equals(name)).findFirst().get();
                        currentBenutzer.getFriends().removeFriendRequest(name);
                        friend.getFriends().addFriendDenied(currentBenutzer.getUsername());

                        benutzerDataBase.child(name).setValue(gson.toJson(friend));
                        benutzerDataBase.child(currentBenutzer.getUsername()).setValue(gson.toJson(currentBenutzer));
                    }
                });

                alert.show();
            }
        }
    }

    private void preferenceChanged(SharedPreferences sharedPrefs, String key)
    {
        Gson gson = new Gson();
        if(key.equals("notifications"))
        {
            boolean showNotifications = sharedPrefs.getBoolean("notifications", true);
            if(showNotifications)
            {
                Intent service = new Intent(this, NotificationService.class);
                startService(service);
            }
            else
            {
                Intent service = new Intent(this, NotificationService.class);
                stopService(service);
            }
        }
        else if(key.equals("private"))
        {
            boolean privateSetting = sharedPrefs.getBoolean("private", false);
            if(privateSetting)
            {
                currentBenutzer.setPrivateSettings(true);
            }
            else
            {
                currentBenutzer.setPrivateSettings(false);
            }
            benutzerDataBase.child(currentBenutzer.getUsername()).setValue(gson.toJson(currentBenutzer));
        }
    }
}
