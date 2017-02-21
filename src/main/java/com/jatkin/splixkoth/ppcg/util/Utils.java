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
    
    public static Point2D multPoint(Point2D p, int scale) {
        return new Point2D(p.getX()*scale, p.getY()*scale);
    }
}
