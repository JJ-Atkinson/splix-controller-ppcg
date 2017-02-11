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

    public SplixPoint(Submission<SplixPlayer> typeOfOwner, Submission<SplixPlayer> typeOfClaimer) {
        this.typeOfOwner = typeOfOwner;
        this.typeOfClaimer = typeOfClaimer;
    }

    public SplixPoint() {this (null, null);}

    public Submission<SplixPlayer> getTypeOfOwner() {
        return typeOfOwner;
    }

    public void setTypeOfOwner(Submission<SplixPlayer> typeOfOwner) {
        this.typeOfOwner = typeOfOwner;
    }

    public Submission<SplixPlayer> getTypeOfClaimer() { return typeOfClaimer; }

    public void setTypeOfClaimer(Submission<SplixPlayer> typeOfClaimer) { this.typeOfClaimer = typeOfClaimer; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SplixPoint that = (SplixPoint) o;

        if (typeOfOwner != null ? !typeOfOwner.equals(that.typeOfOwner) : that.typeOfOwner != null) return false;
        return typeOfClaimer != null ? typeOfClaimer.equals(that.typeOfClaimer) : that.typeOfClaimer == null;
    }

    @Override
    public int hashCode() {
        int result = typeOfOwner != null ? typeOfOwner.hashCode() : 0;
        result = 31 * result + (typeOfClaimer != null ? typeOfClaimer.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SPoint: O=" + typeOfOwner + "; C=" + getTypeOfClaimer();
    }
}
