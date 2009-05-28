/*
 * Scriptographer
 *
 * This file is part of Scriptographer, a Plugin for Adobe Illustrator.
 *
 * Copyright (c) 2002-2008 Juerg Lehni, http://www.scratchdisk.com.
 * All rights reserved.
 *
 * Please visit http://scriptographer.com/ for updates and contact.
 *
 * -- GPL LICENSE NOTICE --
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * -- GPL LICENSE NOTICE --
 *
 * File created on 19.12.2004.
 *
 * $Id:Rectangle.java 402 2007-08-22 23:24:49Z lehni $
 */

package com.scriptographer.ai;

import java.awt.geom.Rectangle2D;

import com.scratchdisk.script.ArgumentReader;

/**
 * A Rectangle specifies an area that is enclosed by it's top-left point (x, y),
 * its width, and its height. It should not be confused with a rectangular path,
 * it is not an item.
 * 
 * @author lehni
 */
public class Rectangle {
	protected double x;
	protected double y;
	protected double width;
	protected double height;
	
	public Rectangle() {
		x = y = width = height = 0;
	}

	/**
	 * Creates a new rectangle.
	 * @param x	The left coordinate.
	 * @param y The top coordinate.
	 * @param width
	 * @param height
	 */
	public Rectangle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Creates a new rectangle from the passed rectangle.
	 * @param rt
	 */
	public Rectangle(Rectangle rt) {
		this(rt.x, rt.y, rt.width, rt.height);
	}

	/**
	 * @jshide
	 */
	public Rectangle(Rectangle2D rt) {
		this(rt.getX(), rt.getY(), rt.getWidth(), rt.getHeight());
	}
	
	/**
	 * Creates a new rectangle from the passed points.
	 * @param point1 The first point defining the rectangle.
	 * @param point2 The second point defining the rectangle.
	 */
	public Rectangle(Point point1, Point point2) {
		this(point1.x, point1.y, point2.x - point1.x, point2.y - point1.y);
		if (width < 0) {
			x = point2.x;
			width = -width;
		}
		if (height < 0) {
			y = point2.y;
			height = -height;
		}
	}

	public Rectangle(Point point, Size size) {
		this(point.x, point.y, size.width, size.height);
	}

	/**
	 * @jshide
	 */
	public Rectangle(ArgumentReader reader) {
		this(reader.readDouble("x", 0),
				reader.readDouble("y", 0),
				reader.readDouble("width", 0),
				reader.readDouble("height", 0));
	}

	/**
	 * Changes the boundary properties of the rectangle.
	 * @param x The left position.
	 * @param y The top position.
	 * @param width
	 * @param height
	 * 
	 * @jshide
	 */
	public void set(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * The x position of the rectangle.
	 */
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	/**
	 * The y position of the rectangle. In the AI coordinate
	 * system, the y axis grows from bottom to top.
	 */
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * The width of the rectangle.
	 */
	public double getWidth() {
		return width;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * The height of the rectangle.
	 */
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * The position of the left hand side of the rectangle. Note that this
	 * doesn't move the whole rectangle; the right hand side stays where it was.
	 */
	public double getLeft() {
		return x;
	}

	public void setLeft(double left) {
		// right should not move
		width -= left - x;
		x = left;
	}

	/**
	 * The top coordinate of the rectangle. In the AI coordinate
	 * system, the y axis grows from bottom to top. Note that this
	 * doesn't move the whole rectangle: the bottom won't move.
	 */
	public double getTop() {
		return y + height;
	}
	
	public void setTop(double top) {
		height = top - y;
	}

	/**
	 * The position of the right hand side of the rectangle. Note that this
	 * doesn't move the whole rectangle; the left hand side stays where it was.
	 */
	public double getRight() {
		return x + width;
	}

	public void setRight(double right) {
		width = right - x;
	}

	/**
	 * The bottom coordinate of the rectangle. In the AI coordinate
	 * system, the y axis grows from bottom to top. Note that this doesn't move
	 * the whole rectangle: the top won't move.
	 */
	public double getBottom() {
		return y;
	}
	
	public void setBottom(double bottom) {
		// top should not move
		height -= bottom - y;
		y = bottom;
	}

	public Size getSize() {
		return new Size(width, height);
	}

	/**
	 * @jshide
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setSize(Size size) {
		this.width = size.width;
		this.height = size.height;
	}

	private double getCenterX() {
		return x + width * 0.5f;
	}
	
	private double getCenterY() {
		return y + height * 0.5f;
	}

	private void setCenterX(double x) {
		this.x = x - width * 0.5f;
	}

	private void setCenterY(double y) {
		this.y = y - height * 0.5f;
	}
	
	/**
	 * The center point of the rectangle.
	 */
	public Point getCenter() {
		return new Point(getCenterX(), getCenterY());
	}

	public void setCenter(Point center) {
		setCenterX(center.x);
		setCenterY(center.y);
	}

	/**
	 * The top left point of the rectangle.
	 */
	public Point getTopLeft() {
		return new Point(getLeft(), getTop());
	}

	public void setTopLeft(Point pt) {
		setLeft(pt.x);
		setTop(pt.y);
	}

	/**
	 * The top right point of the rectangle.
	 */
	public Point getTopRight() {
		return new Point(getRight(), getTop());
	}

	public void setTopRight(Point pt) {
		setRight(pt.x);
		setTop(pt.y);
	}

	/**
	 * The bottom left point of the rectangle.
	 */
	public Point getBottomLeft() {
		return new Point(getLeft(), getBottom());
	}

	public void setBottomLeft(Point pt) {
		setLeft(pt.x);
		setBottom(pt.y);
	}

	/**
	 * The bottom right point of the rectangle.
	 */
	public Point getBottomRight() {
		return new Point(getRight(), getBottom());
	}

	public void setBottomRight(Point pt) {
		setRight(pt.x);
		setBottom(pt.y);
	}

	public Point getLeftCenter() {
		return new Point(getLeft(), getCenterY());
	}
	
	public void setLeftCenter(Point pt) {
		setLeft(pt.x);
		setCenterY(pt.y);
	}

	public Point getTopCenter() {
		return new Point(getCenterX(), getTop());
	}
	
	public void setTopCenter(Point pt) {
		setCenterX(pt.x);
		setTop(pt.y);
	}

	public Point getRightCenter() {
		return new Point(getRight(), getCenterY());
	}
	
	public void setRightCenter(Point pt) {
		setRight(pt.x);
		setCenterY(pt.y);
	}

	public Point getBottomCenter() {
		return new Point(getCenterX(), getBottom());
	}
	
	public void setBottomCenter(Point pt) {
		setCenterX(pt.x);
		setBottom(pt.y);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(128);
		buf.append("{ x: ").append(x);
		buf.append(", y: ").append(y);
		buf.append(", width: ").append(width);
		buf.append(", height: ").append(height);
		buf.append(" }");
		return buf.toString();
	}

	/**
	 * Clones the rectangle.
	 */
	public Object clone() {
		return new Rectangle(this);
	}

	public boolean equals(Object object) {
		if (object instanceof Rectangle) {
			Rectangle rt = (Rectangle) object;
			return rt.x == x && rt.y == y &&
					rt.width == width && rt.height == height;
		} else {
			// TODO: support other rect types?
			return false;
		}
	}

	public Rectangle add(Point point) {
		return new Rectangle(x + point.x, y + point.y, width, height);
	}

	public Rectangle subtract(Point point) {
		return new Rectangle(x - point.x, y - point.y, width, height);
	}

	public Rectangle multiply(double value) {
		return new Rectangle(x, y, width * value, height * value);
	}

	public Rectangle multiply(Point point) {
		return new Rectangle(x, y, width * point.x, height * point.y);
	}

	public Rectangle divide(double value) {
		return new Rectangle(x, y, width / value, height / value);
	}

	/**
	 * Returns <code>true</code> if the rectangle is empty,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
	    return width <= 0 || height <= 0;
	}

	/**
	 * Tests if specified coordinates are inside the boundary of the rectangle.
	 * 
	 * @param x, y the coordinates to test
	 * @return <code>true</code> if the specified coordinates are inside the
	 *         boundary of the rectangle; <code>false</code> otherwise.
	 * 
	 * @jshide
	 */
	public boolean contains(double x, double y) {
		return x >= this.x &&
			y >= this.y &&
			x < this.x + width &&
			y < this.y + height;
	}

    /**
	 * Tests if the specified point is inside the boundary of the rectangle.
	 * 
	 * @param point the specified point
	 * @return <code>true</code> if the point is inside the rectangle's
	 *         boundary; <code>false</code> otherwise.
	 */
	public boolean contains(Point point) {
		return contains(point.x, point.y);
	}
	
    /**
	 * Tests if the interior of the rectangle entirely contains the specified
	 * rectangle.
	 * 
	 * @param rect The specified rectangle
	 * @return <code>true</code> if the rectangle entirely contains the
	 *         specified rectangle; <code>false</code> otherwise.
	 */
	public boolean contains(Rectangle rect) {
	return !isEmpty() 
			&& rect.width > 0 && rect.height > 0
			&& rect.x >= x && rect.y >= y
			&& rect.x + rect.width <= x + width
			&& rect.y + rect.height <= y + height;
	}

	/**
	 * Tests if the interior of this rectangle intersects the interior of
	 * another rectangle.
	 * 
	 * @param rect the specified rectangle
	 * @return <code>true</code> if the rectangle and the specified rectangle
	 *         intersect each other; <code>false</code> otherwise.
	 */
	public boolean intersects(Rectangle rect) {
		return !isEmpty() && rect.width > 0 && rect.height > 0 &&
			rect.x + rect.width > this.x &&
			rect.y + rect.height > this.y &&
			rect.x < this.x + this.width &&
			rect.y < this.y + this.height;
	}

	/**
	 * Returns a new rectangle representing the intersection of this rectangle
	 * with the specified rectangle.
	 * 
	 * @param rect The rectangle to be intersected with this rectangle.
	 * @return The largest rectangle contained in both the specified rectangle
	 *         and in this rectangle.
	 */
	public Rectangle intersect(Rectangle rect) {
		double x1 = Math.max(x, rect.x);
		double y1 = Math.max(y, rect.y);
		double x2 = Math.min(x + width, rect.x + rect.width);
		double y2 = Math.min(y + height, rect.y + rect.height);
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

	/**
	 * Returns a new rectangle representing the union of this rectangle with the
	 * specified rectangle.
	 * 
	 * @param rect the rectangle to be combined with this rectangle
	 * @return the smallest rectangle containing both the specified rectangle
	 *         and this rectangle.
	 */
	public Rectangle unite(Rectangle rect) {
		double x1 = Math.min(x, rect.x);
		double y1 = Math.min(y, rect.y);
		double x2 = Math.max(x + width, rect.x + rect.width);
		double y2 = Math.max(y + height, rect.y + rect.height);
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}

    /**
	 * Adds a point, specified by the arguments <code>x</code>
	 * and <code>y</code>, to the rectangle. The resulting rectangle is the
	 * smallest rectangle that contains both the original rectangle and the
	 * specified point.
	 * 
	 * After adding a point, a call to <code>contains</code> with the added
	 * point as an argument does not necessarily return <code>true</code>.
	 * The <code>contains</code> method does not return <code>true</code>
	 * for points on the right or bottom edges of a rectangle. Therefore, if the
	 * added point falls on the left or bottom edge of the enlarged rectangle,
	 * <code>contains</code> returns <code>false</code> for that point.
	 * 
	 * @param px the x coordinate of the point.
	 * @param py the y coordinate of the point.
	 * 
	 * @jshide
	 */
	public Rectangle unite(double px, double py) {
		double x1 = Math.min(x, px);
		double y1 = Math.min(y, py);
		double x2 = Math.max(x + width, px);
		double y2 = Math.max(y + height, py);
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}

    /**
	 * Adds a point to this rectangle. The resulting rectangle is the
	 * smallest rectangle that contains both the original rectangle and the
	 * specified point.
	 * 
	 * After adding a point, a call to <code>contains</code> with the added
	 * point as an argument does not necessarily return <code>true</code>.
	 * The <code>contains</code> method does not return <code>true</code>
	 * for points on the right or bottom edges of a rectangle. Therefore, if the
	 * added point falls on the left or bottom edge of the enlarged rectangle,
	 * <code>contains</code> returns <code>false</code> for that point.
	 * 
	 * @param point
	 */
	public Rectangle unite(Point point) {
		return unite(point.x, point.y);
	}

	/**
	 * @return
	 */
	protected Rectangle2D toRectangle2D() {
		return new Rectangle2D.Double(x, y, width, height);
	}
}
