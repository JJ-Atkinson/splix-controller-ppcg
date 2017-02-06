package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.nmerrill.kothcomm.game.players.Submission;

/**
 * Created by Jarrett on 02/02/17.
 */
public class ReadOnlySplixPoint {
    private SplixPoint backing;

    public ReadOnlySplixPoint(SplixPoint backing) {
        this.backing = backing;
    }

    public Submission<SplixPlayer> getTypeOfOwner() {
        return backing.getTypeOfOwner();
    }

    public Submission<SplixPlayer> getTypeOfClaimer() {
        return backing.getTypeOfClaimer();
    }

    /**
     * Nuke a splix point so they arn't reused.
     */
    public void destroy() { backing = null; }
}
