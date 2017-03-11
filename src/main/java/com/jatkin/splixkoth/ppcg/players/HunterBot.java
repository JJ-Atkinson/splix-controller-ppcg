package com.jatkin.splixkoth.ppcg.players;

import com.jatkin.splixkoth.ppcg.game.Direction;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.readonly.HiddenPlayer;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlySplixPoint;
import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.maps.Point2D;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Comparator;

/**
 * Trap bot goes to the wall and traces the entirety around. Hopes that
 * the players in the middle die and that nobody challenges him. Nearly 
 * all turns are left turns.
 */
public class HunterBot extends SplixPlayer {


    private Point2D lastTarget;
    
    private Direction lastMove = Direction.East;
    
    @Override
    protected Direction makeMove(ReadOnlyGame game, ReadOnlyBoard board) {
        Point2D thisPos = board.getPosition(this);
        MutableMap<Point2D, ReadOnlySplixPoint> global = board.getGlobal();
        MutableMap<Point2D, ReadOnlySplixPoint> targets = global.select((pt, rosp) ->
                !rosp.getClaimer().equals(getThisHidden()) 
                        && !rosp.getClaimer().equals(new HiddenPlayer(null)));
        
        if (targets.size() == 0 && lastTarget == null) {
            lastMove = lastMove.leftTurn();
            return lastMove;
        }

        Point2D target = null;
        if (targets.size() == 0) target = lastTarget;
        else target = targets.keysView().min(Comparator.comparingInt(t -> Utils.realMovementDist(thisPos, t)));
        if (target.equals(thisPos)) {
            lastTarget = null;
            // time to go home
            target = global.select((z_, x) -> getThisHidden().equals(x.getOwner())).keySet().iterator().next();
        }
        
        lastTarget = target;
        lastMove = makeSafeMove(target, global, board, thisPos);
        return lastMove;
    }
    
    private Direction makeSafeMove(Point2D targetLocation, MutableMap<Point2D, ReadOnlySplixPoint> map, ReadOnlyBoard board, Point2D currLoc) {
        Point2D dist = Utils.addPoints(Utils.multPoint(currLoc, -1), targetLocation);
        ImmutableSet<Direction> possibleMoves = Sets.immutable.of(Direction.values())
                .select(x -> {
                    Point2D pos = Utils.addPoints(x.vector, currLoc);
                    return !board.getBounds().outOfBounds(pos) && !getThisHidden().equals(map.get(pos).getClaimer());
                });
        Direction prefMove;
        if (Math.abs(dist.getX()) > Math.abs(dist.getY()))
            prefMove = getDirectionFroPoint(new Point2D(normalizeNum(dist.getX()), 0));
        else
            prefMove = getDirectionFroPoint(new Point2D(0, normalizeNum(dist.getY())));
        
        if (possibleMoves.contains(prefMove)) return prefMove;
        if (possibleMoves.contains(prefMove.leftTurn())) return prefMove.leftTurn();
        if (possibleMoves.contains(prefMove.rightTurn())) return prefMove.rightTurn();
        return prefMove.leftTurn().leftTurn();
    }

    private Direction getDirectionFroPoint(Point2D dir) {
        return Sets.immutable.of(Direction.values()).select(d -> d.vector.equals(dir)).getOnly();
    }

    private int normalizeNum(int n) { if (n < -1) return -1; if (n > 1) return 1; else return n;}
    
    
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    private int hc = (int)(Math.random()*Integer.MAX_VALUE);
    @Override
    public int hashCode() {return hc;}
}
