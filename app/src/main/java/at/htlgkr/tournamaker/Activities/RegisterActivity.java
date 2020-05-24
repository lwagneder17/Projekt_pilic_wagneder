package at.htlgkr.tournamaker.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import at.htlgkr.tournamaker.Benutzer;
import at.htlgkr.tournamaker.Hasher;
import at.htlgkr.tournamaker.R;

public class RegisterActivity extends AppCompatActivity
{
    private final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private Bitmap cameraPicture;

    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        Button camera = findViewById(R.id.camera_button);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
            }
        });

        Button register = findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onClickRegister();
            }
        });

        if(ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 420);
        }
    }


    public void onClickRegister()
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            Gson gson = new Gson();

            String username = ((TextView) findViewById(R.id.tv_username)).getText().toString();
            String password = ((TextView) findViewById(R.id.tv_password)).getText().toString();
            if(!username.isEmpty() || !password.isEmpty())
            {
                String securedPassword = Hasher.normalToHashedPassword(digest.digest(password.getBytes(StandardCharsets.UTF_8)));

                Benutzer newBenutzer = new Benutzer(username, securedPassword, Hasher.bitmapToString(cameraPicture));
                firebaseDatabase.child("users").child(newBenutzer.getUsername()).setValue(gson.toJson(newBenutzer));

                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
            }
            else
            {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Fields are empty", Snackbar.LENGTH_SHORT);

                View snackView = snack.getView();
                snackView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
                snack.show();

            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE)
        {
            if (resultCode == RESULT_OK)
            {
                cameraPicture = (Bitmap) data.getExtras().get("data");

            }
        }
    }
}
