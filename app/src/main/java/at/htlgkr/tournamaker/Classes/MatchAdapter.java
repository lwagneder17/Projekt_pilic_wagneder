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

public class MatchAdapter extends BaseAdapter
{
    private List<Match> matches;
    private int layoutId;
    private LayoutInflater inflater;

    public MatchAdapter(Context ctx, int layoutId, List<Match> matches)
    {
        this.matches = matches;
        this.layoutId = layoutId;
        this.inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return matches.size();
    }

    @Override
    public Match getItem(int position) {
        return matches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Match m = matches.get(position);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;



        ((TextView) listItem.findViewById(R.id.tv_first)).setText(m.getFirst().getUsername());
        ((TextView) listItem.findViewById(R.id.tv_second)).setText(m.getSecond().getUsername());


        return listItem;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
