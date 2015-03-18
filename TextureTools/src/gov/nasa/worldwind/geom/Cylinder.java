/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;

import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * Represents a geometric cylinder, most often used as a bounding volume. <code>Cylinder</code>s are immutable.
 *
 * @author Tom Gaskins
 * @version $Id: Cylinder.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class Cylinder implements Extent
{
    protected final Vec4 bottomCenter; // point at center of cylinder base
    protected final Vec4 topCenter; // point at center of cylinder top
    protected final Vec4 axisUnitDirection; // axis as unit vector from bottomCenter to topCenter
    protected final double cylinderRadius;
    protected final double cylinderHeight;

    /**
     * Create a Cylinder from two points and a radius.
     *
     * @param bottomCenter   the center point of of the cylinder's base.
     * @param topCenter      the center point of the cylinders top.
     * @param cylinderRadius the cylinder's radius.
     *
     * @throws IllegalArgumentException if the radius is zero or the top or bottom point is null or they are
     *                                  coincident.
     */
    public Cylinder(Vec4 bottomCenter, Vec4 topCenter, double cylinderRadius)
    {
        if (bottomCenter == null || topCenter == null || bottomCenter.equals(topCenter))
        {
            String message = Logging.getMessage(
                bottomCenter == null || topCenter == null ? "nullValue.EndPointIsNull" : "generic.EndPointsCoincident");

            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (cylinderRadius <= 0)
        {
            String message = Logging.getMessage("Geom.Cylinder.RadiusIsZeroOrNegative", cylinderRadius);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Convert the bottom center and top center points to points in four-dimensional homogeneous coordinates to
        // ensure that their w-coordinates are 1. Cylinder's intersection tests compute a dot product between these
        // points and each frustum plane, which depends on a w-coordinate of 1. We convert each point at construction to
        // avoid the additional overhead of converting them during every intersection test.
        this.bottomCenter = bottomCenter.toHomogeneousPoint3();
        this.topCenter = topCenter.toHomogeneousPoint3();
        this.cylinderHeight = this.bottomCenter.distanceTo3(this.topCenter);
        this.cylinderRadius = cylinderRadius;
        this.axisUnitDirection = this.topCenter.subtract3(this.bottomCenter).normalize3();
    }

    /**
     * Create a Cylinder from two points, a radius and an axis direction. Provided for use when unit axis is know and
     * computation of it can be avoided.
     *
     * @param bottomCenter   the center point of of the cylinder's base.
     * @param topCenter      the center point of the cylinders top.
     * @param cylinderRadius the cylinder's radius.
     * @param unitDirection  the unit-length axis of the cylinder.
     *
     * @throws IllegalArgumentException if the radius is zero or the top or bottom point is null or they are
     *                                  coincident.
     */
    public Cylinder(Vec4 bottomCenter, Vec4 topCenter, double cylinderRadius, Vec4 unitDirection)
    {
        if (bottomCenter == null || topCenter == null || bottomCenter.equals(topCenter))
        {
            String message = Logging.getMessage(
                bottomCenter == null || topCenter == null ? "nullValue.EndPointIsNull" : "generic.EndPointsCoincident");

            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (cylinderRadius <= 0)
        {
            String message = Logging.getMessage("Geom.Cylinder.RadiusIsZeroOrNegative", cylinderRadius);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Convert the bottom center and top center points to points in four-dimensional homogeneous coordinates to
        // ensure that their w-coordinates are 1. Cylinder's intersection tests compute a dot product between these
        // points and each frustum plane, which depends on a w-coordinate of 1. We convert each point at construction to
        // avoid the additional overhead of converting them during every intersection test.
        this.bottomCenter = bottomCenter.toHomogeneousPoint3();
        this.topCenter = topCenter.toHomogeneousPoint3();
        this.cylinderHeight = this.bottomCenter.distanceTo3(this.topCenter);
        this.cylinderRadius = cylinderRadius;
        this.axisUnitDirection = unitDirection;
    }

    /**
     * Returns the unit-length axis of this cylinder.
     *
     * @return the unit-length axis of this cylinder.
     */
    public Vec4 getAxisUnitDirection()
    {
        return axisUnitDirection;
    }

    /**
     * Returns the this cylinder's bottom-center point.
     *
     * @return this cylinder's bottom-center point.
     */
    public Vec4 getBottomCenter()
    {
        return bottomCenter;
    }

    /**
     * Returns the this cylinder's top-center point.
     *
     * @return this cylinder's top-center point.
     */
    public Vec4 getTopCenter()
    {
        return topCenter;
    }

    /**
     * Returns this cylinder's radius.
     *
     * @return this cylinder's radius.
     */
    public double getCylinderRadius()
    {
        return cylinderRadius;
    }

    /**
     * Returns this cylinder's height.
     *
     * @return this cylinder's height.
     */
    public double getCylinderHeight()
    {
        return cylinderHeight;
    }

    /**
     * Return this cylinder's center point.
     *
     * @return this cylinder's center point.
     */
    public Vec4 getCenter()
    {
        Vec4 b = this.bottomCenter;
        Vec4 t = this.topCenter;
        return new Vec4(
            (b.x + t.x) / 2.0,
            (b.y + t.y) / 2.0,
            (b.z + t.z) / 2.0);
    }

    /** {@inheritDoc} */
    public double getDiameter()
    {
        return 2 * this.getRadius();
    }

    /** {@inheritDoc} */
    public double getRadius()
    {
        // return the radius of the enclosing sphere
        double halfHeight = this.bottomCenter.distanceTo3(this.topCenter) / 2.0;
        return Math.sqrt(halfHeight * halfHeight + this.cylinderRadius * this.cylinderRadius);
    }

    /**
     * Return this cylinder's volume.
     *
     * @return this cylinder's volume.
     */
    public double getVolume()
    {
        return Math.PI * this.cylinderRadius * this.cylinderRadius * this.cylinderHeight;
    }

    /**
     * Compute a bounding cylinder for a collection of points.
     *
     * @param points the points to compute a bounding cylinder for.
     *
     * @return a cylinder bounding all the points. The axis of the cylinder is the longest principal axis of the
     *         collection. (See {@link WWMath#computePrincipalAxes(Iterable)}.
     *
     * @throws IllegalArgumentException if the point list is null or empty.
     * @see #computeVerticalBoundingCylinder(gov.nasa.worldwind.globes.Globe, double, Sector)
     */
    public static Cylinder computeBoundingCylinder(Iterable<? extends Vec4> points)
    {
        if (points == null)
        {
            String message = Logging.getMessage("nullValue.PointListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4[] axes = WWMath.computePrincipalAxes(points);
        if (axes == null)
        {
            String message = Logging.getMessage("generic.ListIsEmpty");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 r = axes[0];
        Vec4 s = axes[1];

        List<Vec4> sPlanePoints = new ArrayList<Vec4>();
        double minDotR = Double.MAX_VALUE;
        double maxDotR = -minDotR;

        for (Vec4 p : points)
        {
            double pdr = p.dot3(r);
            sPlanePoints.add(p.subtract3(r.multiply3(p.dot3(r))));

            if (pdr < minDotR)
                minDotR = pdr;
            if (pdr > maxDotR)
                maxDotR = pdr;
        }

        Vec4 minPoint = sPlanePoints.get(0);
        Vec4 maxPoint = minPoint;
        double minDotS = Double.MAX_VALUE;
        double maxDotS = -minDotS;
        for (Vec4 p : sPlanePoints)
        {
            double d = p.dot3(s);
            if (d < minDotS)
            {
                minPoint = p;
                minDotS = d;
            }
            if (d > maxDotS)
            {
                maxPoint = p;
                maxDotS = d;
            }
        }

        Vec4 center = minPoint.add3(maxPoint).divide3(2);
        double radius = center.distanceTo3(minPoint);

        for (Vec4 h : sPlanePoints)
        {
            Vec4 hq = h.subtract3(center);
            double d = hq.getLength3();
            if (d > radius)
            {
                Vec4 g = center.subtract3(hq.normalize3().multiply3(radius));
                center = g.add3(h).divide3(2);
                radius = d;
            }
        }

        Vec4 bottomCenter = center.add3(r.multiply3(minDotR));
        Vec4 topCenter = center.add3((r.multiply3(maxDotR)));

        if (radius == 0)
            radius = 1;

        if (bottomCenter.equals(topCenter))
            topCenter = bottomCenter.add3(new Vec4(1, 0, 0));

        return new Cylinder(bottomCenter, topCenter, radius);
    }

    /** {@inheritDoc} */
    public Intersection[] intersect(Line line)
    {
        if (line == null)
        {
            String message = Logging.getMessage("nullValue.LineIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double[] tVals = new double[2];
        if (!intcyl(line.getOrigin(), line.getDirection(), this.bottomCenter, this.axisUnitDirection,
            this.cylinderRadius, tVals))
            return null;

        if (!clipcyl(line.getOrigin(), line.getDirection(), this.bottomCenter, this.topCenter,
            this.axisUnitDirection, tVals))
            return null;

        if (!Double.isInfinite(tVals[0]) && !Double.isInfinite(tVals[1]) && tVals[0] >= 0.0 && tVals[1] >= 0.0)
            return new Intersection[] {new Intersection(line.getPointAt(tVals[0]), false),
                new Intersection(line.getPointAt(tVals[1]), false)};
        if (!Double.isInfinite(tVals[0]) && tVals[0] >= 0.0)
            return new Intersection[] {new Intersection(line.getPointAt(tVals[0]), false)};
        if (!Double.isInfinite(tVals[1]) && tVals[1] >= 0.0)
            return new Intersection[] {new Intersection(line.getPointAt(tVals[1]), false)};
        return null;
    }

    /** {@inheritDoc} */
    public boolean intersects(Line line)
    {
        if (line == null)
        {
            String message = Logging.getMessage("nullValue.LineIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return intersect(line) != null;
    }

    // Taken from "Graphics Gems IV", Section V.2, page 356.

    protected boolean intcyl(Vec4 raybase, Vec4 raycos, Vec4 base, Vec4 axis, double radius, double[] tVals)
    {
        boolean hit; // True if ray intersects cyl
        Vec4 RC; // Ray base to cylinder base
        double d; // Shortest distance between the ray and the cylinder
        double t, s; // Distances along the ray
        Vec4 n, D, O;
        double ln;

        RC = raybase.subtract3(base);
        n = raycos.cross3(axis);

        // Ray is parallel to the cylinder's axis.
        if ((ln = n.getLength3()) == 0.0)
        {
            d = RC.dot3(axis);
            D = RC.subtract3(axis.multiply3(d));
            d = D.getLength3();
            tVals[0] = Double.NEGATIVE_INFINITY;
            tVals[1] = Double.POSITIVE_INFINITY;
            // True if ray is in cylinder.
            return d <= radius;
        }

        n = n.normalize3();
        d = Math.abs(RC.dot3(n)); // Shortest distance.
        hit = (d <= radius);

        // If ray hits cylinder.
        if (hit)
        {
            O = RC.cross3(axis);
            t = -O.dot3(n) / ln;
            O = n.cross3(axis);
            O = O.normalize3();
            s = Math.abs(Math.sqrt(radius * radius - d * d) / raycos.dot3(O));
            tVals[0] = t - s; // Entering distance.
            tVals[1] = t + s; // Exiting distance.
        }

        return hit;
    }

    // Taken from "Graphics Gems IV", Section V.2, page 356.

    protected boolean clipcyl(Vec4 raybase, Vec4 raycos, Vec4 bot, Vec4 top, Vec4 axis, double[] tVals)
    {
        double dc, dwb, dwt, tb, tt;
        double in, out; // Object intersection distances.

        in = tVals[0];
        out = tVals[1];

        dc = axis.dot3(raycos);
        dwb = axis.dot3(raybase) - axis.dot3(bot);
        dwt = axis.dot3(raybase) - axis.dot3(top);

        // Ray is parallel to the cylinder end-caps.
        if (dc == 0.0)
        {
            if (dwb <= 0.0)
                return false;
            if (dwt >= 0.0)
                return false;
        }
        else
        {
            // Intersect the ray with the bottom end-cap.
            tb = -dwb / dc;
            // Intersect the ray with the top end-cap.
            tt = -dwt / dc;

            // Bottom is near cap, top is far cap.
            if (dc >= 0.0)
            {
                if (tb > out)
                    return false;
                if (tt < in)
                    return false;
                if (tb > in && tb < out)
                    in = tb;
                if (tt > in && tt < out)
                    out = tt;
            }
            // Bottom is far cap, top is near cap.
            else
            {
                if (tb < in)
                    return false;
                if (tt > out)
                    return false;
                if (tb > in && tb < out)
                    out = tb;
                if (tt > in && tt < out)
                    in = tt;
            }
        }

        tVals[0] = in;
        tVals[1] = out;
        return in < out;
    }

    protected double intersects(Plane plane, double effectiveRadius)
    {
        // Test the distance from the first cylinder end-point. Assumes that bottomCenter's w-coordinate is 1.
        double dq1 = plane.dot(this.bottomCenter);
        boolean bq1 = dq1 <= -effectiveRadius;

        // Test the distance from the top of the cylinder. Assumes that topCenter's w-coordinate is 1.
        double dq2 = plane.dot(this.topCenter);
        boolean bq2 = dq2 <= -effectiveRadius;

        if (bq1 && bq2) // both beyond effective radius; cylinder is on negative side of plane
            return -1;

        if (bq1 == bq2) // both within effective radius; can't draw any conclusions
            return 0;

        return 1; // Cylinder almost certainly intersects
    }

    protected double intersectsAt(Plane plane, double effectiveRadius, Vec4[] endpoints)
    {
        // Test the distance from the first end-point. Assumes that the first end-point's w-coordinate is 1.
        double dq1 = plane.dot(endpoints[0]);
        boolean bq1 = dq1 <= -effectiveRadius;

        // Test the distance from the possibly reduced second cylinder end-point. Assumes that the second end-point's
        // w-coordinate is 1.
        double dq2 = plane.dot(endpoints[1]);
        boolean bq2 = dq2 <= -effectiveRadius;

        if (bq1 && bq2) // endpoints more distant from plane than effective radius; cylinder is on neg. side of plane
            return -1;

        if (bq1 == bq2) // endpoints less distant from plane than effective radius; can't draw any conclusions
            return 0;

        // Compute and return the endpoints of the cylinder on the positive side of the plane.
        double t = (effectiveRadius + dq1) / plane.getNormal().dot3(endpoints[0].subtract3(endpoints[1]));

        Vec4 newEndPoint = endpoints[0].add3(endpoints[1].subtract3(endpoints[0]).multiply3(t));
        if (bq1) // Truncate the lower end of the cylinder
            endpoints[0] = newEndPoint;
        else // Truncate the upper end of the cylinder
            endpoints[1] = newEndPoint;

        return t;
    }

    /** {@inheritDoc} */
    public double getEffectiveRadius(Plane plane)
    {
        if (plane == null)
            return 0;

        // Determine the effective radius of the cylinder axis relative to the plane.
        double dot = plane.getNormal().dot3(this.axisUnitDirection);
        double scale = 1d - dot * dot;
        if (scale <= 0)
            return 0;
        else
            return this.cylinderRadius * Math.sqrt(scale);
    }

    /** {@inheritDoc} */
    public boolean intersects(Plane plane)
    {
        if (plane == null)
        {
            String message = Logging.getMessage("nullValue.PlaneIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double effectiveRadius = this.getEffectiveRadius(plane);
        return this.intersects(plane, effectiveRadius) >= 0;
    }

    /** {@inheritDoc} */
    public boolean intersects(Frustum frustum)
    {
        if (frustum == null)
        {
            String message = Logging.getMessage("nullValue.FrustumIsNull");

            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double intersectionPoint;
        Vec4[] endPoints = new Vec4[] {this.bottomCenter, this.topCenter};

        double effectiveRadius = this.getEffectiveRadius(frustum.getNear());
        intersectionPoint = this.intersectsAt(frustum.getNear(), effectiveRadius, endPoints);
        if (intersectionPoint < 0)
            return false;

        // Near and far have the same effective radius.
        intersectionPoint = this.intersectsAt(frustum.getFar(), effectiveRadius, endPoints);
        if (intersectionPoint < 0)
            return false;

        effectiveRadius = this.getEffectiveRadius(frustum.getLeft());
        intersectionPoint = this.intersectsAt(frustum.getLeft(), effectiveRadius, endPoints);
        if (intersectionPoint < 0)
            return false;

        effectiveRadius = this.getEffectiveRadius(frustum.getRight());
        intersectionPoint = this.intersectsAt(frustum.getRight(), effectiveRadius, endPoints);
        if (intersectionPoint < 0)
            return false;

        effectiveRadius = this.getEffectiveRadius(frustum.getTop());
        intersectionPoint = this.intersectsAt(frustum.getTop(), effectiveRadius, endPoints);
        if (intersectionPoint < 0)
            return false;

        effectiveRadius = this.getEffectiveRadius(frustum.getBottom());
        intersectionPoint = this.intersectsAt(frustum.getBottom(), effectiveRadius, endPoints);
        return intersectionPoint >= 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof Cylinder))
            return false;

        Cylinder cylinder = (Cylinder) o;

        if (Double.compare(cylinder.cylinderHeight, cylinderHeight) != 0)
            return false;
        if (Double.compare(cylinder.cylinderRadius, cylinderRadius) != 0)
            return false;
        if (axisUnitDirection != null ? !axisUnitDirection.equals(cylinder.axisUnitDirection)
            : cylinder.axisUnitDirection != null)
            return false;
        if (bottomCenter != null ? !bottomCenter.equals(cylinder.bottomCenter) : cylinder.bottomCenter != null)
            return false;
        //noinspection RedundantIfStatement
        if (topCenter != null ? !topCenter.equals(cylinder.topCenter) : cylinder.topCenter != null)
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = bottomCenter != null ? bottomCenter.hashCode() : 0;
        result = 31 * result + (topCenter != null ? topCenter.hashCode() : 0);
        result = 31 * result + (axisUnitDirection != null ? axisUnitDirection.hashCode() : 0);
        temp = cylinderRadius != +0.0d ? Double.doubleToLongBits(cylinderRadius) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = cylinderHeight != +0.0d ? Double.doubleToLongBits(cylinderHeight) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString()
    {
        return this.cylinderRadius + ", " + this.bottomCenter.toString() + ", " + this.topCenter.toString() + ", "
            + this.axisUnitDirection.toString();
    }
}
