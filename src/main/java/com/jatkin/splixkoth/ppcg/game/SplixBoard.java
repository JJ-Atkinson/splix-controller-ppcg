package com.jatkin.splixkoth.ppcg.game;

import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.AdjacencyGraphMap;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.Bounds;
import com.nmerrill.kothcomm.game.maps.graphmaps.neighborhoods.VonNeumannNeighborhood;

/**
 * Created by Jarrett on 02/01/17.
 */
public class SplixBoard extends AdjacencyGraphMap<Point2D, SplixPoint> {

    public SplixBoard(Bounds<Point2D> bounds) {
        super(bounds, new VonNeumannNeighborhood());
    }

    private void fillMapWithDefault() {
        for (int x = 0; true; i++) {
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

//    public MutableMap<Point2D, SplixPoint> getSubset(SquareBounds bounds) {}
}
