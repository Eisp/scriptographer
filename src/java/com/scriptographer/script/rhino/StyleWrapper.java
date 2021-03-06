/*
 * Scriptographer
 *
 * This file is part of Scriptographer, a Scripting Plugin for Adobe Illustrator
 * http://scriptographer.org/
 *
 * Copyright (c) 2002-2010, Juerg Lehni
 * http://scratchdisk.com/
 *
 * All rights reserved. See LICENSE file for details.
 * 
 * File created on 03.11.2005.
 */

package com.scriptographer.script.rhino;

import java.util.Arrays;
import java.util.HashSet;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

import com.scratchdisk.script.rhino.ExtendedJavaObject;
import com.scriptographer.ai.Color;
import com.scriptographer.ai.FontWeight;
import com.scriptographer.ai.Item;
import com.scriptographer.ai.Style;

/**
 * Create a simple wrapper that converts null to Undefined in both ways, and
 * null to Color.NONE in case of color
 * 
 * In Java, null means undefined, while in javascript null is e.g. Color.NONE
 * This is more intuitive...
 * 
 * This wrapper is used by PathStyle, FillStyle, StrokeStyle, ParagraphStyle
 * 
 * @author lehni
 */
public class StyleWrapper extends ExtendedJavaObject {

	/**
	 * The fields that return color values.
	 */
	static HashSet<String> colorFields = new HashSet<String>(Arrays.asList(new String[] {
			"color", // For FillStyle / StrokeStyle
			"strokeColor", // For PathStyle / Item
			"fillColor" // For PathStyle / Item
	}));

	/**
	 * The fields that return font values.
	 */
	static HashSet<String> fontFields = new HashSet<String>(Arrays.asList(new String[] {
			"font"
	}));

	/**
	 * The fields in ai.Item that return style values.
	 */
	static HashSet<String> itemFields = new HashSet<String>(Arrays.asList(new String[] {
			"fillColor",
			"overprint",
			"strokeColor",
			"strokeOverprint",
			"strokeWidth",
			"strokeCap",
			"strokeJoin",
			"miterLimit",
			"dashOffset",
			"dashArray",
			"windingRule",
			"resolution",
	}));

	StyleWrapper(Scriptable scope, Style javaObject,
			Class staticType, boolean sealed) {
		super(scope, javaObject, staticType, sealed);
	}

	public void put(String name, Scriptable start, Object value) {
		if (!(javaObject instanceof Item) || itemFields.contains(name)) {
			if (value == Undefined.instance) {
				value = null;
			} else if (value == null) {
				if (colorFields.contains(name))
					value = Color.NONE;
				else if (fontFields.contains(name))
					value = FontWeight.NONE;
			}
		}
		super.put(name, start, value);
	}

	public Object get(String name, Scriptable start) {
		Object value = super.get(name, start);
		if (!(javaObject instanceof Item) || itemFields.contains(name)) {
			// Convert back
			if (value == null) {
				value = Undefined.instance;
			} else if (value instanceof Wrapper) {
				Object obj = ((Wrapper) value).unwrap();
				if (colorFields.contains(name) && obj == Color.NONE
						|| fontFields.contains(name) && obj == FontWeight.NONE)
					value = null;
			}
		}
		return value;
	}
}
