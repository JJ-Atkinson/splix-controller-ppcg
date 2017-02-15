package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;

import java.util.Map;

/**
 * Created by Jarrett on 02/02/17.
 */
public class ReadOnlyBoard {
    private SplixBoard backing;
    private SquareBounds viewingArea;

    public ReadOnlyBoard(SplixBoard backing, SquareBounds viewingArea) {
        this.backing = backing;
        this.viewingArea = viewingArea;
    }
    
    public MutableMap<Point2D, ReadOnlySplixPoint> getView() {
        MutableMap<Submission<SplixPlayer>, Point2D> playerPositions = backing.getPlayerPositions();
        MutableMap<Point2D, ReadOnlySplixPoint> ret = Maps.mutable.empty();

        MutableMap<Point2D, SplixPoint> subset = backing.getSubset(viewingArea);
        for (Map.Entry<Point2D, SplixPoint> ent : subset.entrySet()) {
            Point2D pos = ent.getKey();
            SplixPoint point = ent.getValue();
            MutableMap<Submission<SplixPlayer>, Point2D> playersWithPos =
                  playerPositions.select((player, p) -> p.equals(pos));
            Submission<SplixPlayer> player = playersWithPos.size() == 1 ? playersWithPos.keysView().getOnly() : null;
            ret.put(pos, new ReadOnlySplixPoint(point, player));
        }

        return ret;
    }
    
    public void destroy() {}
}
