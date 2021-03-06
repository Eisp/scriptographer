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
 * File created on 04.11.2005.
 */

package com.scriptographer.ai;

import java.util.Iterator;

import com.scratchdisk.list.ExtendedList;
import com.scratchdisk.list.ListIterator;
import com.scratchdisk.list.Lists;
import com.scratchdisk.list.ReadOnlyList;
import com.scratchdisk.list.ReadOnlyStringIndexList;

/**
 * @author lehni
 */
public class FontFamily extends NativeObject implements ReadOnlyList<FontWeight>, ReadOnlyStringIndexList<FontWeight> {

	protected FontFamily(int handle) {
		super(handle);
	}

	private native String nativeGetName(int handle);	

	/**
	 * The name of the font family.
	 */
	public  String getName() {
		return nativeGetName(handle);
	}

	public boolean isValid() {
		return size() > 0;
	}

	/* TODO: check AIFont.h for many more features (OpenType, glyph bounds, etc)
	AIAPI AIErr (*GetFullFontName)( AIFontKey font, char *fontName, short maxName );
	AIAPI AIErr (*GetPostScriptFontName)( AIFontKey fontKey, char* postScriptFontName, short maxName );
	AIAPI AIErr (*GetFontStyleName)( AIFontKey font, char *styleName, short maxName );
	AIAPI AIErr (*GetFontFamilyUIName)( AIFontKey font, char *familyName, short maxName );
	AIAPI AIErr (*GetFontStyleUIName)( AIFontKey font, char *styleName, short maxName );
	AIAPI AIErr (*GetTypefaceName)( AITypefaceKey typeface, char *name, short maxName );
	AIAPI AIErr (*GetUserFontName)( AIFontKey font, char *userfontName, short maxName );
	AIAPI AIErr (*GetUserFontUIName)( AIFontKey font, char *userfontName, short maxName );
	AIAPI AIErr (*GetUserFontUINameUnicode)( AIFontKey fontKey, ASUnicode* userfontName, long maxName );
	AIAPI AIErr (*GetFontFamilyUINameUnicode)( AIFontKey fontKey, ASUnicode* familyName, long maxName );
	AIAPI AIErr (*GetFontStyleUINameUnicode)( AIFontKey fontKey, ASUnicode* styleName, long maxName );
	*/

	private native int nativeSize(int handle);

	public int size() {
		return nativeSize(handle);
	}

	private static native int nativeGet(int handle, int index);

	public FontWeight get(int index) {
		return FontWeight.wrapHandle(nativeGet(handle, index));
	}

	public FontWeight get(String name) {
		if (name != null) {
			for (int i = size() - 1; i >= 0; i--) {
				FontWeight weight = get(i);
				if (name.equalsIgnoreCase(weight.getName()))
					return weight;
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public ExtendedList<FontWeight> getSubList(int fromIndex, int toIndex) {
		return Lists.createSubList(this, fromIndex, toIndex);
	}

	public Iterator<FontWeight> iterator() {
		return new ListIterator<FontWeight>(this);
	}

	protected static FontFamily wrapHandle(int handle) {
		return (FontFamily) wrapHandle(FontFamily.class, handle);
	}

	public String toString() {
		return getName();
	}

	public FontWeight getFirst() {
		return size() > 0 ? get(0) : null;
	}

	public FontWeight getLast() {
		int size = size();
		return size > 0 ? get(size - 1) : null;
	}

	public Class<?> getComponentType() {
		return FontWeight.class;
	}
}
