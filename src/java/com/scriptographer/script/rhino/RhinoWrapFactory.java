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
 * File created on Apr 10, 2007.
 *
 * $Id$
 */

package com.scriptographer.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.scriptographer.ai.Style;

/**
 * @author lehni
 *
 */
public class RhinoWrapFactory extends com.scratchdisk.script.rhino.RhinoWrapFactory {
	
	public Object wrap(Context cx, Scriptable scope, Object obj, Class staticType) {
		// By default, Rhino converts chars to integers. In Scriptographer,
		// we want a string of length 1:
        if (staticType == Character.TYPE)
            return obj.toString();
		return super.wrap(cx, scope, obj, staticType);
	}

	public Scriptable wrapCustom(Context cx, Scriptable scope,
			Object javaObj, Class staticType) {
		if (javaObj instanceof Style)
			return new StyleWrapper(scope, (Style) javaObj, staticType, true);
		return null;
	}
}
