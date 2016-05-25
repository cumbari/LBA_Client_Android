package com.moblyo.market.location;

/**
 * Created by Komal on 28/04/16.
 */
public class Coordinate {

    public enum Unit {
        /** The Kilometer. */
        Kilometer(1),
        /** The Meter. */
        Meter(2),
        /** The Miles. */
        Miles(3);

        /** The name. */
        private final int name;

        /**
         * Instantiates a new unit.
         * @param name
         *            the name
         */
        private Unit(int name) {
            this.name = name;
        }

        /**
         * Gets the value.
         * @return the value
         */
        public int getValue() {
            return name;
        }

    };

// HAVERSINE’S FORMULA
    /**
     * Distance.
     *
     * @param lat1
     *            the lat1
     * @param lon1
     *            the lon1
     * @param lat2
     *            the lat2
     * @param lon2
     *            the lon2
     * @param MeasurementUnit
     *            the measurement unit
     * @return the double
     */
    public double distance(double lat1, double lon1, double lat2, double lon2,
                           Unit MeasurementUnit) {
        int RadiusOfEarth = 6371; // KM
        double dLat = deg2rad(lat2 - lat1);
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = RadiusOfEarth * c;

        if (MeasurementUnit == Unit.Meter) {
            // 1 kilometer = 1000 meters
            dist = dist * 1000;
        } else if (MeasurementUnit == Unit.Miles) {
            // 1 kilometer = 0.621371192 miles
            dist = dist * 0.621371192;
        }

        if (Double.isNaN(dist)) {
            dist = 0.0;
        }

        return (dist);
    }

    /**
     * Deg2rad.
     * @param deg
     *            the deg
     * @return the double
     */
    private double deg2rad(double deg) {

        return (deg * Math.PI / 180.0);

    }

}
