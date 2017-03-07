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
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.Set;

/**
 * Created by Jarrett on 01/31/17.
 */
public class SplixGame extends IteratedGame<SplixPlayer> {

    private int size;
    private final int scoreForKill = SplixSettings.pointsForKill;
    
    private SplixBoard board;
    private Scoreboard<SplixPlayer> scoreboard;
    private final MutableSet<SplixPlayer> deadPlayers = Sets.mutable.empty();
    
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
    
    
    public MutableMap<SplixPlayer, Point2D> getPlayerPositions() {
        MutableMap<SplixPlayer, Point2D> ret = Maps.mutable.empty();
        players.forEach(p -> 
            ret.put(p, new Point2D(random.nextInt(board.getBounds().getRight()-1), random.nextInt(board.getBounds().getTop() - 1)))
        );
        return ret;
    }

    public SplixBoard getBoard() {return board;}
    
    @Override
    public void step() {
        super.step();
        
        MutableMap<SplixPlayer, Direction> playerMoves = Maps.mutable.empty();

        players.select(p -> !deadPlayers.contains(p)).forEach(each -> 
                playerMoves.put(each,
                        each.makeMove(new ReadOnlyGame(this),
                                 getReadOnlyBoardForPosition(board.getPlayerPositions().get(each)))));
        
        MutableMap<SplixPlayer, SplixPlayer> deaths = board.getDeathsFromMoves(playerMoves);
        
        MutableMap<SplixPlayer, MutableSet<Point2D>> deadPlayersChangedArea = deaths.collect((player, k_) -> 
                Tuples.pair(player, 
                        board.locations().select(pos -> board.get(pos).getOwner() == player)));
        deadPlayersChangedArea.forEach((pl, area) -> area.add(board.getPlayerPositions().get(pl)));
        
        board.killPlayers(deaths.keySet());
        deadPlayers.addAll(deaths.keySet());
        deaths.forEach((p, killer) -> scoreboard.addScore(getPlayerForType(killer), scoreForKill));
        
        board.applyMoves(playerMoves);
        board.checkPlayerTrailsConnected();
        
        // perform a fill for all players - it may have removed a player that was preventing filling for another player
        deadPlayersChangedArea.forEach((d_, areaChanged) -> {
            
            players.forEach(p -> board.fillPlayerCapturedArea(p, areaChanged));
        });
    }

    private SplixPlayer getPlayerForType(SplixPlayer player) {
        return players.select(p -> p.equals(player)).getOnly();
    }

    public ReadOnlyBoard getReadOnlyBoardForPosition(Point2D pos) {
        SquareRegion area = new SquareRegion(
                Utils.addPoints(pos, new Point2D(-SplixSettings.viewingAreaSize.getX()/2,
                        -SplixSettings.viewingAreaSize.getY()/2)),
                Utils.addPoints(pos, new Point2D(SplixSettings.viewingAreaSize.getX()/2,
                        SplixSettings.viewingAreaSize.getY()/2))
        );
//        SquareRegion bounds = board.getBounds();
//
//        area = new SquareRegion(
//                new Point2D(Math.max(area.getLeft(), bounds.getLeft()),
//                            Math.max(area.getBottom(), bounds.getBottom())),
//                new Point2D(Math.min(area.getRight(), bounds.getRight()),
//                            Math.min(area.getTop(), bounds.getTop())));
        
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
        
        MutableSet<SplixPlayer> deadPlayers = 
                players.toSet().difference(board.getPlayerPositions().keysView().toSet());
        deadPlayers.forEach(p -> scoreboard.setScore(getPlayerForType(p), 0));
        
        players.select(p -> !deadPlayers.contains(p))
               .forEach(p -> scoreboard.addScore(p, board.countPointsOwnedByPlayer(p)));
        hasComputedScores = true;
        return scoreboard;
    }

    @Override
    public boolean finished() {
        return super.finished() && board.getPlayerPositions().size() != 0;
    }
}
