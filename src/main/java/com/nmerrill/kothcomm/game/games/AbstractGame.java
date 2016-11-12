package com.nmerrill.kothcomm.game.games;

import com.nmerrill.kothcomm.game.AbstractPlayer;
import com.nmerrill.kothcomm.game.scoring.Scoreboard;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Random;

public abstract class AbstractGame<T extends AbstractPlayer<T>> {
    protected Random random;
    protected MutableList<T> players;
    private boolean started;
    private boolean finished;
    private Scoreboard<T> scores;

    public AbstractGame(){
        this.players = Lists.mutable.empty();
        this.random = new Random();
        this.started = false;
        this.finished = false;
    }

    public final void addPlayers(MutableCollection<T> players){
        this.players.addAll(players);
        if (random != null) {
            players.forEachWith(AbstractPlayer::setRandom, random);
        }
    }

    public final void setRandom(Random random){
        this.random = random;
        if (players != null){
            players.forEachWith(AbstractPlayer::setRandom, random);
        }
    }

    public void start(){
        if (!started){
            setup();
            started = true;
        }
    }

    protected abstract void setup();

    protected abstract boolean step();

    public abstract Scoreboard<T> getScores();

    public final boolean next(){
        if (finished){
            return false;
        }
        if (!started){
            setup();
            started = true;
            return true;
        }
        if (step()) {
            return true;
        }
        finished = true;
        scores = getScores();
        return false;
    }

    public final boolean started(){
        return started;
    }

    public final boolean finished(){
        return finished;
    }

    public final Scoreboard<T> run(){
        setup();
        started = true;
        //noinspection StatementWithEmptyBody
        while (step()){ }
        finished = true;
        scores = getScores();
        return scores;
    }


}