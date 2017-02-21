package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.nmerrill.kothcomm.game.players.Submission;

/**
 * Created by Jarrett on 02/02/17.
 */
public class ReadOnlySplixPoint {
    private SplixPoint backing;
    private Submission<SplixPlayer> whosOnSpot;

    public ReadOnlySplixPoint(SplixPoint backing, Submission<SplixPlayer> owner) {
        this.backing = backing;
        this.whosOnSpot = owner;
    }

    public HiddenPlayer getTypeOfOwner() {
        return new HiddenPlayer(backing.getTypeOfOwner());
    }

    public HiddenPlayer getTypeOfClaimer() {
        return new HiddenPlayer(backing.getTypeOfClaimer());
    }

    /**
     * Returns which, if any, player's dot is on the spot.  
     * @return
     */
    public HiddenPlayer getWhosOnSpot() { return new HiddenPlayer(whosOnSpot); }
}
