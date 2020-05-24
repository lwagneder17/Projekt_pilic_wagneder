package at.htlgkr.tournamaker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import at.htlgkr.tournamaker.Benutzer;
import at.htlgkr.tournamaker.FireBase.asyncGetAllUsers;
import at.htlgkr.tournamaker.R;


public class MainActivity extends AppCompatActivity
{
    private DatabaseReference firebaseDatabase;
    private List<Benutzer> allBenutzer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        asyncGetAllUsers asyncGetAllUsers = new asyncGetAllUsers();
        asyncGetAllUsers.execute("getAllUsers");
        try
        {
            allBenutzer = asyncGetAllUsers.get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        TextView register = findViewById(R.id.tv_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

    }
}
