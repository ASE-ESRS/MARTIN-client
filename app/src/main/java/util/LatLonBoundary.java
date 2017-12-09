package util;

/**
 * Created by markp on 09/12/2017.
 */

public class LatLonBoundary {
    private double latFrom;
    private double latTo;
    private double lonFrom;
    private double lonTo;

     public double getLatFrom() {
        return latFrom;
    }

     public double getLatTo() {
        return latTo;
    }

     public double getLonFrom() {
        return lonFrom;
    }

     public double getLonTo() {
        return lonTo;
    }

     public void setLatFrom(double latFrom) {
        this.latFrom = latFrom;
    }

     public void setLatTo(double latTo) {
        this.latTo = latTo;
    }

     public void setLonFrom(double lonFrom) {
        this.lonFrom = lonFrom;
    }

     public void setLonTo(double lonTo) {
        this.lonTo = lonTo;
    }
}
