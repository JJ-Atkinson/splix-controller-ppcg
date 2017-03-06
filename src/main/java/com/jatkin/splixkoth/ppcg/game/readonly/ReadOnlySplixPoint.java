package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.nmerrill.kothcomm.game.players.Submission;

/**
 * Created by Jarrett on 02/02/17.
 */
public class ReadOnlySplixPoint {
    private SplixPoint backing;
    private SplixPlayer whosOnSpot;

    public ReadOnlySplixPoint(SplixPoint backing, SplixPlayer owner) {
        this.backing = backing;
        this.whosOnSpot = owner;
    }

    public HiddenPlayer getOwner() {
        return new HiddenPlayer(backing.getOwner());
    }

    public HiddenPlayer getClaimer() {
        return new HiddenPlayer(backing.getClaimer());
    }

    /**
     * Returns which, if any, player's dot is on the spot.  
     * @return
     */
    public HiddenPlayer getWhosOnSpot() { return new HiddenPlayer(whosOnSpot); }
}
