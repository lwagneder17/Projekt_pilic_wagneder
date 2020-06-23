package at.htlgkr.tournamaker.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Friends implements Serializable
{
    private List<String> friendList;
    private List<String> friendRequests;
    private List<String> friendDenied;
    private List<String> friendRemoved;

    public Friends()
    {
        friendList = new ArrayList<>();
        friendRequests = new ArrayList<>();
        friendDenied = new ArrayList<>();
        friendRemoved = new ArrayList<>();
    }

    public List<String> getFriendRemoved() {
        return friendRemoved;
    }

    public void addFriendRemoved(String name)
    {
        friendRemoved.add(name);
    }

    public void addFriendDenied(String name)
    {
        friendDenied.add(name);
    }

    public List<String> getFriendDenied() {
        return friendDenied;
    }

    public void addFriendRequest(String name)
    {
        friendRequests.add(name);
    }

    public void removeFriendRequest(String name)
    {
        friendRequests.remove(name);
    }

    public void addFriend(String name)
    {
        friendList.add(name);
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public List<String> getFriendRequests() {
        return friendRequests;
    }
}

