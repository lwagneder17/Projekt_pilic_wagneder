package at.htlgkr.tournamaker.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.tournamaker.Activities.FragmentsActivity;
import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.Classes.FriendsAdapter;
import at.htlgkr.tournamaker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class statisticFragment extends Fragment
{
    private List<Benutzer> allBenutzer = FragmentsActivity.allBenutzer;
    private Benutzer currentBenutzer = FragmentsActivity.currentBenutzer;
    private List<Benutzer> friends = new ArrayList<>();
    private View activityView;
    private FriendsAdapter friendsAdapter;

    public statisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_statistics, container, false);
        ListView friendsListView = activityView.findViewById(R.id.friendsListView);
        bindAdapterToListView(friendsListView);



        ImageView search = activityView.findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tv_search = ((TextView) activityView.findViewById(R.id.tv_search)).getText().toString();
                List<Benutzer> filtered;
                if(tv_search.isEmpty() || tv_search.equals(" "))
                {
                    filtered = friends;
                }
                else
                {
                    filtered = friends
                            .stream()
                            .filter((b) -> b.getUsername().equals(tv_search))
                            .collect(Collectors.toList());
                }

                friendsAdapter = new FriendsAdapter(getContext(), R.layout.frienditem_layout, filtered);

                friendsListView.setAdapter(friendsAdapter);
                friendsAdapter.notifyDataSetChanged();
            }
        });


        return activityView;
    }

    private void bindAdapterToListView(ListView lv)
    {
        for(Benutzer b: allBenutzer)
        {
            if(currentBenutzer.getFriends().getFriendList().contains(b.getUsername()))
            {
                if(!b.getPrivateSettings())
                {
                    friends.add(b);
                }
            }
        }


        friendsAdapter = new FriendsAdapter(getContext(), R.layout.frienditem_layout, friends);

        lv.setAdapter(friendsAdapter);
        friendsAdapter.notifyDataSetChanged();

    }
}
