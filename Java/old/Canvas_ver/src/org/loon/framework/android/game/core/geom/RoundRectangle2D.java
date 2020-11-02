/*
 * Copyright 1997-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package org.loon.framework.android.game.core.geom;


/**
 * The <code>RoundRectangle2D</code> class defines a rectangle with rounded
 * corners defined by a location {@code (x,y)}, a dimension {@code (w x h)}, and
 * the width and height of an arc with which to round the corners.
 * <p>
 * This class is the abstract superclass for all objects that store a 2D rounded
 * rectangle. The actual storage representation of the coordinates is left to
 * the subclass.
 * 
 * @author Jim Graham
 * @since 1.2
 */
public abstract class RoundRectangle2D extends RectangularShape {


	/**
	 * This is an abstract class that cannot be instantiated directly.
	 * Type-specific implementation subclasses are available for instantiation
	 * and provide a number of formats for storing the information necessary to
	 * satisfy the various accessor methods below.
	 * 
	 * @see and.awt.geom.RoundRectangle2D.Float
	 * @see and.awt.geom.RoundRectangle2D.Double
	 * @since 1.2
	 */
	protected RoundRectangle2D() {
	}

	/**
	 * Gets the width of the arc that rounds off the corners.
	 * 
	 * @return the width of the arc that rounds off the corners of this
	 *         <code>RoundRectangle2D</code>.
	 * @since 1.2
	 */
	public abstract double getArcWidth();

	/**
	 * Gets the height of the arc that rounds off the corners.
	 * 
	 * @return the height of the arc that rounds off the corners of this
	 *         <code>RoundRectangle2D</code>.
	 * @since 1.2
	 */
	public abstract double getArcHeight();

	/**
	 * Sets the location, size, and corner radii of this
	 * <code>RoundRectangle2D</code> to the specified <code>double</code>
	 * values.
	 * 
	 * @param x
	 *            the X coordinate to which to set the location of this
	 *            <code>RoundRectangle2D</code>
	 * @param y
	 *            the Y coordinate to which to set the location of this
	 *            <code>RoundRectangle2D</code>
	 * @param w
	 *            the width to which to set this <code>RoundRectangle2D</code>
	 * @param h
	 *            the height to which to set this <code>RoundRectangle2D</code>
	 * @param arcWidth
	 *            the width to which to set the arc of this
	 *            <code>RoundRectangle2D</code>
	 * @param arcHeight
	 *            the height to which to set the arc of this
	 *            <code>RoundRectangle2D</code>
	 * @since 1.2
	 */
	public abstract void setRoundRect(double x, double y, double w, double h,
			double arcWidth, double arcHeight);

	/**
	 * Sets this <code>RoundRectangle2D</code> to be the same as the specified
	 * <code>RoundRectangle2D</code>.
	 * 
	 * @param rr
	 *            the specified <code>RoundRectangle2D</code>
	 * @since 1.2
	 */
	public void setRoundRect(RoundRectangle2D rr) {
		setRoundRect(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr
				.getArcWidth(), rr.getArcHeight());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	public void setFrame(double x, double y, double w, double h) {
		setRoundRect(x, y, w, h, getArcWidth(), getArcHeight());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	public boolean contains(double x, double y) {
		if (isEmpty()) {
			return false;
		}
		double rrx0 = getX();
		double rry0 = getY();
		double rrx1 = rrx0 + getWidth();
		double rry1 = rry0 + getHeight();
		// Check for trivial rejection - point is outside bounding rectangle
		if (x < rrx0 || y < rry0 || x >= rrx1 || y >= rry1) {
			return false;
		}
		double aw = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0;
		double ah = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0;
		// Check which corner point is in and do circular containment
		// test - otherwise simple acceptance
		if (x >= (rrx0 += aw) && x < (rrx0 = rrx1 - aw)) {
			return true;
		}
		if (y >= (rry0 += ah) && y < (rry0 = rry1 - ah)) {
			return true;
		}
		x = (x - rrx0) / aw;
		y = (y - rry0) / ah;
		return (x * x + y * y <= 1.0);
	}

	private int classify(double coord, double left, double right, double arcsize) {
		if (coord < left) {
			return 0;
		} else if (coord < left + arcsize) {
			return 1;
		} else if (coord < right - arcsize) {
			return 2;
		} else if (coord < right) {
			return 3;
		} else {
			return 4;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	public boolean intersects(double x, double y, double w, double h) {
		if (isEmpty() || w <= 0 || h <= 0) {
			return false;
		}
		double rrx0 = getX();
		double rry0 = getY();
		double rrx1 = rrx0 + getWidth();
		double rry1 = rry0 + getHeight();
		// Check for trivial rejection - bounding rectangles do not intersect
		if (x + w <= rrx0 || x >= rrx1 || y + h <= rry0 || y >= rry1) {
			return false;
		}
		double aw = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0;
		double ah = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0;
		int x0class = classify(x, rrx0, rrx1, aw);
		int x1class = classify(x + w, rrx0, rrx1, aw);
		int y0class = classify(y, rry0, rry1, ah);
		int y1class = classify(y + h, rry0, rry1, ah);
		// Trivially accept if any point is inside inner rectangle
		if (x0class == 2 || x1class == 2 || y0class == 2 || y1class == 2) {
			return true;
		}
		// Trivially accept if either edge spans inner rectangle
		if ((x0class < 2 && x1class > 2) || (y0class < 2 && y1class > 2)) {
			return true;
		}
		// Since neither edge spans the center, then one of the corners
		// must be in one of the rounded edges. We detect this case if
		// a [xy]0class is 3 or a [xy]1class is 1. One of those two cases
		// must be true for each direction.
		// We now find a "nearest point" to test for being inside a rounded
		// corner.
		if (x1class == 1)
			x = x + w - (rrx0 + aw);
		else
			x = x - (rrx1 - aw);

		if (y1class == 1)
			y = y + h - (rry0 + ah);
		else
			y = y - (rry1 - ah);
		x = x / aw;
		y = y / ah;
		return (x * x + y * y <= 1.0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	public boolean contains(double x, double y, double w, double h) {
		if (isEmpty() || w <= 0 || h <= 0) {
			return false;
		}
		return (contains(x, y) && contains(x + w, y) && contains(x, y + h) && contains(
				x + w, y + h));
	}

	/**
	 * Returns an iteration object that defines the boundary of this
	 * <code>RoundRectangle2D</code>. The iterator for this class is
	 * multi-threaded safe, which means that this <code>RoundRectangle2D</code>
	 * class guarantees that modifications to the geometry of this
	 * <code>RoundRectangle2D</code> object do not affect any iterations of that
	 * geometry that are already in process.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the
	 *            coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @return the <code>PathIterator</code> object that returns the geometry of
	 *         the outline of this <code>RoundRectangle2D</code>, one segment at
	 *         a time.
	 * @since 1.2
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new RoundRectIterator(this, at);
	}

	/**
	 * Returns the hashcode for this <code>RoundRectangle2D</code>.
	 * 
	 * @return the hashcode for this <code>RoundRectangle2D</code>.
	 * @since 1.6
	 */
	public int hashCode() {
		long bits = java.lang.Double.doubleToLongBits(getX());
		bits += java.lang.Double.doubleToLongBits(getY()) * 37;
		bits += java.lang.Double.doubleToLongBits(getWidth()) * 43;
		bits += java.lang.Double.doubleToLongBits(getHeight()) * 47;
		bits += java.lang.Double.doubleToLongBits(getArcWidth()) * 53;
		bits += java.lang.Double.doubleToLongBits(getArcHeight()) * 59;
		return (((int) bits) ^ ((int) (bits >> 32)));
	}

	/**
	 * Determines whether or not the specified <code>Object</code> is equal to
	 * this <code>RoundRectangle2D</code>. The specified <code>Object</code> is
	 * equal to this <code>RoundRectangle2D</code> if it is an instance of
	 * <code>RoundRectangle2D</code> and if its location, size, and corner arc
	 * dimensions are the same as this <code>RoundRectangle2D</code>.
	 * 
	 * @param obj
	 *            an <code>Object</code> to be compared with this
	 *            <code>RoundRectangle2D</code>.
	 * @return <code>true</code> if <code>obj</code> is an instance of
	 *         <code>RoundRectangle2D</code> and has the same values;
	 *         <code>false</code> otherwise.
	 * @since 1.6
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RoundRectangle2D) {
			RoundRectangle2D rr2d = (RoundRectangle2D) obj;
			return ((getX() == rr2d.getX()) && (getY() == rr2d.getY())
					&& (getWidth() == rr2d.getWidth())
					&& (getHeight() == rr2d.getHeight())
					&& (getArcWidth() == rr2d.getArcWidth()) && (getArcHeight() == rr2d
					.getArcHeight()));
		}
		return false;
	}
}
