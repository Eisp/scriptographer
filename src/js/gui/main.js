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

var mainDialog = new FloatingDialog('tabbed show-cycle resizing remember-placing', function() {
	if (app.macintosh) {
		function executeProcess() {
			if (arguments.length == 1) {
				var command = arguments[0];
			} else {
				var command = [];
				for (var i = 0; i < arguments.length; i++) {
					command.push(arguments[i]);
				}
			}
			var proccess = java.lang.Runtime.getRuntime().exec(command);
			var exitValue = proccess.waitFor();

			function readStream(stream) {
				var reader = new java.io.BufferedReader(new java.io.InputStreamReader(stream));
				var res = [], line, first = true;
				while ((line = reader.readLine()) != null) {
					if (first) first = false;
					else res.push(lineBreak);
					res.push(line);
				}
				return res.join('');
			}

			return {
				command: command,
				output: readStream(proccess.getInputStream()),
				error: readStream(proccess.getErrorStream()),
				exitValue: exitValue
			};
		}
	}

	var lineHeight = 17;

	// Script List:
	scriptList = new HierarchyList(this) {
		style: 'black-rect',
		size: [208, 20 * lineHeight],
		minimumSize: [208, 8 * lineHeight],
		entrySize: [2000, lineHeight],
		entryTextRect: [0, 0, 2000, lineHeight],
		// TODO: consider adding onDoubleClick, instead of this nasty workaround here!
		// Avoid onTrack as much as possible in scripts, and add what's needed behind
		// the scenes
		onTrackEntry: function(tracker, entry) {
			if (tracker.action == Tracker.ACTION_BUTTON_UP &&
					(tracker.modifiers & Tracker.MODIFIER_DOUBLE_CLICK) &&
					tracker.point.x > entry.expandArrowRect.right) {
				if (entry.directory) {
					entry.expanded = !entry.expanded;
					entry.list.invalidate();
				} else {
					app.launch(entry.file);
					// execute();
				}
			}
		}
	};

	// Filter for hiding files:
	var scriptFilter = new java.io.FilenameFilter() {
		accept: function(dir, name) {
			return !/^__|^\.|^libraries$|^CVS$/.test(name) && 
				(/\.(?:js|rb|py)$/.test(name) || new File(dir, name).isDirectory());
		}
	};

	var scriptImage = getImage('script.png');
	var folderImage = getImage('folder.png');

	var directoryEntries = {};
	var fileEntries = {};

	function addFile(list, file, selected) {
		var entry = new HierarchyListEntry(list) {
			text: file.name,
			selected: selected && selected[file] || selected == true,
			// backgroundColor: 'background',
			file: file,
			directory: file.isDirectory()
		};
		if (entry.directory) {
			addFiles(entry.createChildList(), file, selected);
			entry.expanded = false;
			entry.image = folderImage;
			directoryEntries[file] = entry;
		} else {
			entry.image = scriptImage;
			fileEntries[file] = entry;
		}
		return entry;
	}

	function addFiles(list, dir, selected) {
		var files = dir.listFiles(scriptFilter);
		for (var i = 0; i < files.length; i++) {
			addFile(list, files[i], selected);
		}
	}

	function removeFiles() {
		scriptList.removeAll();
		directoryEntries = {};
		fileEntries = {};
	}

    function createFile() {
        var entry = scriptList.activeLeaf;
        var list;
        if (entry) {
            if (entry.directory) {
				list = entry.childList;
            } else {
                list = entry.list;
                entry = list.parentEntry;
            }
        } else list = scriptList;
        // If we're at root, entry is null:
        var dir = entry ? entry.file : scriptographer.scriptDirectory;
        if (dir) {
            // Find a non existing filename:
            var file;
            for (var i = 1;;i++) {
                file = new File(dir, 'Untitled ' + i + '.js');
                if (!file.exists())
                    break;
            }
            file = Dialog.fileSave('Create A New Script:', [
                'JavaScript Files (*.js)', '*.js',
                'All Files', '*.*'
			], file);
               // Add it to the list as well:
            if (file && file.createNewFile())
				addFile(list, file, true);
        }
    }

	function refreshFiles() {
		// Collect expanded items:
		var expandedDirs = {}, selected = {};
		for (file in directoryEntries) {
			var entry = directoryEntries[file];
			if (entry.directory && entry.expanded)
				expandedDirs[file] = true;
		}
		// Create a lookup table for all selected files, so they
		// can be selected again in addFiles
		var sel = scriptList.selectedEntries;
		for (var i = 0; i < sel.length; i++)
			selected[sel[i].file] = true;
		removeFiles();
		addFiles(scriptList, scriptographer.scriptDirectory, selected);
		// Now restore the expanded state:
		for (file in expandedDirs) {
			var entry = directoryEntries[file];
			if (entry) entry.expanded = true;
		}
		tool1Button.setCurrentImage();
		tool2Button.setCurrentImage();
	}

	function execute() {
		var entry = scriptList.activeLeaf;
		if (entry && entry.file)
			ScriptographerEngine.execute(entry.file, null);
	}
	
	// Add the menus:
	var scriptographerGroup = new MenuGroup(MenuGroup.GROUP_TOOL_PALETTES, MenuGroup.OPTION_ADD_ABOVE | MenuGroup.OPTION_SEPARATOR_ABOVE);

	var scriptographerItem = new MenuItem(scriptographerGroup) {
		text: 'Scriptographer'
	};

// 	var separator = new MenuGroup(scriptographerGroup, MenuGroup.OPTION_ADD_ABOVE);

	new MenuItem(scriptographerItem) {
		onSelect: function() {
			mainDialog.visible = !mainDialog.visible;
		},
		onUpdate: function() {
			this.text = (mainDialog.visible ? 'Hide' : 'Show') + ' Main Palette';
		}
	};

	new MenuItem(scriptographerItem) {
		onSelect: function() {
			consoleDialog.visible = !consoleDialog.visible;
		},
		onUpdate: function() {
			this.text = (consoleDialog.visible ? 'Hide' : 'Show') + ' Console Palette';
		}
	};

	new MenuItem(scriptographerItem) {
		text: 'About...',
		onSelect: function() {
			aboutDialog.doModal();
		}
	};

	new MenuItem(scriptographerItem) {
		separator: true
	};

	new MenuItem(scriptographerItem) {
		text: 'Reload',
		onSelect: function() {
			ScriptographerEngine.reload();
		}
	};

	// Add the popup menu
	var menu = this.popupMenu;

	var executeEntry = new ListEntry(menu) {
		text: 'Execute Script',
		onSelect: function() {
			execute();
		}
	};

	var refreshEntry = new ListEntry(menu) {
		text: 'Refresh List',
		onSelect: function() {
			refreshFiles();
		}
	};

	var consoleEntry = new ListEntry(menu) {
		text: 'Show / Hide Console',
		onSelect: function() {
			consoleDialog.visible = !consoleDialog.visible;
		}
	};

	var scriptDirEntry = new ListEntry(menu) {
		text: 'Set Script Directory...',
		onSelect: function() {
			if (chooseScriptDirectory())
				refreshFiles();
		}
	};

	var aboutEntry = new ListEntry(menu) {
		text: 'About Scriptographer...',
		onSelect: function() {
			aboutDialog.doModal();
		}
	};

	var referenceEntry = new ListEntry(menu) {
		text: 'Reference...',
		onSelect: function() {
			app.launch('file://' + new File(scriptographer.pluginDirectory, 'doc/index.html'));
		}
	};

	var separatorEntry = new ListEntry(menu) {
		separator: true
	};

	var reloadEntry = new ListEntry(menu) {
		text: 'Reload',
		onSelect: function() {
			ScriptographerEngine.reload();
		}
	};

	// Buttons:
	var playButton = new ImageButton(this) {
		onClick: function() {
			execute();
		},
		image: getImage('play.png'),
		size: buttonSize
	};

	var stopButton = new ImageButton(this) {
		onClick: function() {
			ScriptographerEngine.stopAll();
		},
		image: getImage('stop.png'),
		size: buttonSize
	};

	var refreshButton = new ImageButton(this) {
		onClick: function() {
			refreshFiles();
		},
		image: getImage('refresh.png'),
		size: buttonSize
	};

	var consoleButton = new ImageButton(this) {
		onClick: function() {
			consoleDialog.setVisible(!consoleDialog.isVisible());
		},
		image: getImage('console.png'),
		size: buttonSize
	};

	var newButton = new ImageButton(this) {
		onClick: function() {
			createFile();
		},
		image: getImage('script.png'),
		size: buttonSize
	};

	var tool1Button = new ImageButton(this) {
		image: getImage('tool1.png'),
		size: buttonSize,
		toolIndex: 0,
		entryImage: getImage('tool1script.png')
	};

	var tool2Button = new ImageButton(this) {
		image: getImage('tool2.png'),
		size: buttonSize,
		toolIndex: 1,
		entryImage: getImage('tool2script.png')
	};

	tool1Button.onClick = tool2Button.onClick = function() {
		var entry = scriptList.activeLeaf;
		if (entry && entry.file) {
			Tool.getTool(this.toolIndex).compileScript(entry.file);
			if (entry.file != this.curFile) {
				this.setCurrentImage(scriptImage);
				entry.image = this.entryImage;
				this.curFile = entry.file;
			}
		}
	}

	tool1Button.setCurrentImage = tool2Button.setCurrentImage = function(image) {
		var curEntry = fileEntries[this.curFile];
		if (curEntry)
			curEntry.image = image ? image : this.entryImage;
	}

	if (scriptographer.scriptDirectory)
		addFiles(scriptList, scriptographer.scriptDirectory);

	return {
		title: 'Scriptographer',
		margin: [0, -1, -1, -1],
		content: {
			center: scriptList,
			south: new ItemGroup(this) {
				layout: [ 'left', -1, -1 ],
				content: [
					playButton,
					stopButton,
					new Spacer(4, 0),
					refreshButton,
					new Spacer(4, 0),
					newButton,
					consoleButton,
					new Spacer(4, 0),
					tool1Button,
					tool2Button
				]
			}
		}
	};
});
