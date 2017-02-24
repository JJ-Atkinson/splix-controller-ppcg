package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.SplixSettings;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;
import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.games.AbstractGame;
import com.nmerrill.kothcomm.game.games.IteratedGame;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareRegion;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.game.scoring.Scoreboard;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Set;

/**
 * Created by Jarrett on 01/31/17.
 */
public class SplixGame extends IteratedGame<SplixPlayer> {

    private int size;
    private final int scoreForKill = SplixSettings.pointsForKill;
    
    private SplixBoard board;
    private Scoreboard<SplixPlayer> scoreboard;
    private final MutableSet<Submission<SplixPlayer>> deadPlayers = Sets.mutable.empty();
    
    public SplixGame(int size) {
        this.size = size;
    }

    @Override
    public int getNumIterations() {
        return SplixSettings.gameIterationsCount;// FIXME
    }
    

    @Override
    public void setup() {
        super.setup();
        board = new SplixBoard(new SquareRegion(size));
        board.initPlayers(getPlayerPositions());
        scoreboard = new Scoreboard<>();
    }
    
    
    private MutableMap<Submission<SplixPlayer>, Point2D> getPlayerPositions() {
        MutableMap<Submission<SplixPlayer>, Point2D> ret = Maps.mutable.empty();
        players.forEach(p -> 
            ret.put(p.getType(), new Point2D(random.nextInt(board.getBounds().getRight()-1), random.nextInt(board.getBounds().getTop() - 1)))
        );
        return ret;
    }

    public SplixBoard getBoard() {return board;}
    @Override
    public void step() {
        super.step();
        
        MutableMap<Submission<?>, Direction> playerMoves = Maps.mutable.empty();

        players.select(p -> !deadPlayers.contains(p.getType())).forEach(each -> 
                playerMoves.put(each.getType(),
                        each.makeMove(new ReadOnlyGame(this),
                                 getReadOnlyBoardForPosition(board.getPlayerPositions().get(each.getType())))));
        
        MutableMap<Submission<SplixPlayer>, Submission<SplixPlayer>> deaths = board.getDeathsFromMoves(playerMoves);
        board.killPlayers(deaths.keySet());
        deadPlayers.addAll(deaths.keySet());
        deaths.forEach((p, killer) -> scoreboard.addScore(getPlayerForType(killer), scoreForKill));
        
        board.applyMoves(playerMoves);
        board.checkPlayerTrailsConnected();
        
        // perform a fill for all players - it may have removed a player that was preventing filling for another player
        if (deaths.notEmpty())
            players.forEach(p -> board.fillPlayerCapturedArea(p.getType()));
    }

    private SplixPlayer getPlayerForType(Submission<SplixPlayer> type) {
        return players.select(p -> p.getType().equals(type)).getOnly();
    }

    private ReadOnlyBoard getReadOnlyBoardForPosition(Point2D pos) {
        SquareRegion area = new SquareRegion(
                Utils.addPoints(pos, new Point2D(-SplixSettings.viewingAreaSize.getX()/2,
                        -SplixSettings.viewingAreaSize.getY()/2)),
                Utils.addPoints(pos, new Point2D(SplixSettings.viewingAreaSize.getX()/2,
                        SplixSettings.viewingAreaSize.getY()/2))
        );
        
        return new ReadOnlyBoard(this.board, area);
    }
    
    private boolean hasComputedScores = false;

    /**
     * Calculates the score of the game. Returns null if the game is not finished.
     * @return
     */
    @Override
    public Scoreboard<SplixPlayer> getScores() {
        if (!finished())
            return null;
        if (hasComputedScores)
            return scoreboard;
        
        MutableSet<Submission<SplixPlayer>> deadPlayers = 
                players.collect(SplixPlayer::getType).toSet()
                        .difference(board.getPlayerPositions().keysView().toSet());
        deadPlayers.forEach(p -> scoreboard.setScore(getPlayerForType(p), 0));
        
        players.select(p -> !deadPlayers.contains(p.getType()))
               .forEach(p -> scoreboard.addScore(p, board.countPointsOwnedByPlayer(p.getType())));
        hasComputedScores = true;
        return scoreboard;
    }
}
