package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;
import com.nmerrill.kothcomm.game.games.AbstractGame;
import com.nmerrill.kothcomm.game.games.IteratedGame;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.game.scoring.Scoreboard;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;

import java.util.Set;

/**
 * Created by Jarrett on 01/31/17.
 */
public class SplixGame extends IteratedGame<SplixPlayer> {

    private int size;
    private int iterations;
    private final int scoreForKill = 300;
    
    private SplixBoard board;
    private Scoreboard<Submission<SplixPlayer>> scoreboard;

    public SplixGame(int size, int iterations) {
        super();
        this.size = size;
        this.iterations = iterations;
    }// FIXME

    @Override
    public int getNumIterations() {
        return 0;// FIXME
    }
    

    @Override
    protected void setup() {
        board = new SplixBoard(new SquareBounds(size));
        board.initPlayers(getPlayerPositions());
        scoreboard = new Scoreboard<>();
    }
    
    
    private MutableMap<Submission<SplixPlayer>, Point2D> getPlayerPositions() {
        MutableMap<Submission<SplixPlayer>, Point2D> ret = Maps.mutable.empty();
        players.forEach(p -> 
            ret.put(p, new Point2D(random.nextInt(board.getBounds().getLeft()-1), random.nextInt(board.getBounds().getBottom() - 1)))
        );
        return ret;
    }

    @Override
    protected void step() {
        super.step();
        
        MutableMap<Submission<?>, Direction> playerMoves = Maps.mutable.empty();
        players.forEach(each -> playerMoves.put(each.getType(), each.makeMove(getReadOnlyGame(), null)));
        
        MutableMap<Submission<SplixPlayer>, Submission<SplixPlayer>> deaths = board.getDeathsFromMoves(playerMoves);
        board.killPlayers(deaths.keySet());
        deaths.forEach((p, killer) -> scoreboard.addScore(killer, scoreForKill));
        
        board.applyMoves(playerMoves);
        board.checkPlayerTrailsConnected();
        
        // perform a fill for all players - it may have removed a player that was preventing filling for another player
        if (deaths.notEmpty())
            players.forEach(p -> board.fillPlayerCapturedArea(p.getType()));
    }

    
    private ReadOnlyGame getReadOnlyGame() {return null; // FIXME
    }

    
    private boolean hasComputedScores = false;

    /**
     * Calculates the score of the game. Returns null if the game is not finished.
     * @return
     */
    @Override
    public Scoreboard getScores() {
        if (!finished())
            return null;
        if (hasComputedScores)
            return scoreboard;
        
        MutableSet<Submission<SplixPlayer>> deadPlayers = 
                players.collect(SplixPlayer::getType).toSet()
                        .difference(board.getPlayerPositions().keysView().toSet());
        deadPlayers.forEach(p -> scoreboard.setScore(p, 0));
        
        players.select(p -> !deadPlayers.contains(p.getType()))
               .forEach(p -> scoreboard.addScore(p.getType(), board.countPointsOwnedByPlayer(p.getType())));
        hasComputedScores = true;
        return scoreboard;
    }
}
