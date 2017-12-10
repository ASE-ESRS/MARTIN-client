package util;

public class LatLonBoundary {
    private double latFrom;
    private double latTo;
    private double lonFrom;
    private double lonTo;

    public LatLonBoundary(double latitude, double longitude, int radius) {
        double latChange = Math.abs(radius*(1/(110.574*1000)));
        double lonChange = Math.abs(radius*(1/(111.320*1000)));

        this.latFrom = latitude - latChange;
        this.lonFrom = longitude - lonChange;

        this.latTo = latitude + latChange;
        this.lonFrom = longitude + lonChange;
    }

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
