package de.unidue.inf.is.domain;

import java.sql.Timestamp;

public class Drive {
    int fid;
    String startort;
    String zielort;
    Timestamp fahrtDatumZeit;
    int maxPlaetze;
    int fahrtkosten;
    String status;
    int anbieter;
    int transportmittel;

    public Drive(int fid, String startort, String zielort, Timestamp fahrtDatumZeit, int maxPlaetze, int fahrtkosten,
                 String status, int anbieter, int transportmittel) {
        this.fid = fid;
        this.startort = startort;
        this.zielort = zielort;
        this.fahrtDatumZeit = fahrtDatumZeit;
        this.maxPlaetze = maxPlaetze;
        this.fahrtkosten = fahrtkosten;
        this.status = status;
        this.anbieter = anbieter;
        this.transportmittel = transportmittel;
    }

    public int getFid() {
        return fid;
    }

    public String getStartort() {
        return startort;
    }

    public String getZielort() {
        return zielort;
    }

    public void setFahrtDatumZeit(Timestamp fahrtDatumZeit) {
        this.fahrtDatumZeit = fahrtDatumZeit;
    }

    public int getMaxPlaetze() {
        return maxPlaetze;
    }

    public int getFahrtkosten() {
        return fahrtkosten;
    }

    public String getStatus() {
        return status;
    }

    public int getAnbieter() {
        return anbieter;
    }

    public int getTransportmittel() {
        return transportmittel;
    }
}
