/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.Logging;

import java.awt.geom.*;
import java.util.*;

/**
 * <code>Sector</code> represents a rectangular region of latitude and longitude. The region is defined by four angles:
 * its minimum and maximum latitude, its minimum and maximum longitude. The angles are assumed to be normalized to +/-
 * 90 degrees latitude and +/- 180 degrees longitude. The minimums and maximums are relative to these ranges, e.g., -80
 * is less than 20. Behavior of the class is undefined for angles outside these ranges. Normalization is not performed
 * on the angles by this class, nor is it verified by the class' methods. See {@link Angle} for a description of
 * specifying angles. <p/> <code>Sector</code> instances are immutable. </p>
 *
 * @author Tom Gaskins
 * @version $Id: Sector.java 1171 2013-02-11 21:45:02Z dcollins $
 * @see Angle
 */
public class Sector implements  Comparable<Sector>, Iterable<LatLon>
{
    /** A <code>Sector</code> of latitude [-90 degrees, + 90 degrees] and longitude [-180 degrees, + 180 degrees]. */
    public static final Sector FULL_SPHERE = new Sector(Angle.NEG90, Angle.POS90, Angle.NEG180, Angle.POS180);
    public static final Sector EMPTY_SECTOR = new Sector(Angle.ZERO, Angle.ZERO, Angle.ZERO, Angle.ZERO);

    private final Angle minLatitude;
    private final Angle maxLatitude;
    private final Angle minLongitude;
    private final Angle maxLongitude;
    private final Angle deltaLat;
    private final Angle deltaLon;

    /**
     * Creates a new <code>Sector</code> and initializes it to the specified angles. The angles are assumed to be
     * normalized to +/- 90 degrees latitude and +/- 180 degrees longitude, but this method does not verify that.
     *
     * @param minLatitude  the sector's minimum latitude in degrees.
     * @param maxLatitude  the sector's maximum latitude in degrees.
     * @param minLongitude the sector's minimum longitude in degrees.
     * @param maxLongitude the sector's maximum longitude in degrees.
     *
     * @return the new <code>Sector</code>
     */
    public static Sector fromDegrees(double minLatitude, double maxLatitude, double minLongitude,
        double maxLongitude)
    {
        return new Sector(Angle.fromDegrees(minLatitude), Angle.fromDegrees(maxLatitude), Angle.fromDegrees(
            minLongitude), Angle.fromDegrees(maxLongitude));
    }

    /**
     * Creates a new <code>Sector</code> and initializes it to angles in the specified array. The array is assumed to
     * hold four elements containing the Sector's angles, and must be ordered as follows: minimum latitude, maximum
     * latitude, minimum longitude, and maximum longitude. Additionally, the angles are assumed to be normalized to +/-
     * 90 degrees latitude and +/- 180 degrees longitude, but this method does not verify that.
     *
     * @param array the array of angles in degrees.
     *
     * @return he new <code>Sector</code>
     *
     * @throws IllegalArgumentException if <code>array</code> is null or if its length is less than 4.
     */
    public static Sector fromDegrees(double[] array)
    {
        if (array == null)
        {
            String message = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (array.length < 4)
        {
            String message = Logging.getMessage("generic.ArrayInvalidLength", array.length);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return fromDegrees(array[0], array[1], array[2], array[3]);
    }

    /**
     * Creates a new <code>Sector</code> and initializes it to the angles resulting from the given {@link
     * java.awt.geom.Rectangle2D} in degrees lat-lon coordinates where x corresponds to longitude and y to latitude. The
     * resulting geographic angles are assumed to be normalized to +/- 90 degrees latitude and +/- 180 degrees
     * longitude, but this method does not verify that.
     *
     * @param rectangle the sector's rectangle in degrees lat-lon coordinates.
     *
     * @return the new <code>Sector</code>
     */
    public static Sector fromDegrees(java.awt.geom.Rectangle2D rectangle)
    {
        return fromDegrees(rectangle.getY(), rectangle.getMaxY(), rectangle.getX(), rectangle.getMaxX());
    }

    /**
     * Creates a new <code>Sector</code> and initializes it to the specified angles. The angles are assumed to be
     * normalized to +/- \u03c0/2 radians latitude and +/- \u03c0 radians longitude, but this method does not verify
     * that.
     *
     * @param minLatitude  the sector's minimum latitude in radians.
     * @param maxLatitude  the sector's maximum latitude in radians.
     * @param minLongitude the sector's minimum longitude in radians.
     * @param maxLongitude the sector's maximum longitude in radians.
     *
     * @return the new <code>Sector</code>
     */
    public static Sector fromRadians(double minLatitude, double maxLatitude, double minLongitude,
        double maxLongitude)
    {
        return new Sector(Angle.fromRadians(minLatitude), Angle.fromRadians(maxLatitude), Angle.fromRadians(
            minLongitude), Angle.fromRadians(maxLongitude));
    }

    public static Sector boundingSector(Iterable<? extends LatLon> locations)
    {
        if (locations == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!locations.iterator().hasNext())
            return EMPTY_SECTOR; // TODO: should be returning null

        double minLat = Angle.POS90.getDegrees();
        double minLon = Angle.POS180.getDegrees();
        double maxLat = Angle.NEG90.getDegrees();
        double maxLon = Angle.NEG180.getDegrees();

        for (LatLon p : locations)
        {
            double lat = p.getLatitude().getDegrees();
            if (lat < minLat)
                minLat = lat;
            if (lat > maxLat)
                maxLat = lat;

            double lon = p.getLongitude().getDegrees();
            if (lon < minLon)
                minLon = lon;
            if (lon > maxLon)
                maxLon = lon;
        }

        return Sector.fromDegrees(minLat, maxLat, minLon, maxLon);
    }

    public static Sector[] splitBoundingSectors(Iterable<? extends LatLon> locations)
    {
        if (locations == null)
        {
            String message = Logging.getMessage("nullValue.LocationInListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!locations.iterator().hasNext())
            return null;

        double minLat = Angle.POS90.getDegrees();
        double minLon = Angle.POS180.getDegrees();
        double maxLat = Angle.NEG90.getDegrees();
        double maxLon = Angle.NEG180.getDegrees();

        LatLon lastLocation = null;

        for (LatLon ll : locations)
        {
            double lat = ll.getLatitude().getDegrees();
            if (lat < minLat)
                minLat = lat;
            if (lat > maxLat)
                maxLat = lat;

            double lon = ll.getLongitude().getDegrees();
            if (lon >= 0 && lon < minLon)
                minLon = lon;
            if (lon <= 0 && lon > maxLon)
                maxLon = lon;

            if (lastLocation != null)
            {
                double lastLon = lastLocation.getLongitude().getDegrees();
                if (Math.signum(lon) != Math.signum(lastLon))
                {
                    if (Math.abs(lon - lastLon) < 180)
                    {
                        // Crossing the zero longitude line too
                        maxLon = 0;
                        minLon = 0;
                    }
                }
            }
            lastLocation = ll;
        }

        if (minLat == maxLat && minLon == maxLon)
            return null;

        return new Sector[]
            {
                Sector.fromDegrees(minLat, maxLat, minLon, 180), // Sector on eastern hemisphere.
                Sector.fromDegrees(minLat, maxLat, -180, maxLon) // Sector on western hemisphere.
            };
    }

    public static Sector boundingSector(LatLon pA, LatLon pB)
    {
        if (pA == null || pB == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double minLat = pA.getLatitude().degrees;
        double minLon = pA.getLongitude().degrees;
        double maxLat = pA.getLatitude().degrees;
        double maxLon = pA.getLongitude().degrees;

        if (pB.getLatitude().degrees < minLat)
            minLat = pB.getLatitude().degrees;
        else if (pB.getLatitude().degrees > maxLat)
            maxLat = pB.getLatitude().degrees;

        if (pB.getLongitude().degrees < minLon)
            minLon = pB.getLongitude().degrees;
        else if (pB.getLongitude().degrees > maxLon)
            maxLon = pB.getLongitude().degrees;

        return Sector.fromDegrees(minLat, maxLat, minLon, maxLon);
    }

    /**
     * Creates a new <code>Sector</code> and initializes it to the specified angles. The angles are assumed to be
     * normalized to +/- 90 degrees latitude and +/- 180 degrees longitude, but this method does not verify that.
     *
     * @param minLatitude  the sector's minimum latitude.
     * @param maxLatitude  the sector's maximum latitude.
     * @param minLongitude the sector's minimum longitude.
     * @param maxLongitude the sector's maximum longitude.
     *
     * @throws IllegalArgumentException if any of the angles are null
     */
    public Sector(Angle minLatitude, Angle maxLatitude, Angle minLongitude, Angle maxLongitude)
    {
        if (minLatitude == null || maxLatitude == null || minLongitude == null || maxLongitude == null)
        {
            String message = Logging.getMessage("nullValue.InputAnglesNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
        this.deltaLat = Angle.fromDegrees(this.maxLatitude.degrees - this.minLatitude.degrees);
        this.deltaLon = Angle.fromDegrees(this.maxLongitude.degrees - this.minLongitude.degrees);
    }

    public Sector(Sector sector)
    {
        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.minLatitude = new Angle(sector.getMinLatitude());
        this.maxLatitude = new Angle(sector.getMaxLatitude());
        this.minLongitude = new Angle(sector.getMinLongitude());
        this.maxLongitude = new Angle(sector.getMaxLongitude());
        this.deltaLat = Angle.fromDegrees(this.maxLatitude.degrees - this.minLatitude.degrees);
        this.deltaLon = Angle.fromDegrees(this.maxLongitude.degrees - this.minLongitude.degrees);
    }

    /**
     * Returns the sector's minimum latitude.
     *
     * @return The sector's minimum latitude.
     */
    public final Angle getMinLatitude()
    {
        return minLatitude;
    }

    /**
     * Returns the sector's minimum longitude.
     *
     * @return The sector's minimum longitude.
     */
    public final Angle getMinLongitude()
    {
        return minLongitude;
    }

    /**
     * Returns the sector's maximum latitude.
     *
     * @return The sector's maximum latitude.
     */
    public final Angle getMaxLatitude()
    {
        return maxLatitude;
    }

    /**
     * Returns the sector's maximum longitude.
     *
     * @return The sector's maximum longitude.
     */
    public final Angle getMaxLongitude()
    {
        return maxLongitude;
    }

    /**
     * Returns the angular difference between the sector's minimum and maximum latitudes: max - min
     *
     * @return The angular difference between the sector's minimum and maximum latitudes.
     */
    public final Angle getDeltaLat()
    {
        return this.deltaLat;//Angle.fromDegrees(this.maxLatitude.degrees - this.minLatitude.degrees);
    }

    public final double getDeltaLatDegrees()
    {
        return this.deltaLat.degrees;//this.maxLatitude.degrees - this.minLatitude.degrees;
    }

    public final double getDeltaLatRadians()
    {
        return this.deltaLat.radians;//this.maxLatitude.radians - this.minLatitude.radians;
    }

    /**
     * Returns the angular difference between the sector's minimum and maximum longitudes: max - min.
     *
     * @return The angular difference between the sector's minimum and maximum longitudes
     */
    public final Angle getDeltaLon()
    {
        return this.deltaLon;//Angle.fromDegrees(this.maxLongitude.degrees - this.minLongitude.degrees);
    }

    public final double getDeltaLonDegrees()
    {
        return this.deltaLon.degrees;//this.maxLongitude.degrees - this.minLongitude.degrees;
    }

    public final double getDeltaLonRadians()
    {
        return this.deltaLon.radians;//this.maxLongitude.radians - this.minLongitude.radians;
    }

    public boolean isWithinLatLonLimits()
    {
        return this.minLatitude.degrees >= -90 && this.maxLatitude.degrees <= 90
            && this.minLongitude.degrees >= -180 && this.maxLongitude.degrees <= 180;
    }

    public boolean isSameSector(Iterable<? extends LatLon> corners)
    {
        if (corners == null)
        {
            String message = Logging.getMessage("nullValue.LocationsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!isSector(corners))
            return false;

        Sector s = Sector.boundingSector(corners);

        return s.equals(this);
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public static boolean isSector(Iterable<? extends LatLon> corners)
    {
        if (corners == null)
        {
            String message = Logging.getMessage("nullValue.LocationsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        LatLon[] latlons = new LatLon[5];

        int i = 0;
        for (LatLon ll : corners)
        {
            if (i > 4 || ll == null)
                return false;

            latlons[i++] = ll;
        }

        if (!latlons[0].getLatitude().equals(latlons[1].getLatitude()))
            return false;
        if (!latlons[2].getLatitude().equals(latlons[3].getLatitude()))
            return false;
        if (!latlons[0].getLongitude().equals(latlons[3].getLongitude()))
            return false;
        if (!latlons[1].getLongitude().equals(latlons[2].getLongitude()))
            return false;

        if (i == 5 && !latlons[4].equals(latlons[0]))
            return false;

        return true;
    }

    /**
     * Returns the latitude and longitude of the sector's angular center: (minimum latitude + maximum latitude) / 2,
     * (minimum longitude + maximum longitude) / 2.
     *
     * @return The latitude and longitude of the sector's angular center
     */
    public LatLon getCentroid()
    {
        Angle la = Angle.fromDegrees(0.5 * (this.getMaxLatitude().degrees + this.getMinLatitude().degrees));
        Angle lo = Angle.fromDegrees(0.5 * (this.getMaxLongitude().degrees + this.getMinLongitude().degrees));
        return new LatLon(la, lo);
    }


    public final boolean contains(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return containsDegrees(latitude.degrees, longitude.degrees);
    }

    /**
     * Determines whether a latitude/longitude position is within the sector. The sector's angles are assumed to be
     * normalized to +/- 90 degrees latitude and +/- 180 degrees longitude. The result of the operation is undefined if
     * they are not.
     *
     * @param latLon the position to test, with angles normalized to +/- &#960 latitude and +/- 2&#960 longitude.
     *
     * @return <code>true</code> if the position is within the sector, <code>false</code> otherwise.
     *
     * @throws IllegalArgumentException if <code>latlon</code> is null.
     */
    public final boolean contains(LatLon latLon)
    {
        if (latLon == null)
        {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.contains(latLon.getLatitude(), latLon.getLongitude());
    }

    /**
     * Determines whether a latitude/longitude postion expressed in radians is within the sector. The sector's angles
     * are assumed to be normalized to +/- 90 degrees latitude and +/- 180 degrees longitude. The result of the
     * operation is undefined if they are not.
     *
     * @param radiansLatitude  the latitude in radians of the position to test, normalized +/- &#960.
     * @param radiansLongitude the longitude in radians of the position to test, normalized +/- 2&#960.
     *
     * @return <code>true</code> if the position is within the sector, <code>false</code> otherwise.
     */
    public boolean containsRadians(double radiansLatitude, double radiansLongitude)
    {
        return radiansLatitude >= this.minLatitude.radians && radiansLatitude <= this.maxLatitude.radians
            && radiansLongitude >= this.minLongitude.radians && radiansLongitude <= this.maxLongitude.radians;
    }

    public boolean containsDegrees(double degreesLatitude, double degreesLongitude)
    {
        return degreesLatitude >= this.minLatitude.degrees && degreesLatitude <= this.maxLatitude.degrees
            && degreesLongitude >= this.minLongitude.degrees && degreesLongitude <= this.maxLongitude.degrees;
    }

    /**
     * Determines whether another sectror is fully contained within this one. The sector's angles are assumed to be
     * normalized to +/- 90 degrees latitude and +/- 180 degrees longitude. The result of the operation is undefined if
     * they are not.
     *
     * @param that the sector to test for containment.
     *
     * @return <code>true</code> if this sector fully contains the input sector, otherwise <code>false</code>.
     */
    public boolean contains(Sector that)
    {
        if (that == null)
            return false;

        // Assumes normalized angles -- [-180, 180], [-90, 90]
        if (that.minLongitude.degrees < this.minLongitude.degrees)
            return false;
        if (that.maxLongitude.degrees > this.maxLongitude.degrees)
            return false;
        if (that.minLatitude.degrees < this.minLatitude.degrees)
            return false;
        //noinspection RedundantIfStatement
        if (that.maxLatitude.degrees > this.maxLatitude.degrees)
            return false;

        return true;
    }

    /**
     * Determines whether this sector intersects another sector's range of latitude and longitude. The sector's angles
     * are assumed to be normalized to +/- 90 degrees latitude and +/- 180 degrees longitude. The result of the
     * operation is undefined if they are not.
     *
     * @param that the sector to test for intersection.
     *
     * @return <code>true</code> if the sectors intersect, otherwise <code>false</code>.
     */
    public boolean intersects(Sector that)
    {
        if (that == null)
            return false;

        // Assumes normalized angles -- [-180, 180], [-90, 90]
        if (that.maxLongitude.degrees < this.minLongitude.degrees)
            return false;
        if (that.minLongitude.degrees > this.maxLongitude.degrees)
            return false;
        if (that.maxLatitude.degrees < this.minLatitude.degrees)
            return false;
        //noinspection RedundantIfStatement
        if (that.minLatitude.degrees > this.maxLatitude.degrees)
            return false;

        return true;
    }

    /**
     * Determines whether the interiors of this sector and another sector intersect. The sector's angles are assumed to
     * be normalized to +/- 90 degrees latitude and +/- 180 degrees longitude. The result of the operation is undefined
     * if they are not.
     *
     * @param that the sector to test for intersection.
     *
     * @return <code>true</code> if the sectors' interiors intersect, otherwise <code>false</code>.
     *
     * @see #intersects(Sector)
     */
    public boolean intersectsInterior(Sector that)
    {
        if (that == null)
            return false;

        // Assumes normalized angles -- [-180, 180], [-90, 90]
        if (that.maxLongitude.degrees <= this.minLongitude.degrees)
            return false;
        if (that.minLongitude.degrees >= this.maxLongitude.degrees)
            return false;
        if (that.maxLatitude.degrees <= this.minLatitude.degrees)
            return false;
        //noinspection RedundantIfStatement
        if (that.minLatitude.degrees >= this.maxLatitude.degrees)
            return false;

        return true;
    }

    /**
     * Determines whether this sector intersects the specified geographic line segment. The line segment is specified by
     * a begin location and an end location. The locations are are assumed to be connected by a linear path in
     * geographic space. This returns true if any location along that linear path intersects this sector, including the
     * begin and end locations.
     *
     * @param begin the line segment begin location.
     * @param end   the line segment end location.
     *
     * @return true <code>true</code> if this sector intersects the line segment, otherwise <code>false</code>.
     *
     * @throws IllegalArgumentException if either the begin location or the end location is null.
     */
    public boolean intersectsSegment(LatLon begin, LatLon end)
    {
        if (begin == null)
        {
            String message = Logging.getMessage("nullValue.BeginIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (end == null)
        {
            String message = Logging.getMessage("nullValue.EndIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 segmentBegin = new Vec4(begin.getLongitude().degrees, begin.getLatitude().degrees, 0);
        Vec4 segmentEnd = new Vec4(end.getLongitude().degrees, end.getLatitude().degrees, 0);
        Vec4 tmp = segmentEnd.subtract3(segmentBegin);
        Vec4 segmentCenter = segmentBegin.add3(segmentEnd).divide3(2);
        Vec4 segmentDirection = tmp.normalize3();
        double segmentExtent = tmp.getLength3() / 2.0;

        LatLon centroid = this.getCentroid();
        Vec4 boxCenter = new Vec4(centroid.getLongitude().degrees, centroid.getLatitude().degrees, 0);
        double boxExtentX = this.getDeltaLonDegrees() / 2.0;
        double boxExtentY = this.getDeltaLatDegrees() / 2.0;

        Vec4 diff = segmentCenter.subtract3(boxCenter);

        if (Math.abs(diff.x) > (boxExtentX + segmentExtent * Math.abs(segmentDirection.x)))
        {
            return false;
        }

        if (Math.abs(diff.y) > (boxExtentY + segmentExtent * Math.abs(segmentDirection.y)))
        {
            return false;
        }

        //noinspection SuspiciousNameCombination
        Vec4 segmentPerp = new Vec4(segmentDirection.y, -segmentDirection.x, 0);

        return Math.abs(segmentPerp.dot3(diff)) <=
            (boxExtentX * Math.abs(segmentPerp.x) + boxExtentY * Math.abs(segmentPerp.y));
    }

    /**
     * Returns a new sector whose angles are the extremes of the this sector and another. The new sector's minimum
     * latitude and longitude will be the minimum of the two sectors. The new sector's maximum latitude and longitude
     * will be the maximum of the two sectors. The sectors are assumed to be normalized to +/- 90 degrees latitude and
     * +/- 180 degrees longitude. The result of the operation is undefined if they are not.
     *
     * @param that the sector to join with <code>this</code>.
     *
     * @return A new sector formed from the extremes of the two sectors, or <code>this</code> if the incoming sector is
     *         <code>null</code>.
     */
    public final Sector union(Sector that)
    {
        if (that == null)
            return this;

        Angle minLat = this.minLatitude;
        Angle maxLat = this.maxLatitude;
        Angle minLon = this.minLongitude;
        Angle maxLon = this.maxLongitude;

        if (that.minLatitude.degrees < this.minLatitude.degrees)
            minLat = that.minLatitude;
        if (that.maxLatitude.degrees > this.maxLatitude.degrees)
            maxLat = that.maxLatitude;
        if (that.minLongitude.degrees < this.minLongitude.degrees)
            minLon = that.minLongitude;
        if (that.maxLongitude.degrees > this.maxLongitude.degrees)
            maxLon = that.maxLongitude;

        return new Sector(minLat, maxLat, minLon, maxLon);
    }

    public final Sector union(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
            return this;

        Angle minLat = this.minLatitude;
        Angle maxLat = this.maxLatitude;
        Angle minLon = this.minLongitude;
        Angle maxLon = this.maxLongitude;

        if (latitude.degrees < this.minLatitude.degrees)
            minLat = latitude;
        if (latitude.degrees > this.maxLatitude.degrees)
            maxLat = latitude;
        if (longitude.degrees < this.minLongitude.degrees)
            minLon = longitude;
        if (longitude.degrees > this.maxLongitude.degrees)
            maxLon = longitude;

        return new Sector(minLat, maxLat, minLon, maxLon);
    }

    public static Sector union(Sector sectorA, Sector sectorB)
    {
        if (sectorA == null || sectorB == null)
        {
            if (sectorA == sectorB)
                return sectorA;

            return sectorB == null ? sectorA : sectorB;
        }

        return sectorA.union(sectorB);
    }

    public static Sector union(Iterable<? extends Sector> sectors)
    {
        if (sectors == null)
        {
            String msg = Logging.getMessage("nullValue.SectorListIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Angle minLat = Angle.POS90;
        Angle maxLat = Angle.NEG90;
        Angle minLon = Angle.POS180;
        Angle maxLon = Angle.NEG180;

        for (Sector s : sectors)
        {
            if (s == null)
                continue;

            for (LatLon p : s)
            {
                if (p.getLatitude().degrees < minLat.degrees)
                    minLat = p.getLatitude();
                if (p.getLatitude().degrees > maxLat.degrees)
                    maxLat = p.getLatitude();
                if (p.getLongitude().degrees < minLon.degrees)
                    minLon = p.getLongitude();
                if (p.getLongitude().degrees > maxLon.degrees)
                    maxLon = p.getLongitude();
            }
        }

        return new Sector(minLat, maxLat, minLon, maxLon);
    }

    public final Sector intersection(Sector that)
    {
        if (that == null)
            return this;

        Angle minLat, maxLat;
        minLat = (this.minLatitude.degrees > that.minLatitude.degrees) ? this.minLatitude : that.minLatitude;
        maxLat = (this.maxLatitude.degrees < that.maxLatitude.degrees) ? this.maxLatitude : that.maxLatitude;
        if (minLat.degrees > maxLat.degrees)
            return null;

        Angle minLon, maxLon;
        minLon = (this.minLongitude.degrees > that.minLongitude.degrees) ? this.minLongitude : that.minLongitude;
        maxLon = (this.maxLongitude.degrees < that.maxLongitude.degrees) ? this.maxLongitude : that.maxLongitude;
        if (minLon.degrees > maxLon.degrees)
            return null;

        return new Sector(minLat, maxLat, minLon, maxLon);
    }

    public final Sector intersection(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
            return this;

        if (!this.contains(latitude, longitude))
            return null;
        return new Sector(latitude, latitude, longitude, longitude);
    }

    public Sector[] subdivide()
    {
        Angle midLat = Angle.average(this.minLatitude, this.maxLatitude);
        Angle midLon = Angle.average(this.minLongitude, this.maxLongitude);

        Sector[] sectors = new Sector[4];
        sectors[0] = new Sector(this.minLatitude, midLat, this.minLongitude, midLon);
        sectors[1] = new Sector(this.minLatitude, midLat, midLon, this.maxLongitude);
        sectors[2] = new Sector(midLat, this.maxLatitude, this.minLongitude, midLon);
        sectors[3] = new Sector(midLat, this.maxLatitude, midLon, this.maxLongitude);

        return sectors;
    }

    public Sector[] subdivide(int div)
    {
        double dLat = this.deltaLat.degrees / div;
        double dLon = this.deltaLon.degrees / div;

        Sector[] sectors = new Sector[div * div];
        int idx = 0;
        for (int row = 0; row < div; row++)
        {
            for (int col = 0; col < div; col++)
            {
                sectors[idx++] = Sector.fromDegrees(
                    this.minLatitude.degrees + dLat * row,
                    this.minLatitude.degrees + dLat * row + dLat,
                    this.minLongitude.degrees + dLon * col,
                    this.minLongitude.degrees + dLon * col + dLon);
            }
        }

        return sectors;
    }

    /**
     * Returns a four element array containing the Sector's angles in degrees. The returned array is ordered as follows:
     * minimum latitude, maximum latitude, minimum longitude, and maximum longitude.
     *
     * @return four-element array containing the Sector's angles.
     */
    public double[] toArrayDegrees()
    {
        return new double[]
            {
                this.minLatitude.degrees, this.maxLatitude.degrees,
                this.minLongitude.degrees, this.maxLongitude.degrees
            };
    }

    /**
     * Returns a {@link java.awt.geom.Rectangle2D} corresponding to this Sector in degrees lat-lon coordinates where x
     * corresponds to longitude and y to latitude.
     *
     * @return a {@link java.awt.geom.Rectangle2D} corresponding to this Sector in degrees lat-lon coordinates.
     */
    public Rectangle2D toRectangleDegrees()
    {
        return new Rectangle2D.Double(this.getMinLongitude().degrees, this.getMinLatitude().degrees,
            this.getDeltaLonDegrees(), this.getDeltaLatDegrees());
    }

    /**
     * Returns a string indicating the sector's angles.
     *
     * @return A string indicating the sector's angles.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.minLatitude.toString());
        sb.append(", ");
        sb.append(this.minLongitude.toString());
        sb.append(")");

        sb.append(", ");

        sb.append("(");
        sb.append(this.maxLatitude.toString());
        sb.append(", ");
        sb.append(this.maxLongitude.toString());
        sb.append(")");

        return sb.toString();
    }

    /**
     * Retrieve the size of this object in bytes. This implementation returns an exact value of the object's size.
     *
     * @return the size of this object in bytes
     */
    public long getSizeInBytes()
    {
        return 4 * minLatitude.getSizeInBytes();  // 4 angles
    }

    /**
     * Compares this sector to a specified sector according to their minimum latitude, minimum longitude, maximum
     * latitude, and maximum longitude, respectively.
     *
     * @param that the <code>Sector</code> to compareTo with <code>this</code>.
     *
     * @return -1 if this sector compares less than that specified, 0 if they're equal, and 1 if it compares greater.
     *
     * @throws IllegalArgumentException if <code>that</code> is null
     */
    public int compareTo(Sector that)
    {
        if (that == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.getMinLatitude().compareTo(that.getMinLatitude()) < 0)
            return -1;

        if (this.getMinLatitude().compareTo(that.getMinLatitude()) > 0)
            return 1;

        if (this.getMinLongitude().compareTo(that.getMinLongitude()) < 0)
            return -1;

        if (this.getMinLongitude().compareTo(that.getMinLongitude()) > 0)
            return 1;

        if (this.getMaxLatitude().compareTo(that.getMaxLatitude()) < 0)
            return -1;

        if (this.getMaxLatitude().compareTo(that.getMaxLatitude()) > 0)
            return 1;

        if (this.getMaxLongitude().compareTo(that.getMaxLongitude()) < 0)
            return -1;

        if (this.getMaxLongitude().compareTo(that.getMaxLongitude()) > 0)
            return 1;

        return 0;
    }

    /**
     * Creates an iterator over the four corners of the sector, starting with the southwest position and continuing
     * counter-clockwise.
     *
     * @return an iterator for the sector.
     */
    public Iterator<LatLon> iterator()
    {
        return new Iterator<LatLon>()
        {
            private int position = 0;

            public boolean hasNext()
            {
                return this.position < 4;
            }

            public LatLon next()
            {
                if (this.position > 3)
                    throw new NoSuchElementException();

                LatLon p;
                switch (this.position)
                {
                    case 0:
                        p = new LatLon(Sector.this.getMinLatitude(), Sector.this.getMinLongitude());
                        break;
                    case 1:
                        p = new LatLon(Sector.this.getMinLatitude(), Sector.this.getMaxLongitude());
                        break;
                    case 2:
                        p = new LatLon(Sector.this.getMaxLatitude(), Sector.this.getMaxLongitude());
                        break;
                    default:
                        p = new LatLon(Sector.this.getMaxLatitude(), Sector.this.getMinLongitude());
                        break;
                }
                ++this.position;

                return p;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns the coordinates of the sector as a list, in the order minLat, maxLat, minLon, maxLon.
     *
     * @return the list of sector coordinates.
     */
    public List<LatLon> asList()
    {
        ArrayList<LatLon> list = new ArrayList<LatLon>(4);

        for (LatLon ll : this)
        {
            list.add(ll);
        }

        return list;
    }

    /**
     * Returns the coordinates of the sector as an array of values in degrees, in the order minLat, maxLat, minLon,
     * maxLon.
     *
     * @return the array of sector coordinates.
     */
    public double[] asDegreesArray()
    {
        return new double[]
            {
                this.getMinLatitude().degrees, this.getMaxLatitude().degrees,
                this.getMinLongitude().degrees, this.getMaxLongitude().degrees
            };
    }

    /**
     * Returns the coordinates of the sector as an array of values in radians, in the order minLat, maxLat, minLon,
     * maxLon.
     *
     * @return the array of sector coordinates.
     */
    public double[] asRadiansArray()
    {
        return new double[]
            {
                this.getMinLatitude().radians, this.getMaxLatitude().radians,
                this.getMinLongitude().radians, this.getMaxLongitude().radians
            };
    }

    /**
     * Returns a list of the Lat/Lon coordinates of a Sector's corners.
     *
     * @return an array of the four corner locations, in the order SW, SE, NE, NW
     */
    public LatLon[] getCorners()
    {
        LatLon[] corners = new LatLon[4];

        corners[0] = new LatLon(this.minLatitude, this.minLongitude);
        corners[1] = new LatLon(this.minLatitude, this.maxLongitude);
        corners[2] = new LatLon(this.maxLatitude, this.maxLongitude);
        corners[3] = new LatLon(this.maxLatitude, this.minLongitude);

        return corners;
    }

    /**
     * Tests the equality of the sectors' angles. Sectors are equal if all of their corresponding angles are equal.
     *
     * @param o the sector to compareTo with <code>this</code>.
     *
     * @return <code>true</code> if the four corresponding angles of each sector are equal, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final gov.nasa.worldwind.geom.Sector sector = (gov.nasa.worldwind.geom.Sector) o;

        if (!maxLatitude.equals(sector.maxLatitude))
            return false;
        if (!maxLongitude.equals(sector.maxLongitude))
            return false;
        if (!minLatitude.equals(sector.minLatitude))
            return false;
        //noinspection RedundantIfStatement
        if (!minLongitude.equals(sector.minLongitude))
            return false;

        return true;
    }

    /**
     * Computes a hash code from the sector's four angles.
     *
     * @return a hash code incorporating the sector's four angles.
     */
    @Override
    public int hashCode()
    {
        int result;
        result = minLatitude.hashCode();
        result = 29 * result + maxLatitude.hashCode();
        result = 29 * result + minLongitude.hashCode();
        result = 29 * result + maxLongitude.hashCode();
        return result;
    }
}