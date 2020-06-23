package at.htlgkr.tournamaker.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import at.htlgkr.tournamaker.Activities.FragmentsActivity;
import at.htlgkr.tournamaker.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class TournamentAdapter extends BaseAdapter
{
    private List<Tournament> tournaments;
    private int layoutId;
    private LayoutInflater inflater;
    private static StorageReference firebaseStorage = FragmentsActivity.firebaseStorage;

    public TournamentAdapter(Context ctx, int layoutId, List<Tournament> tournaments) {
        this.tournaments = tournaments;
        this.layoutId = layoutId;
        this.inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tournaments.size();
    }

    @Override
    public Tournament getItem(int position) {
        return tournaments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Tournament t = tournaments.get(position);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;


        ((TextView) listItem.findViewById(R.id.tv_title)).setText(t.getName());

        ((TextView) listItem.findViewById(R.id.tv_anzahl)).setText(t.getTeilnehmer().size()+" / "+t.getMaxTeilnehmer());
        ImageView gameImage = listItem.findViewById(R.id.gameImage);
        String imageName = "";

        switch(t.getGame())
        {
            case CSGO:
                imageName = "csgologo.png";
                break;

            case FORTNITE:
                imageName = "fortnitelogo.png";
                break;

            case VALORANT:
                imageName = "valorantlogo.png";
                break;

            case LeagueOfLegends:
                imageName = "leaguelogo.png";
                break;

            default:
                imageName = "othergamelogo.png";
                break;
        }

        firebaseStorage.child(imageName).getBytes(5000000).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes)
            {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                gameImage.setImageBitmap(bm);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Log.d("SearchFragment", "Loading Picture failed");
            }
        });

        return listItem;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
