package at.htlgkr.tournamaker.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.Classes.Hasher;
import at.htlgkr.tournamaker.Classes.Match;
import at.htlgkr.tournamaker.Classes.MatchAdapter;
import at.htlgkr.tournamaker.R;
import at.htlgkr.tournamaker.Classes.Tournament;

public class TournamentActivity extends AppCompatActivity
{
    private Tournament selectedTournament;
    private Benutzer currentBenutzer = FragmentsActivity.currentBenutzer;
    private MatchAdapter matchAdapter;
    private ArrayAdapter<String> spinnerAdapter;
    private static StorageReference firebaseStorage = FragmentsActivity.firebaseStorage;
    private static DatabaseReference tournamentsDataBase = FragmentsActivity.tournamentsDataBase;
    private static DatabaseReference benutzerDataBase = FragmentsActivity.benutzerDataBase;

    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);
        getSupportActionBar().hide();
        Spinner finalSpinner = findViewById(R.id.finalSpinner);
        ListView matchListView = findViewById(R.id.matchListView);

        Intent i = getIntent();
        if(i.getExtras() != null)
        {
            Bundle extra = i.getBundleExtra("extra");
            selectedTournament = (Tournament) extra.getSerializable("selectedTournament");
        }


        Button join = findViewById(R.id.join_button);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinTournament();
            }

        });

        matchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Match selectedMatch = matchAdapter.getItem(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(TournamentActivity.this);
                alert.setTitle("Who is the Winner?");

                String[] array = new String[]{selectedMatch.getFirst().getUsername(), selectedMatch.getSecond().getUsername()};
                alert.setSingleChoiceItems(array, 0, null);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        if(currentBenutzer.getUsername().equals(selectedTournament.getCreator().getUsername()))
                        {
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            Benutzer winner;
                            switch(selectedPosition)
                            {
                                case 0:
                                    winner = selectedMatch.getFirst();
                                    break;

                                case 1:
                                    winner = selectedMatch.getSecond();
                                    break;

                                default:
                                    throw new IllegalStateException("Unexpected value: " + selectedPosition);
                            }

                            switch((String) finalSpinner.getSelectedItem())
                            {
                                case "Round of 16":
                                    selectedTournament.getRoundOf16().remove(selectedMatch);
                                    break;

                                case "Quarter":
                                    selectedTournament.getQuarterFinals().remove(selectedMatch);
                                    break;

                                case "Semi":
                                    selectedTournament.getSemiFinals().remove(selectedMatch);
                                    break;

                                case "Final":
                                    selectedTournament.getFinals().remove(selectedMatch);
                                    break;
                            }

                            matchAdapter.notifyDataSetChanged();
                            moveWinnerUp(finalSpinner, winner);
                        }
                        else
                        {
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "You are not the Creator of the tournament", Snackbar.LENGTH_SHORT);
                            View snackView = snack.getView();
                            snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
                            snack.show();
                        }

                    }
                });

                alert.setNegativeButton("Cancel",null);
                alert.show();
                return false;
            }
        });

        finalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerAdapter.getItem(position);
                switch(selected)
                {
                    case "Round of 16":
                        matchAdapter.setMatches(selectedTournament.getRoundOf16());
                        break;

                    case "Quarter":
                        matchAdapter.setMatches(selectedTournament.getQuarterFinals());
                        break;

                    case "Semi":
                        matchAdapter.setMatches(selectedTournament.getSemiFinals());
                        break;

                    case "Final":
                        matchAdapter.setMatches(selectedTournament.getFinals());
                        break;
                }
                matchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                matchAdapter.setMatches(selectedTournament.getRoundOf16());
                matchAdapter.notifyDataSetChanged();
            }
        });

        bindAdapterToSpinner(finalSpinner);
        bindAdapterToListView(matchListView);

    }

    private void joinTournament()
    {
        if(!selectedTournament.getTeilnehmer().isEmpty())
        {
            if((selectedTournament.getTeilnehmer()
                    .stream()
                    .map(Benutzer::getUsername)
                    .noneMatch((n) -> n.equals(currentBenutzer.getUsername()))) || !selectedTournament.getCreator().getUsername().equals(currentBenutzer.getUsername()))
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(TournamentActivity.this);
                alert.setTitle("Enter the password needed to join");

                final EditText et_password = new EditText(TournamentActivity.this);
                alert.setView(et_password);

                alert.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MessageDigest digest = null;
                        try
                        {
                            digest = MessageDigest.getInstance("SHA-256");
                        }
                        catch (NoSuchAlgorithmException e)
                        {
                            e.printStackTrace();
                        }

                        String password = Hasher.normalToHashedPassword(digest.digest(et_password.getText().toString().getBytes(StandardCharsets.UTF_8)));
                        if(password.equals(selectedTournament.getPassword()))
                        {
                            selectedTournament.addBenutzerToTournament(currentBenutzer);
                            tournamentsDataBase.child(selectedTournament.getName()).setValue(gson.toJson(selectedTournament));

                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "You joined the tournament", Snackbar.LENGTH_SHORT);
                            View snackView = snack.getView();
                            snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
                            snack.show();
                        }
                        else
                        {
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Wrong Password", Snackbar.LENGTH_SHORT);
                            View snackView = snack.getView();
                            snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
                            snack.show();
                        }
                        matchAdapter.notifyDataSetChanged();
                    }
                });

                alert.show();
            }
            else
            {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "You are either already in the tournament or you are the Creator of the Tournament", Snackbar.LENGTH_SHORT);
                View snackView = snack.getView();
                snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
                snack.show();
            }
        }
        else
        {
            if(!selectedTournament.getCreator().getUsername().equals(currentBenutzer.getUsername()))
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(TournamentActivity.this);
                alert.setTitle("Please enter the password needed to join");

                final EditText et_password = new EditText(TournamentActivity.this);
                alert.setView(et_password);

                alert.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MessageDigest digest = null;
                        try
                        {
                            digest = MessageDigest.getInstance("SHA-256");
                        }
                        catch (NoSuchAlgorithmException e)
                        {
                            e.printStackTrace();
                        }

                        String password = Hasher.normalToHashedPassword(digest.digest(et_password.getText().toString().getBytes(StandardCharsets.UTF_8)));
                        if(password.equals(selectedTournament.getPassword()))
                        {
                            selectedTournament.addBenutzerToTournament(currentBenutzer);
                            tournamentsDataBase.child(selectedTournament.getName()).setValue(gson.toJson(selectedTournament));

                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "You joined the tournament", Snackbar.LENGTH_SHORT);
                            View snackView = snack.getView();
                            snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
                            snack.show();
                        }
                        else
                        {
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Wrong Password", Snackbar.LENGTH_SHORT);
                            View snackView = snack.getView();
                            snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
                            snack.show();
                        }
                        matchAdapter.notifyDataSetChanged();
                    }
                });
                alert.show();
            }
            else
            {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "You are the Creator of the Tournament", Snackbar.LENGTH_SHORT);
                View snackView = snack.getView();
                snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
                snack.show();
            }
        }
    }


    private void moveWinnerUp(Spinner spinner, Benutzer winner)
    {
        int position = spinner.getSelectedItemPosition() + 1;
        String whichFinal;
        if(position != 4)
        {
            whichFinal = spinnerAdapter.getItem(position);
        }
        else
        {
            String text = "The WINNER of the tournament "+selectedTournament.getName().toUpperCase()+" is "+winner.getUsername().toUpperCase()+"!";
            winner.getStatistics().addGame();
            winner.getStatistics().addWin();
            benutzerDataBase.child(winner.getUsername()).setValue(gson.toJson(winner));

            for(Benutzer b: selectedTournament.getTeilnehmer())
            {
                if(!b.getUsername().equals(winner.getUsername()))
                {
                    b.getStatistics().addGame();
                    b.getStatistics().addLoss();
                    benutzerDataBase.child(b.getUsername()).setValue(gson.toJson(b));
                }
            }

            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT);
            View snackView = snack.getView();
            snackView.setBackgroundColor(ContextCompat.getColor(TournamentActivity.this, R.color.colorPrimary));
            snack.show();


            tournamentsDataBase.child(selectedTournament.getName()).removeValue();
            return;
        }

        switch(whichFinal)
        {
            case "Quarter":
                for(Match m: selectedTournament.getQuarterFinals())
                {
                    if(!m.getFirst().getUsername().equals("none"))
                    {
                        m.setFirst(winner);
                        break;
                    }
                    else if(!m.getSecond().getUsername().equals("none"))
                    {
                        m.setSecond(winner);
                        break;
                    }
                }
                break;

            case "Semi":
                for(Match m: selectedTournament.getSemiFinals())
                {
                    if(!m.getFirst().getUsername().equals("none"))
                    {
                        m.setFirst(winner);
                        break;
                    }
                    else if(!m.getSecond().getUsername().equals("none"))
                    {
                        m.setSecond(winner);
                        break;
                    }
                }
                break;

            case "Final":
                for(Match m: selectedTournament.getFinals())
                {
                    if(!m.getFirst().getUsername().equals("none"))
                    {
                        m.setFirst(winner);
                        break;
                    }
                    else if(!m.getSecond().getUsername().equals("none"))
                    {
                        m.setSecond(winner);
                        break;
                    }
                }
                break;
        }

        tournamentsDataBase.child(selectedTournament.getName()).setValue(gson.toJson(selectedTournament));

    }

    private void bindAdapterToListView(ListView lv)
    {
        matchAdapter = new MatchAdapter(this, R.layout.matchitem_layout, selectedTournament.getRoundOf16());

        lv.setAdapter(matchAdapter);
        matchAdapter.notifyDataSetChanged();

    }

    private void bindAdapterToSpinner(Spinner sp)
    {
        spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_layout,
                Arrays.asList("Round of 16", "Quarter", "Semi", "Final")
        );
        sp.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();

    }


}
