/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;


/**
 * Represents a volume enclosing one or more objects or collections of points. Primarily used to test intersections with
 * other objects.
 *
 * @author Tom Gaskins
 * @version $Id: Extent.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public interface Extent
{
    /**
     * Returns the extent's center point.
     *
     * @return the extent's center point.
     */
    Vec4 getCenter();

    /**
     * Returns the extent's diameter. The computation of the diameter depends on the implementing class. See the
     * documentation for the individual classes to determine how they compute a diameter.
     *
     * @return the extent's diameter.
     */
    double getDiameter();

    /**
     * Returns the extent's radius. The computation of the radius depends on the implementing class. See the
     * documentation for the individual classes to determine how they compute a radius.
     *
     * @return the extent's radius.
     */
    double getRadius();

    /**
     * Determines whether or not this <code>Extent</code> intersects <code>frustum</code>. Returns true if any part of
     * these two objects intersect, including the case where either object wholly contains the other, false otherwise.
     *
     * @param frustum the <code>Frustum</code> with which to test for intersection.
     *
     * @return true if there is an intersection, false otherwise.
     */
    boolean intersects(Frustum frustum);

    /**
     * Computes the intersections of this extent with <code>line</code>. The returned array may be either null or of
     * zero length if no intersections are discovered. It does not contain null elements. Tangential intersections are
     * marked as such. <code>line</code> is considered to have infinite length in both directions.
     *
     * @param line the <code>Line</code> with which to intersect this <code>Extent</code>.
     *
     * @return an array of intersections representing all the points where <code>line</code> enters or leave this
     *         <code>Extent</code>.
     */
    gov.nasa.worldwind.geom.Intersection[] intersect(gov.nasa.worldwind.geom.Line line);

    /**
     * Determines whether or not <code>line</code> intersects this <code>Extent</code>. This method may be faster than
     * checking the size of the array returned by <code>intersect(Line)</code>. Implementing methods must ensure that
     * this method returns true if and only if <code>intersect(Line)</code> returns a non-null array containing at least
     * one element.
     *
     * @param line the <code>Line</code> with which to test for intersection.
     *
     * @return true if an intersection is found, false otherwise.
     */
    boolean intersects(gov.nasa.worldwind.geom.Line line);

    /**
     * Calculate whether or not this <code>Extent</code> is intersected by <code>plane</code>.
     *
     * @param plane the <code>Plane</code> with which to test for intersection.
     *
     * @return true if <code>plane</code> is found to intersect this <code>Extent</code>.
     */
    boolean intersects(gov.nasa.worldwind.geom.Plane plane);

    /**
     * Computes the effective radius of the extent relative to a specified plane.
     *
     * @param plane the plane.
     *
     * @return the effective radius, or 0 if the plane is null.
     */
    double getEffectiveRadius(Plane plane);
}
