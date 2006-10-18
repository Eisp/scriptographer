/*
 * Scriptographer
 * 
 * This file is part of Scriptographer, a Plugin for Adobe Illustrator.
 * 
 * Copyright (c) 2002-2006 Juerg Lehni, http://www.scratchdisk.com.
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
 * File created on 18.01.2005.
 * 
 * $RCSfile: ArtSet.java,v $
 * $Author: lehni $
 * $Revision: 1.12 $
 * $Date: 2006/10/18 14:17:43 $
 */

package com.scriptographer.ai;

import java.util.HashMap;

import com.scriptographer.util.ExtendedArrayList;
import com.scriptographer.util.ExtendedList;
import com.scriptographer.util.Lists;

public class ArtSet extends ExtendedArrayList {
	HashMap map;

	public ArtSet() {
		map = new HashMap();
	}

	public ArtSet(ExtendedList artObjects) {
		this();
		addAll(artObjects);
	}

	public ArtSet(Object[] artObjects) {
		this(Lists.asList(artObjects));
	}

	public Art getArt(int index) {
		return (Art) get(index);
	}

	/**
	 * Adds the art to the ArtSet, only if it does not already exist in it.
	 * @param index
	 * @param art
	 * @return true if the art was added to the set.
	 */
	public Object add(int index, Object art) {
		if (art instanceof Art) {
			if (map.get(art) == null) {
				if (super.add(index, art) != null) {
					map.put(art, art);
					return art;
				}
			}
		}
		return null;
	}

	public Object remove(int index) {
		Object obj = super.remove(index);
		if (obj != null)
			map.remove(obj);
		return obj;
	}

	public boolean contains(Object element) {
		return map.get(element) != null;
	}

	public native ArtSet invert();
	
	/**
	 * 
	 * @param type Color.TYPE_*
	 * @param resolution
	 * @param antialiasing
	 * @param width
	 * @param height
	 * @return
	 */
	public native Raster rasterize(int type, float resolution, int antialiasing, float width, float height);
	
	public Raster rasterize(int type, float resolution, int antialiasing) {
		return rasterize(type, resolution, antialiasing, -1, -1);
	}
	
	public Raster rasterize(int type) {
		return rasterize(type, 0, 4, -1, -1);
	}
	
	public Raster rasterize() {
		return rasterize(-1, 0, 4, -1, -1);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < getLength(); i++) {
			if (i > 0)
				buffer.append(", ");
			buffer.append(get(i).toString());
		}
		buffer.append("]");
		return buffer.toString();
	}
}
