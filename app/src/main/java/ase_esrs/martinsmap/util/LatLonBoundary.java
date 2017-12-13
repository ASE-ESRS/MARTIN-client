package ase_esrs.martinsmap.util;

/**
 * This class computes and stores the coordinates corresponding to a boundary when given a lat,lon pair and radius.
 * @author Mark and Loic
 */
public class LatLonBoundary {
    private double latFrom;
    private double latTo;
    private double lonFrom;
    private double lonTo;

    /**
     * Calculates the boundary coordinates for an area within the given radius of the provided location.
     * @param latitude The lat coordinate of the centre point around which to calculate the boundary.
     * @param longitude The lon coordinate of the centre point around which to calculate the boundary.
     * @param radius The distance around the lat and lon points for which to define the boundary.
     * @author Mark and Loic
     */
    public LatLonBoundary(double latitude, double longitude, int radius) {
        double latChange = Math.abs(radius*(1/(110.574*1000)));
        double lonChange = Math.abs(radius*(1/(111.320*1000)));

        this.latFrom = latitude - latChange;
        this.lonFrom = longitude - lonChange;

        this.latTo = latitude + latChange;
        this.lonTo = longitude + lonChange;
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
        if (checkLatitude(latFrom)) {
            this.latFrom = latFrom;
        }
    }

     public void setLatTo(double latTo) {
         if (checkLatitude(latTo)) {
             this.latTo = latTo;
         }
    }

     public void setLonFrom(double lonFrom) {
         if (checkLongitude(lonFrom)) {
             this.lonFrom = lonFrom;
         }
    }

     public void setLonTo(double lonTo) {
         if (checkLongitude(lonTo)) {
             this.lonTo = lonTo;
         }
    }

    /**
     * Checks that a given latitude is in range -90 to +90
     * @param lat A latitude to check is in range
     * @return Whether or not the given latitude is valid
     * @author Mark
     */
    public boolean checkLatitude(double lat) {
        return (lat <= Math.abs(90.0));
    }

    /**
     * Checks that a given longitude is in range -180 to +180
     * @param lon A longitude to check is in range
     * @return Whether or not the given longitude is valid
     * @author Mark
     */
    public boolean checkLongitude(double lon) {
        return (lon <= Math.abs(180.0));
    }
}
