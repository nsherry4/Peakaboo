#!/bin/sh

mkdir ./16/
mkdir ./24/
mkdir ./32/
mkdir ./48/

#ok/cancel
./harvest.rb elementary-xfce/actions dialog-apply choose-ok
./harvest.rb elementary/actions process-stop choose-cancel




#mime types
./harvest.rb elementary/mimes application-pdf mime-pdf
./harvest.rb elementary/mimes x-office-drawing mime-svg
./harvest.rb elementary/mimes text-x-generic mime-text
./harvest.rb elementary/mimes image mime-raster

#actions
./harvest.rb elementary/actions view-refresh action-refresh

#undo/redo
./harvest.rb elementary/actions edit-undo
./harvest.rb elementary/actions edit-redo

#cut/copy/paste
#Elememtary doesn't seem to have large cut icons, so we do a gnome fallback first
./harvestOther.rb /usr/share/icons/gnome actions edit-cut
./harvest.rb elementary/actions edit-cut
./harvest.rb elementary/actions edit-copy
./harvest.rb elementary/actions edit-paste

#zoom controls
#Elementary doesn't seem to have large zoom icons, so we do a gnome fallback first
./harvestOther.rb /usr/share/icons/gnome actions zoom-in
./harvestOther.rb /usr/share/icons/gnome actions zoom-out
./harvestOther.rb /usr/share/icons/gnome actions zoom-original
./harvestOther.rb /usr/share/icons/gnome actions zoom-best-fit

./harvest.rb elementary/actions zoom-in
./harvest.rb elementary/actions zoom-out
./harvest.rb elementary/actions zoom-original
./harvest.rb elementary/actions zoom-fit-best zoom-best-fit


#list/edit controls
./harvest.rb elementary/actions list-add edit-add
./harvest.rb elementary/actions edit-delete
./harvest.rb elementary/actions list-remove edit-remove
./harvest.rb elementary/actions edit-clear
./harvest.rb elementary/actions edit edit-edit

#selection controls
./harvest.rb elementary/actions selection-checked selection-all
./harvest.rb elementary/actions selection-remove selection-none

./harvest.rb custom/actions object-flip-vertical
./harvest.rb elementary/actions object-flip-horizontal
./harvest.rb elementary/actions object-inverse
./harvest.rb elementary/actions object-rotate-left
./harvest.rb elementary/actions object-rotate-right

#directional controls
./harvest.rb elementary/actions go-up
./harvest.rb elementary/actions go-down
./harvest.rb elementary/actions go-next
./harvest.rb elementary/actions go-previous
./harvest.rb elementary/actions go-top
./harvest.rb elementary/actions go-bottom
./harvest.rb elementary/actions go-first
./harvest.rb elementary/actions go-last

#document operations
./harvest.rb elementary/actions document-open
./harvest.rb elementary/actions document-save
#Elememtary doesn't seem to have large save-as icons, so we do a gnome fallback first
./harvestOther.rb /usr/share/icons/gnome actions document-save-as
./harvest.rb elementary/actions document-save-as
./harvest.rb elementary/actions document-new
./harvest.rb elementary/actions document-import
./harvest.rb elementary/actions document-export
./harvest.rb custom/actions document-export-archive

#window/tab operations
./harvest.rb elementary/actions window-close window-close
./harvest.rb elementary-xfce/actions window-new window-new
./harvest.rb elementary-xfce/actions tab-new window-tab-new

#badges
./harvest.rb elementary/status dialog-information badge-info
./harvest.rb elementary/status dialog-warning badge-warning
./harvest.rb elementary/actions help-contents badge-help
./harvest.rb elementary/status dialog-error badge-error


#find/replace
./harvest.rb elementary/actions edit-find find
./harvest.rb elementary/actions edit-find-replace find-replace
./harvest.rb elementary/actions edit-select-all find-select-all

#devices
./harvest.rb elementary/devices camera-photo device-camera
./harvest.rb elementary/devices drive-harddisk device-harddisk
./harvest.rb elementary/devices video-display device-monitor
./harvest.rb elementary/devices video-display device-computer
./harvest.rb elementary/devices printer device-printer

#places
./harvest.rb elementary-xfce/places user-desktop place-desktop
./harvest.rb elementary/places folder place-folder
./harvest.rb elementary/actions folder-new place-folder-new
./harvest.rb elementary/places folder-drag-accept place-folder-open
./harvest.rb elementary/places folder-remote place-remote
./harvest.rb elementary/places user-trash place-trash
./harvest.rb elementary/places user-home place-home

#misc
./harvest.rb elementary/actions help-about misc-about
./harvest.rb elementary/apps preferences-desktop misc-preferences
./harvest.rb elementary/actions document-properties misc-properties
./harvest.rb elementary/apps application-default-icon misc-executable
./harvest.rb elementary/status locked misc-locked

#sort
./harvestOther.rb /usr/share/icons/gnome actions view-sort-ascending edit-sort-asc
./harvestOther.rb /usr/share/icons/gnome actions view-sort-descending edit-sort-des

