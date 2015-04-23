package it.unipr.ce.dsg.examples.centralizednetworkfm.utils;

import com.google.gson.JsonObject;

/**
 *
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class Location {

        private Double latitude;
        private Double longitude;
        private static final Double EARTH_RADIUS = 6370.9860; //in Km

        public Location(Double latitude, Double longitude) {
                this.setLatitude(latitude);
                this.setLongitude(longitude);
        }

        public Location(Location location) {
                this.latitude = location.getLatitude();
                this.longitude = location.getLongitude();
        }

        public Double getLongitude() {
                return longitude;
        }

        public void setLongitude(Double longitude) {
                this.longitude = longitude;
        }

        public Double getLatitude() {
                return latitude;
        }

        public void setLatitude(Double latitude) {
                this.latitude = latitude;
        }

        @Override
        public String toString() {
                return latitude + ", " + longitude;
        }
       
        /* (non Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result
                                + ((latitude == null) ? 0 : latitude.hashCode());
                result = prime * result
                                + ((longitude == null) ? 0 : longitude.hashCode());
                return result;
        }

        /* (non Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (obj == null)
                        return false;
                if (getClass() != obj.getClass())
                        return false;
                Location other = (Location) obj;
                if (latitude == null) {
                        if (other.latitude != null)
                                return false;
                } else if (!latitude.equals(other.latitude))
                        return false;
                if (longitude == null) {
                        if (other.longitude != null)
                                return false;
                } else if (!longitude.equals(other.longitude))
                        return false;
                return true;
        }

        /**
         * @param location
         * - the location with which to compare.
         * @return
         * the great-circle distance in Km.
         */
        public Double distanceFrom(Location location) {
                Double deltaLatitude = latitude - location.getLatitude();
                Double deltaLongitude = longitude - location.getLongitude();
               
                Double a = Math.sin(Math.toRadians(deltaLatitude)/2) * Math.sin(Math.toRadians(deltaLatitude)/2) +
                                Math.sin(Math.toRadians(deltaLongitude)/2) * Math.sin(Math.toRadians(deltaLongitude)/2) * Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(location.getLatitude()));
               
                return 2 * EARTH_RADIUS * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        }
       
        /**
         * Returns initial bearing (sometimes referred to as forward azimuth) which if followed in a straight line along a great-circle arc will take you from this {@link Location} to the specified {@link Location}.
         * @param location
         * - end point of the straight line along a great-circle arc
         * @return
         * initial bearing in degrees from this {@link Location} to the specified {@link Location}
         */
        public Double initialBearingTo(Location location) {
        Double y = Math.sin(Math.toRadians(location.longitude - this.longitude)) * Math.cos(Math.toRadians(location.latitude));
        Double x = Math.cos(Math.toRadians(this.latitude))*Math.sin(Math.toRadians(location.latitude)) - Math.sin(Math.toRadians(this.latitude))*Math.cos(Math.toRadians(location.latitude))*Math.cos(Math.toRadians(location.longitude - this.longitude));
        return (Math.toDegrees(Math.atan2(y, x))+360.0)%360.0;
        }
       
        /**
         * Returns final bearing which if followed in a straight line along a great-circle arc will take you from this {@link Location} to the specified {@link Location}.
         * @param location
         * - end point of the straight line along a great-circle arc
         * @return
         * final bearing in degrees from this {@link Location} to the specified {@link Location}
         */
        public Double finalBearingTo(Location location) {
                return (location.initialBearingTo(this) + 180.0)%360.0;
        }
       
        /**
         * Returns the destination location starting from this {@link Location}, with the specified initial bearing and travelling along a great circle arc the specified distance.
         * @param initialBearing
         * - initial bearing in degrees
         * @param distance
         * - distance in kilometres
         * @return
         * the destination location starting from this {@link Location}, with the specified initial bearing and travelling along a great circle arc the specified distance
         */
        public Location arrivesTo(Double initialBearing, Double distance) {
                Double latitude = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(this.latitude))*Math.cos(distance/EARTH_RADIUS) + Math.cos(Math.toRadians(this.latitude))*Math.sin(distance/EARTH_RADIUS)*Math.cos(Math.toRadians(initialBearing))));

                Double longitude = this.longitude + Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(initialBearing)) * Math.sin(distance/EARTH_RADIUS) * Math.cos(Math.toRadians(this.latitude)), Math.cos(distance/EARTH_RADIUS) - Math.sin(Math.toRadians(this.latitude)) * Math.sin(Math.toRadians(latitude))));
                               
                return new Location(latitude, longitude);
        }
       
        /**
         * Indicates whether some other location is "equal to" this one.
         * @param location
         * - the reference location with which to compare.
         * @return
         * true if this location is the same as the location argument; false otherwise.
         */
        public boolean equals(Location location) {
                return this.latitude.equals(location.getLatitude()) && this.longitude.equals(location.getLongitude());
        }

        /**
         *
         * @return
         * A JsonObject that represents the Location.
         */
        public JsonObject toJsonObject() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("latitude", latitude);
                jsonObject.addProperty("longitude", longitude);
                return jsonObject;
        }
}
