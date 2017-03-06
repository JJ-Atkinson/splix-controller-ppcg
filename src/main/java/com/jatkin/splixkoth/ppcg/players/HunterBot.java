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
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;

import javax.rmi.CORBA.Util;
import java.util.Comparator;

/**
 * Trap bot goes to the wall and traces the entirety around. Hopes that
 * the players in the middle die and that nobody challenges him. Nearly 
 * all turns are left turns.
 */
public class HunterBot extends SplixPlayer {


    @Override
    protected Direction makeMove(ReadOnlyGame game, ReadOnlyBoard board) {
        MutableMap<Point2D, ReadOnlySplixPoint> global = board.getGlobal();
        MutableMap<Point2D, ReadOnlySplixPoint> targets = global.select((pt, rosp) -> 
                rosp.getClaimer() != getThisHidden() && rosp.getOwner() != new HiddenPlayer(null));
        if (targets.size() == 0)
            return Direction.values()[getRandom().nextInt(4)];// could hit itself, need to fix

        Point2D thisPos = board.getPosition(this);
        Point2D target = targets.keysView().min(Comparator.comparingInt(t -> Utils.realMovementDist(thisPos, t)));
        Point2D dist = Utils.addPoints(thisPos, Utils.multPoint(target, -1));
        
        if (Math.abs(dist.getX()) > Math.abs(dist.getY()));

        return null;
    }
    
    private int normalizeNum(int n) { if (n < -1) return -1; else if (n > 1) return 1; else return n;}

    


    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    private int hc = (int)(Math.random()*Integer.MAX_VALUE);
    @Override
    public int hashCode() {return hc;}
}
