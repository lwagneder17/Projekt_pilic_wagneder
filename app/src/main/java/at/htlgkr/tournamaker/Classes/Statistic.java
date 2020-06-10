package at.htlgkr.tournamaker.Classes;

import java.io.Serializable;

public class Statistic implements Serializable
{
    private int wins;
    private int losses;
    private int games;

    public Statistic(int wins, int losses, int games) {
        this.wins = wins;
        this.losses = losses;
        this.games = games;
    }

    public void addWin()
    {
        this.wins++;
    }

    public void addLoss()
    {
        this.losses++;
    }

    public void addGame()
    {
        this.games++;
    }


    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getGames() {
        return games;
    }
}
