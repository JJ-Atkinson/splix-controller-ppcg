package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;
import com.nmerrill.kothcomm.game.games.AbstractGame;
import com.nmerrill.kothcomm.game.games.IteratedGame;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.game.scoring.Scoreboard;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

/**
 * Created by Jarrett on 01/31/17.
 */
public class SplixGame extends IteratedGame<SplixPlayer> {

    public SplixGame(int iterations) {
        super(iterations);
    }

    @Override
    protected void setup() {

    }

    @Override
    protected void step() {
        super.step();
        MutableMap<Submission<?>, Direction> playerMoves = Maps.mutable.empty();
        players.forEach(each -> playerMoves.put(each.getType(), each.makeMove(getReadOnlyGame(), null)));

    }

    private ReadOnlyGame getReadOnlyGame() {return null; // FIXME
    }

    @Override
    public Scoreboard getScores() {
        return null;
    }

    @Override
    public boolean finished() {
        return false;
    }
}
