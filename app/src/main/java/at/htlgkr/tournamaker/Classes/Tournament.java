package at.htlgkr.tournamaker.Classes;


import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Tournament implements Serializable
{
    private Benutzer creator;
    private int maxTeilnehmer;
    private String name;
    private String password;
    private Games game;
    private List<Benutzer> teilnehmer;
    private List<Match> roundOf16;
    private List<Match> quarterFinals;
    private List<Match> semiFinals;
    private List<Match> finals;

    public Tournament(int maxTeilnehmer, String name, String password, Games game, Benutzer creator)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            this.password = Hasher.normalToHashedPassword(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        this.maxTeilnehmer = maxTeilnehmer;
        this.name = name;
        this.game = game;
        this.creator = creator;

        teilnehmer = new ArrayList<>();
        roundOf16 = new ArrayList<>();
        quarterFinals = new ArrayList<>();
        semiFinals = new ArrayList<>();
        finals = new ArrayList<>();

        fillTournamentsWithNones();

    }

    private void fillTournamentsWithNones()
    {
        if(maxTeilnehmer == 16)
        {
            for(int i = 0; i < 8; ++i)
            {
                roundOf16.add(new Match(new Benutzer("None", "none"), new Benutzer("None", "none")));
            }
        }

        for(int i = 0; i < 4; ++i)
        {
            quarterFinals.add(new Match(new Benutzer("None", "none"), new Benutzer("None", "none")));
        }
        for(int i = 0; i < 2; ++i)
        {
            semiFinals.add(new Match(new Benutzer("None", "none"), new Benutzer("None", "none")));
        }
        finals.add(new Match(new Benutzer("None", "none"), new Benutzer("None", "none")));
    }

    public void addBenutzerToTournament(Benutzer newBenutzer)
    {
        teilnehmer.add(newBenutzer);

        if(maxTeilnehmer == 16)
        {
            for(Match m: roundOf16)
            {
                if(m.getFirst().getUsername().equals("None"))
                {
                    m.setFirst(newBenutzer);
                    return;
                }
                else if(m.getSecond().getUsername().equals("None"))
                {
                    m.setSecond(newBenutzer);
                    return;
                }
            }
        }
        for(Match m: quarterFinals)
        {
            if(m.getFirst().getUsername().equals("None"))
            {
                m.setFirst(newBenutzer);
                return;
            }
            else if(m.getSecond().getUsername().equals("None"))
            {
                m.setSecond(newBenutzer);
                return;
            }
        }
        for(Match m: semiFinals)
        {
            if(m.getFirst().getUsername().equals("None"))
            {
                m.setFirst(newBenutzer);
                return;
            }
            else if(m.getSecond().getUsername().equals("None"))
            {
                m.setSecond(newBenutzer);
                return;
            }
        }
        for(Match m: finals)
        {
            if(m.getFirst().getUsername().equals("None"))
            {
                m.setFirst(newBenutzer);
                return;
            }
            else if(m.getSecond().getUsername().equals("None"))
            {
                m.setSecond(newBenutzer);
                return;
            }
        }

    }

    public Benutzer getCreator() {
        return creator;
    }

    public int getMaxTeilnehmer() {
        return maxTeilnehmer;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Games getGame() {
        return game;
    }

    public List<Benutzer> getTeilnehmer() {
        return teilnehmer;
    }

    public void setTeilnehmer(List<Benutzer> teilnehmer) {
        this.teilnehmer = teilnehmer;
    }

    public List<Match> getRoundOf16() {
        return roundOf16;
    }

    public void setRoundOf16(List<Match> roundOf16) {
        this.roundOf16 = roundOf16;
    }

    public List<Match> getQuarterFinals() {
        return quarterFinals;
    }

    public void setQuarterFinals(List<Match> quarterFinals) {
        this.quarterFinals = quarterFinals;
    }

    public List<Match> getSemiFinals() {
        return semiFinals;
    }

    public void setSemiFinals(List<Match> semiFinals) {
        this.semiFinals = semiFinals;
    }

    public List<Match> getFinals() {
        return finals;
    }

    public void setFinals(List<Match> finals) {
        this.finals = finals;
    }
}
