/*
 * Scriptographer
 * 
 * This file is part of Scriptographer, a Plugin for Adobe Illustrator.
 * 
 * Copyright (c) 2002-2010 Juerg Lehni, http://www.scratchdisk.com.
 * All rights reserved.
 *
 * Please visit http://scriptographer.org/ for updates and contact.
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
 * File created on 08.12.2006.
 * 
 * $Id$
 */

package com.scratchdisk.util;

/**
 * @author lehni 
 */
public class StringUtils {

	private StringUtils() {
	}

	public static String replace(String str, String find, String replace) {
		int pos = str.indexOf(find);
		if (pos == -1)
			return str;

		int next = 0;
		StringBuffer buf = new StringBuffer(str.length() + replace.length());
		do {
			buf.append(str.substring(next, pos));
			buf.append(replace);
			next = pos + find.length();
		} while ((pos = str.indexOf(find, next)) != -1);

		if (next < str.length())
			buf.append(str.substring(next, str.length()));

		return buf.toString();
	}

	public static String capitalize(String str, String delimiter) {
		String[] parts = str.split("\\s");
		StringBuffer res = new StringBuffer();
		for (int i = 0, l = parts.length; i < l; i++) {
			if (i > 0)
				res.append(delimiter);
			String part = parts[i];
			res.append(Character.toUpperCase(part.charAt(0)));
			res.append(part, 1, part.length());
		}
		return res.toString();
	}

	public static String capitalize(String str) {
		return capitalize(str, "");
	}

	public static String join(Object[] parts, String separator) {
		if (parts == null)
			return null;
		int length = parts.length;
		if (length == 0)
			return "";
		StringBuffer res = new StringBuffer((parts[0] == null
				? 16 : parts[0].toString().length()) * length);
		for (int i = 0; i < length; i++) {
			if (i > 0)
				res.append(separator);
			if (parts[i] != null)
				res.append(parts[i]);
		}
		return res.toString();

	}
}
