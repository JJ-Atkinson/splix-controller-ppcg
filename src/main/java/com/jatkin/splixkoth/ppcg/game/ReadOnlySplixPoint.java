package com.jatkin.splixkoth.ppcg.game;

/**
 * Created by Jarrett on 02/02/17.
 */
public class ReadOnlySplixPoint {
    private final SplixPoint backing;

    public ReadOnlySplixPoint(SplixPoint backing) {
        this.backing = backing;
    }

    public boolean isTrail() {return backing.isTrail();}
    public boolean isPlayerLocatedOnPoint() {return backing.isPlayerLocatedOnPoint();}
}
