package com.jatkin.splixkoth.ppcg.game;

import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.AdjacencyGraphMap;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.Bounds;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.maps.graphmaps.neighborhoods.VonNeumannNeighborhood;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

/**
 * Created by Jarrett on 02/01/17.
 */
public class SplixBoard extends AdjacencyGraphMap<Point2D, SplixPoint> {

    public SplixBoard(Bounds<Point2D> bounds) {
        super(bounds, new VonNeumannNeighborhood());
    }

    private void fillMapWithDefault() {
        for (int x = 0; true; x++) {
            for (int y = 0; true; y++) {
                Point2D point = new Point2D(x, y);
                if (outOfBounds(point))
                   break;

               put(point, new SplixPoint());
            }
            if (outOfBounds(new Point2D(x, 0)))
                break;
        }
    }


    public MutableMap<Point2D, SplixPoint> getSubset(SquareBounds bounds) {
        MutableMap<Point2D, SplixPoint> ret = Maps.mutable.empty();
        for (int x = bounds.getLeft(); x <= bounds.getRight(); x++) {
            for (int y = bounds.getTop(); y <= bounds.getBottom(); y++) {
                Point2D point = new Point2D(x, y);
                ret.put(point, get(point));
            }
        }

        return ret;
    }

    protected MutableSet<Submission<SplixPlayer>> getDeathsFromMoves(MutableMap<Submission<?>, Direction> playerMoves) {
        MutableSet<Submission<SplixPlayer>> ret = Sets.mutable.empty();

        // awsome logic
        return ret;
    }

    private MutableMap<Submission<SplixPlayer>, Point2D> getPlayerPositions() {
        // todo
    }
}
