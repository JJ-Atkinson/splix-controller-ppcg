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

    private Submission<SplixPlayer> typeOfClaimer;

    public Submission<SplixPlayer> getTypeOfOwner() {
        return typeOfOwner;
    }

    public void setTypeOfOwner(Submission<SplixPlayer> typeOfOwner) {
        this.typeOfOwner = typeOfOwner;
    }

    public Submission<SplixPlayer> getTypeOfClaimer() { return typeOfClaimer; }

    public void setTypeOfClaimer(Submission<SplixPlayer> typeOfClaimer) { this.typeOfClaimer = typeOfClaimer; }
}
