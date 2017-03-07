package com.jatkin.splixkoth.ppcg.players;

import com.jatkin.splixkoth.ppcg.game.Direction;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.readonly.HiddenPlayer;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlySplixPoint;
import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareRegion;
import javafx.util.Pair;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

import javax.rmi.CORBA.Util;
import java.util.Comparator;

/**
 * Trap bot goes to the wall and traces the entirety around. Hopes that
 * the players in the middle die and that nobody challenges him. Nearly 
 * all turns are left turns.
 */
public class HunterBot extends SplixPlayer {


    private Point2D lastTarget;
    
    @Override
    protected Direction makeMove(ReadOnlyGame game, ReadOnlyBoard board) {
        Point2D thisPos = board.getPosition(this);
        MutableMap<Point2D, ReadOnlySplixPoint> global = board.getGlobal();
        MutableMap<Point2D, ReadOnlySplixPoint> targets = global.select((pt, rosp) ->
                !rosp.getClaimer().equals(getThisHidden()) 
                        && !rosp.getClaimer().equals(new HiddenPlayer(null)));
        
        if (targets.size() == 0 && lastTarget == null) {
            ImmutableList<Direction> possibleMoves = Lists.immutable.of(Direction.values())
                    .select(x -> {
                        Point2D pos = Utils.addPoints(x.vector, thisPos);
                        return !global.get(pos).getClaimer().equals(getThisHidden()) && !board.getBounds().outOfBounds(pos);
                    });
            return possibleMoves.size() != 0 ? possibleMoves.get(0) : Direction.East;
        }

        Point2D target = null;
        if (targets.size() == 0) target = lastTarget;
        else targets.keysView().min(Comparator.comparingInt(t -> Utils.realMovementDist(thisPos, t)));
        
        Point2D dist = Utils.addPoints(Utils.multPoint(thisPos, -1), target);
        
        lastTarget = target;
        if (Math.abs(dist.getX()) > Math.abs(dist.getY()))
            return getDirectionFroPoint(new Point2D(normalizeNum(dist.getX()), 0));
        else
            return getDirectionFroPoint(new Point2D(0, normalizeNum(dist.getY())));
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
