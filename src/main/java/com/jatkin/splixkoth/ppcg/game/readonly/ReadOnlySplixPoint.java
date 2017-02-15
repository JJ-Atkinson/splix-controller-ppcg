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

    public Submission<SplixPlayer> getTypeOfOwner() {
        return backing.getTypeOfOwner();
    }

    public Submission<SplixPlayer> getTypeOfClaimer() {
        return backing.getTypeOfClaimer();
    }

    /**
     * Returns which, if any, player's dot is on the spot.  
     * @return
     */
    public Submission<SplixPlayer> getWhosOnSpot() { return whosOnSpot; }

    /**
     * Nuke a splix point so they arn't reused.
     */
    public void destroy() { backing = null; }
}
