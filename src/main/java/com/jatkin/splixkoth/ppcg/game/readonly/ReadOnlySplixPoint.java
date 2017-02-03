package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixPoint;

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
