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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import at.htlgkr.tournamaker.Activities.FragmentsActivity;
import at.htlgkr.tournamaker.Activities.MainActivity;
import at.htlgkr.tournamaker.Activities.TournamentActivity;
import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.Classes.Hasher;
import at.htlgkr.tournamaker.Preferences.MySettingsActivity;
import at.htlgkr.tournamaker.Preferences.NotificationService;
import at.htlgkr.tournamaker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class profileFragment extends Fragment
{
    private Benutzer currentBenutzer = FragmentsActivity.currentBenutzer;
    private List<Benutzer> allBenutzer = FragmentsActivity.allBenutzer;
    private static StorageReference firebaseStorage = FragmentsActivity.firebaseStorage;
    private static DatabaseReference benutzerDataBase = FragmentsActivity.benutzerDataBase;
    private View activityView;

    public profileFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activityView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button friend = activityView.findViewById(R.id.friend_button);
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                friendButtonClick();
            }
        });

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
                        benutzerDataBase.child(currentBenutzer.getUsername()).removeValue();
                        firebaseStorage.child(currentBenutzer.getUsername()).delete();

                        Intent delete = new Intent(activityView.getContext(), MainActivity.class);
                        startActivity(delete);
                    }
                });

                alert.show();
            }
        });

        return activityView;
    }

    public void friendButtonClick()
    {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alert.setTitle("Enter a name");

        final EditText et_friendName = new EditText(getContext());
        alert.setView(et_friendName);

        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String friendName = et_friendName.getText().toString();
                Benutzer searchedBenutzer = allBenutzer.stream()
                        .filter((b) -> b.getUsername().equals(friendName))
                        .findFirst()
                        .orElse(null);

                if(searchedBenutzer != null)
                {
                    if(searchedBenutzer.getFriends()
                            .getFriendList()
                            .stream()
                            .anyMatch((name) -> name.equals(currentBenutzer.getUsername())))
                    {
                        Snackbar snack = Snackbar.make(FragmentsActivity.fragmentActivityView.findViewById(android.R.id.content), "You have already sent a Friend-Request to "+searchedBenutzer.getUsername(), Snackbar.LENGTH_SHORT);

                        View snackView = snack.getView();
                        snackView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        snack.show();
                    }
                    else
                    {
                        Gson gson = new Gson();
                        searchedBenutzer.getFriends().addFriendRequest(currentBenutzer.getUsername());
                        benutzerDataBase.child(searchedBenutzer.getUsername()).setValue(gson.toJson(searchedBenutzer));
                    }
                }
                else
                {
                    Snackbar snack = Snackbar.make(FragmentsActivity.fragmentActivityView.findViewById(android.R.id.content), "No User was found", Snackbar.LENGTH_SHORT);

                    View snackView = snack.getView();
                    snackView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    snack.show();
                }
            }
        });

        alert.setNegativeButton("Cancel", null);

        alert.show();
    }

}
