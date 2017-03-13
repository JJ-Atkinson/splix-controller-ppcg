package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareRegion;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import java.util.Map;

/**
 * Created by Jarrett on 02/02/17.
 */
public class ReadOnlyBoard {
    private SplixBoard backing;
    public final SquareRegion viewingArea;

    public ReadOnlyBoard(SplixBoard backing, SquareRegion viewingArea) {
        this.backing = backing;
        this.viewingArea = viewingArea;
    }

    /**
     * A subset of getGlobal that shows where players are.
     * @return
     */
    public MutableMap<Point2D, ReadOnlySplixPoint> getView() {
        MutableMap<SplixPlayer, Point2D> playerPositions = backing.getPlayerPositions();
        MutableMap<Point2D, ReadOnlySplixPoint> ret = Maps.mutable.empty();

        MutableMap<Point2D, SplixPoint> subset = backing.getSubset(viewingArea);
        for (Map.Entry<Point2D, SplixPoint> ent : subset.entrySet()) {
            Point2D pos = ent.getKey();
            SplixPoint point = ent.getValue();
            MutableMap<SplixPlayer, Point2D> playersWithPos =
                  playerPositions.select((player, p) -> p.equals(pos));
            SplixPlayer player = playersWithPos.size() == 1 ? playersWithPos.keysView().getOnly() : null;
            ret.put(pos, new ReadOnlySplixPoint(point, player));
        }

        return ret;
    }

    /**
     * @return the whole board with the modification that players are not shown.
     */
    public MutableMap<Point2D, ReadOnlySplixPoint> getGlobal() {
        MutableMap<Point2D, ReadOnlySplixPoint> ret = Maps.mutable.empty();
        backing.locations().forEach(p -> ret.put(p, new ReadOnlySplixPoint(backing.get(p), null)));
        return ret;
    }
    
    public SquareRegion getBounds() {return backing.getBounds();}
    
    public Point2D getPosition(SplixPlayer me) {
        return backing.getPlayerPositions().get(me);
    }
}
