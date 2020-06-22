package at.htlgkr.tournamaker.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import at.htlgkr.tournamaker.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FriendsAdapter extends BaseAdapter
{
    private List<Benutzer> friends;
    private int layoutId;
    private LayoutInflater inflater;

    public FriendsAdapter(Context ctx, int layoutId, List<Benutzer> friends)
    {
        this.friends = friends;
        this.layoutId = layoutId;
        this.inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Benutzer getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Benutzer b = friends.get(position);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;

        ((TextView) listItem.findViewById(R.id.tv_name)).setText(b.getUsername());

       ((TextView) listItem.findViewById(R.id.tv_wins)).setText(""+b.getStatistics().getWins());
       ((TextView) listItem.findViewById(R.id.tv_losses)).setText(""+b.getStatistics().getLosses());
       ((TextView) listItem.findViewById(R.id.tv_games)).setText(""+b.getStatistics().getGames());


        return listItem;
    }

    public List<Benutzer> getMatches() {
        return friends;
    }

    public void setMatches(List<Benutzer> friends) {
        this.friends = friends;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}

