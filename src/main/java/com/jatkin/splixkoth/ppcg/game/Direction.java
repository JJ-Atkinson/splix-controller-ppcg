package com.jatkin.splixkoth.ppcg.game;

import com.nmerrill.kothcomm.game.maps.Point2D;

/**
 * Created by Jarrett on 02/02/17.
 */
public enum Direction {
    North(new Point2D(0, 1)),
    South(new Point2D(0, -1)),
    East(new Point2D(1, 0)),
    West(new Point2D(-1, 0));


    public final Point2D vector;

    Direction(Point2D vector) {
        this.vector = vector;
    }
}
