package com.jatkin.splixkoth.ppcg;

import com.nmerrill.kothcomm.game.maps.Point2D;

/**
 * Created by Jarrett on 02/14/17.
 */
public class SplixSettings {
    public static final int gameIterationsCount = 300;
    public static final int pointsForKill = 300;
    
    // must be square for the ui to work correctly
    public static final Point2D viewingAreaSize = new Point2D(20, 20);
}
