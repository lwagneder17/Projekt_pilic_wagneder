package at.htlgkr.tournamaker.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import at.htlgkr.tournamaker.Activities.FragmentsActivity;
import at.htlgkr.tournamaker.Activities.MainActivity;
import at.htlgkr.tournamaker.Benutzer;
import at.htlgkr.tournamaker.NotificationService;
import at.htlgkr.tournamaker.Preferences.MySettingsActivity;
import at.htlgkr.tournamaker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class profileFragment extends Fragment
{
    private List<Benutzer> allBenutzer = FragmentsActivity.allBenutzer;
    private Benutzer currentBenutzer = FragmentsActivity.currentBenutzer;
    private static StorageReference firebaseStorage = FragmentsActivity.firebaseStorage;
    private static DatabaseReference firebaseDatabase = FragmentsActivity.firebaseDatabase;
    private View activityView;

    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener;

    public profileFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activityView = inflater.inflate(R.layout.fragment_profile, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(activityView.getContext());
        preferencesChangeListener = this::preferenceChanged;
        prefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        ImageView profilePic = activityView.findViewById(R.id.profilePic);
        firebaseStorage.child(currentBenutzer.getUsername()).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes)
            {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePic.setImageBitmap(bm);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Toast.makeText(activityView.getContext(), "Loading Picture failed", Toast.LENGTH_LONG).show();
            }
        });

        TextView welcomeMessage = activityView.findViewById(R.id.tv_welcome);
        welcomeMessage.setText(currentBenutzer.getUsername());

        Button settings = activityView.findViewById(R.id.settings_button);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent preference = new Intent(activityView.getContext(), MySettingsActivity.class);
                startActivityForResult(preference, 69);
            }
        });

        Button signOut = activityView.findViewById(R.id.signoff_button);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(activityView.getContext(), MainActivity.class);
                startActivity(logout);
            }
        });

        Button delete = activityView.findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(activityView.getContext());
                alert.setTitle("Are you Sure?");
                alert.setNegativeButton("CANCEL", null);
                alert.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        firebaseDatabase.child(currentBenutzer.getUsername()).removeValue();

                        Intent delete = new Intent(activityView.getContext(), MainActivity.class);
                        startActivity(delete);
                    }
                });

                alert.show();
            }
        });

        return activityView;
    }

    private void preferenceChanged(SharedPreferences sharedPrefs, String key)
    {

        if(key.equals("notifications"))
        {
            boolean showNotifications = sharedPrefs.getBoolean("notifications", true);
            if(showNotifications)
            {
                //Intent service = new Intent(getContext(), NotificationService.class);
                //getActivity().startService(service);
            }
            else
            {
                //Intent service = new Intent(getContext(), NotificationService.class);
                //getActivity().stopService(service);
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
            firebaseDatabase.child(currentBenutzer.getUsername()).setValue(currentBenutzer);
        }
    }

}
