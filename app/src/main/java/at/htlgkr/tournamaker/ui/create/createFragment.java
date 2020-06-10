package at.htlgkr.tournamaker.ui.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import at.htlgkr.tournamaker.Activities.FragmentsActivity;
import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.Classes.Games;
import at.htlgkr.tournamaker.R;
import at.htlgkr.tournamaker.Classes.Tournament;


/**
 * A simple {@link Fragment} subclass.
 */
public class createFragment extends Fragment
{
    private List<Benutzer> allBenutzer = FragmentsActivity.allBenutzer;
    private Benutzer currentBenutzer = FragmentsActivity.currentBenutzer;
    private static List<Tournament> allTournaments = FragmentsActivity.allTournaments;
    private static StorageReference firebaseStorage = FragmentsActivity.firebaseStorage;
    private static DatabaseReference tournamentsDataBase = FragmentsActivity.tournamentsDataBase;
    private View activityView;

    private ArrayAdapter<Games> gameAdapter;
    private ArrayAdapter<String> anzahlAdapter;
    private Spinner gameSpinner;
    private Spinner anzahlSpinner;

    public createFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activityView = inflater.inflate(R.layout.fragment_create, container, false);

        gameSpinner = activityView.findViewById(R.id.gameSpinner);
        anzahlSpinner = activityView.findViewById(R.id.anzahlSpinner);
        bindAdaptersToSpinners();


        Button create = activityView.findViewById(R.id.create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateClick();
            }
        });


        return activityView;
    }

    public void onCreateClick()
    {
        String tournamentName = ((TextView) activityView.findViewById(R.id.tv_tournamentName)).getText().toString();
        String tournamentPassword = ((TextView) activityView.findViewById(R.id.tv_tournamentPassword)).getText().toString();
        Games selectedGame = (Games) gameSpinner.getSelectedItem();
        int selectedAnzahl = Integer.parseInt((String) anzahlSpinner.getSelectedItem());

        if(allTournaments.stream().map(Tournament::getName).noneMatch((name) -> name.equals(tournamentName)))
        {
            Tournament newTournament = new Tournament(selectedAnzahl, tournamentName, tournamentPassword, selectedGame, currentBenutzer);

            Gson gson = new Gson();

            tournamentsDataBase.child(newTournament.getName()).setValue(gson.toJson(newTournament));
            allTournaments.add(newTournament);

            Snackbar snack = Snackbar.make(FragmentsActivity.fragmentActivityView.findViewById(android.R.id.content), "A new tournament was created", Snackbar.LENGTH_SHORT);

            View snackView = snack.getView();
            snackView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            snack.show();
        }
        else
        {
            Snackbar snack = Snackbar.make(FragmentsActivity.fragmentActivityView.findViewById(android.R.id.content), "Tournament-Name is already taken", Snackbar.LENGTH_SHORT);

            View snackView = snack.getView();
            snackView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            snack.show();
        }


    }

    private void bindAdaptersToSpinners()
    {
        gameAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item_layout,
                Games.values()
        );

        gameSpinner.setAdapter(gameAdapter);
        gameAdapter.notifyDataSetChanged();

        anzahlAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item_layout,
                Arrays.asList("8", "16")
        );

        anzahlSpinner.setAdapter(anzahlAdapter);
        anzahlAdapter.notifyDataSetChanged();
    }
}
