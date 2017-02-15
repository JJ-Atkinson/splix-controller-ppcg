package com.jatkin.splixkoth.ppcg.util;

import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import java.util.Map;


/**
 * Created by Jarrett on 02/02/17.
 */
public class Utils {
    public static Point2D addPoints(Point2D p1, Point2D p2) {
        return new Point2D(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }

    public static int realMovementDist(Point2D p1, Point2D p2) {
        return Math.abs(p1.getX() - p2.getX()) +  Math.abs(p1.getY() - p2.getY());
    }

    /**
     * Fuzz out a board to a set of points and booleans. Each boolean is true only if 
     * the minimum density is satisfied in a block. 
     * @param original
     * @param blockSize
     * @param minDensity
     * @return
     */
    public static Map<Point2D, Boolean> fuzzOutBoard(SplixBoard original, int blockSize, double minDensity) {
        MutableMap<Point2D, Boolean> ret = Maps.mutable.empty();
        original.locations().forEach(point -> ret.put(point, Boolean.FALSE));
        
        for (int y = 0; y < original.getBounds().getTop(); y+=blockSize) {
            for (int x = 0; x < original.getBounds().getRight(); x+=blockSize) {
                int actualBlockSizeX = Math.min(blockSize, original.getBounds().getBottom() - x);
                int actualBlockSizeY = Math.min(blockSize, original.getBounds().getTop() - y);
                SquareBounds boundsBeingChecked = new SquareBounds(
                        new Point2D(x, y), new Point2D(actualBlockSizeX, actualBlockSizeY));
                MutableMap<Point2D, SplixPoint> blockData = 
                        original.getSubset(boundsBeingChecked);
                double numberOfOccupiedBlocks = 
                        blockData.count(point -> point.getTypeOfOwner() != null);
                
                if (numberOfOccupiedBlocks/blockData.size() > minDensity) {
                    for (int ty = boundsBeingChecked.getBottom(); ty < original.getBounds().getTop(); ty++) {
                        for (int tx = boundsBeingChecked.getLeft(); tx < original.getBounds().getRight(); tx++) {
                            ret.put(new Point2D(x, y), Boolean.TRUE);
                    }}
                }
            }
        }
    }
}
