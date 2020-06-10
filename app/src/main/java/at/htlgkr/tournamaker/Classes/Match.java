package at.htlgkr.tournamaker.Classes;

import java.io.Serializable;

public class Match implements Serializable
{
    private Benutzer first;
    private Benutzer second;

    public Match(Benutzer first, Benutzer second) {
        this.first = first;
        this.second = second;
    }

    public Benutzer getFirst() {
        return first;
    }

    public void setFirst(Benutzer first) {
        this.first = first;
    }

    public Benutzer getSecond() {
        return second;
    }

    public void setSecond(Benutzer second) {
        this.second = second;
    }


}
