package com.jatkin.splixkoth.ppcg.util;

import com.nmerrill.kothcomm.game.maps.Point2D;

import static java.lang.Math.abs;

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
}
