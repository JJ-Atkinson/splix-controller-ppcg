package com.jatkin.splixkoth.ppcg.game;

/**
 * Created by Jarrett on 02/02/17.
 */
public class SplixPoint {
    /**
     * Who owns the point. May be null if nobody owns it.
     */
    private SplixPlayer typeOfOwner;

    private SplixPlayer typeOfClaimer;

    public SplixPoint(SplixPlayer typeOfOwner, SplixPlayer typeOfClaimer) {
        this.typeOfOwner = typeOfOwner;
        this.typeOfClaimer = typeOfClaimer;
    }

    public SplixPoint() {this (null, null);}

    public SplixPlayer getOwner() {
        return typeOfOwner;
    }

    public void setOwner(SplixPlayer typeOfOwner) {
        this.typeOfOwner = typeOfOwner;
    }

    public SplixPlayer getClaimer() { return typeOfClaimer; }

    public void setClaimer(SplixPlayer typeOfClaimer) { this.typeOfClaimer = typeOfClaimer; }

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
        return "SPoint: O=" + typeOfOwner + "; C=" + getClaimer();
    }
}
