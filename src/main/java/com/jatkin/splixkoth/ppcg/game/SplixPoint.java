package com.jatkin.splixkoth.ppcg.game;

import com.nmerrill.kothcomm.game.maps.MapPoint;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.players.Submission;

/**
 * Created by Jarrett on 02/02/17.
 */
public class SplixPoint {
    /**
     * Who owns the point. May be null if nobody owns it.
     */
    private Submission<SplixPlayer> typeOfOwner;

    /**
     * Is this the trail of the player.
     */
    private boolean isTrail;

    public boolean isTrail() {
        return isTrail;
    }

    void setTrail(boolean trail) {
        isTrail = trail;
    }

    public Submission<SplixPlayer> getTypeOfOwner() {
        return typeOfOwner;
    }

    public void setTypeOfOwner(Submission<SplixPlayer> typeOfOwner) {
        this.typeOfOwner = typeOfOwner;
    }
}
