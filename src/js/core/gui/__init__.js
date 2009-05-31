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
 * $Id$
 */

importPackage(Packages.com.scriptographer);
importPackage(Packages.com.scratchdisk.script);
importPackage(Packages.com.scriptographer.script);

script.showProgress = false;

// Load the core libraries
loadLibraries(new File(script.directory.parentFile, 'lib'));

var buttonSize = new Size(27, 17);
var lineHeight = 17;
var lineBreak = java.lang.System.getProperty('line.separator');

function getImage(filename) {
	return new Image(new File(script.directory, 'resources/' + filename));
}

function loadLibraries(dir) {
	var files = dir.listFiles();
	if (files) {
		for (var i = 0; i < files.length; i++) {
			var file = files[i];
			if (file.isDirectory() && !/^\.|^CVS$/.test(file.name)) {
				loadLibraries(file);
			} else {
				try {
					var engine = ScriptEngine.getEngineByFile(file);
					if (engine) {
						var scrpt = engine.compile(file);
						// Don't call scrpt.execute directly, since we handle
						// SG specific things in ScriptographerEngine.execute:
						if (scrpt)
							ScriptographerEngine.execute(scrpt, file, engine.globalScope);
					}
				} catch (e) {
					print(e);
				}
			}
		}
	}
}

function chooseScriptDirectory(dir) {
	dir = Dialog.chooseDirectory(
		'Please choose the Scriptographer script directory',
		dir || scriptographer.scriptDirectory || scriptographer.pluginDirectory);
	if (dir && dir.isDirectory()) {
		script.preferences.scriptDirectory = dir.path;
		setScriptDirectory(dir);
		return true;
	}
}

function setScriptDirectory(dir) {
	// Tell Scriptographer about where to look for scripts.
	ScriptographerEngine.scriptDirectory = dir;
	// Load librarires
	loadLibraries(new File(dir, 'libraries'));
}

var tool = new Tool("Scriptographer Tool") {
	image: getImage('tool.png'),
	activeImage: getImage('tool-active.png'),
	tooltip: 'Execute a tool script to assign it with this tool button'
};

if (!script.preferences.accepted) {
	include('license.js');
	script.preferences.accepted = licenseDialog.doModal() == licenseDialog.defaultItem;
}

if (script.preferences.accepted) {
	// Read the script directory first, or ask for it if its not defined:
	var dir = script.preferences.scriptDirectory;
	// If no script directory is defined, try the default place for Scripts:
	// The subdirectory 'scripts' in the plugin directory:
	dir = dir
		? new File(dir)
		: new File(scriptographer.pluginDirectory, 'scripts');
	if (!dir.exists() || !dir.isDirectory()) {
		if (!chooseScriptDirectory(dir))
			Dialog.alert('Could not find Scriptographer script directory.');
	} else {
		setScriptDirectory(dir);
	}

	include('console.js');
	include('about.js');
	include('main.js');

	if (!script.preferences.installed) {
		// include('install.js');
		script.preferences.installed = true;
	}
}