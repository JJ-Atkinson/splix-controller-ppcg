package com.jatkin.splixkoth.ppcg.players;

import com.jatkin.splixkoth.ppcg.game.Direction;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlySplixPoint;
import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareRegion;
import javafx.util.Pair;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Comparator;

/**
 * Trap bot goes to the wall and traces the entirety around. Hopes that
 * the players in the middle die and that nobody challenges him. Nearly 
 * all turns are left turns.
 */
public class TrapBot extends SplixPlayer {

    /**
     * Mode when the bot is attempting to reach the wall from it's original spawn
     * location.
     */
    public static final int MODE_GOING_TO_WALL = 1;

    /**
     * Mode when we have reached the wall and are now going around the board.
     */
    public static final int MODE_FOLLOWING_WALL = 2;
    
    private int mode = MODE_GOING_TO_WALL;
    
    public static int WALL_EAST = 1;
    public static int WALL_NORTH = 2;
    public static int WALL_WEST = 3;
    public static int WALL_SOUTH = 4;
    
    private int currentWall = 0;

    /**
     * How long the bot would like to go before he turns around to go back home.
     */
    private static final int PREFERRED_LINE_DIST = 5;
    
    private int distToTravel = 0;
    
    private Direction lastMove = Direction.East;// could be anything that's not null
    private int lastTrailLength = 0;
    
    @Override
    protected Direction makeMove(ReadOnlyGame game, ReadOnlyBoard board) {
        Direction ret = null;
        MutableMap<Point2D, ReadOnlySplixPoint> view = board.getView();
        int trailLength = getTrailLength(board, view);
        
        if (trailLength == 0) {
            
            int closestWall = getClosestWall(board);
            Direction directionToWall = getDirectionToWall(closestWall);
            
            if (lastTrailLength != 0) {
                ret = lastMove.leftTurn();
                // move to the other half of 2 width line so we can start without shifting to the left
            }
            
            if (mode == MODE_GOING_TO_WALL && ret == null) {
                int distCanTravel = getDistCanTravel(
                        board.getPosition(this), board.getBounds(), directionToWall);
                if (distCanTravel == 0) mode = MODE_FOLLOWING_WALL;
                else ret = directionToWall;
                distToTravel = distCanTravel;
                
            }
            
            if (mode == MODE_FOLLOWING_WALL && ret == null) {
                int distCanTravel = 0;
                ret = directionToWall;
                while (distCanTravel == 0) {// keep turning left until we can get somewhere
                    ret = ret.leftTurn();
                    distCanTravel = getDistCanTravel(
                            board.getPosition(this), board.getBounds(), ret);
                }
                
                distToTravel = distCanTravel;
            }
        }
        
        // once we have started we are on auto pilot (can't run after the before block)
        else if (trailLength == distToTravel || trailLength == (distToTravel + 1))
            ret = lastMove.leftTurn();
        
        if (ret == null)// if we don't have a move otherwise, we must be on our trail. ret same as last time
            ret = lastMove;
        
        lastTrailLength = trailLength;
        lastMove = ret;
        return ret;
    }

    int getClosestWall(ReadOnlyBoard board) {
        return Lists.mutable.of(
                new Pair<>(WALL_NORTH, board.getBounds().getTop() - board.getPosition(this).getY()),
                new Pair<>(WALL_SOUTH, board.getPosition(this).getY()), 
                new Pair<>(WALL_EAST, board.getBounds().getRight() - board.getPosition(this).getX()),
                new Pair<>(WALL_WEST, board.getPosition(this).getX())
        ).min(Comparator.comparingInt(Pair::getValue)).getKey();
    }

    /**
     * This goes around some intended behavior to get the correct result. When a player goes outside his territory
     * the land under him is converted to a trail - on the next step of the game. So a trail length may be the count
     * of the trail locations plus one. That is what this function calculates. Depends on the whole trail being 
     * contained inside the view.
     * @return
     */
    int getTrailLength(ReadOnlyBoard board, MutableMap<Point2D, ReadOnlySplixPoint> view) {
        boolean isPlayerOutsideHome = !view.get(board.getPosition(this)).getOwner().equals(getThisHidden());
        int trailLength = view.count(rop -> rop.getClaimer().equals(getThisHidden()));
        return trailLength + (isPlayerOutsideHome? 1 : 0);
    }

    /**
     * Calculate how far we can travel in the direction before we hit the wall.
     * @return
     */
    int getDistCanTravel(Point2D currPos, SquareRegion bounds, Direction direction) {
        for (int i = 1; i <= PREFERRED_LINE_DIST; i++) {
            if (!bounds.inBounds(
                    Utils.addPoints(currPos,
                            Utils.multPoint(direction.vector, i))))
                return i-1;
        }
        return PREFERRED_LINE_DIST;
    }

    /**
     * Get which direction needs to be traveled to reach the specified wall.
     * Requires that neither Direction nor the values of `WALL_...` change.
     * @param targetWall
     * @return
     */
    Direction getDirectionToWall(int targetWall) {
        return Direction.values()[targetWall-1];
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    private int hc = (int)(Math.random()*Integer.MAX_VALUE);
    @Override
    public int hashCode() {return hc;}
}
