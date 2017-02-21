package com.jatkin.splixkoth.ppcg.game;

import com.nmerrill.kothcomm.game.maps.Point2D;

/**
 * Created by Jarrett on 02/02/17.
 */
public enum Direction {
    East(new Point2D(1, 0)),
    North(new Point2D(0, 1)),
    West(new Point2D(-1, 0)),
    South(new Point2D(0, -1));


    public final Point2D vector;

    Direction(Point2D vector) {
        this.vector = vector;
    }
    
    public Direction leftTurn() {return values()[(this.ordinal()+1)%4];}
    public Direction rightTurn() {return values()[(this.ordinal()+3)%4];}
}
