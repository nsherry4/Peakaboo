package org.peakaboo.framework.stratus.api.icons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public enum StockIcon implements IconSet {

	CHOOSE_OK,
	CHOOSE_CANCEL,
	
	ACTION_REFRESH_SYMBOLIC,
	
	MENU_MAIN,
	MENU_SETTINGS,
	MENU_DISPLAY,
	
	BADGE_INFO,
	BADGE_WARNING,
	BADGE_QUESTION,
	BADGE_ERROR,
	
	DEVICE_HARDDISK,
	DEVICE_HARDDISK_SYMBOLIC,
	
	DOCUMENT_EXPORT,
	DOCUMENT_IMPORT,
	DOCUMENT_OPEN,
	DOCUMENT_SAVE_AS,
	DOCUMENT_SAVE,

	DOCUMENT_EXPORT_SYMBOLIC,
	DOCUMENT_EXPORT_ARCHIVE_SYMBOLIC,
	DOCUMENT_IMPORT_SYMBOLIC,
	DOCUMENT_NEW_SYMBOLIC,
	DOCUMENT_OPEN_SYMBOLIC,
	DOCUMENT_SAVE_AS_SYMBOLIC,
	DOCUMENT_SAVE_SYMBOLIC,
	
	EDIT_CUT,
	EDIT_COPY,
	EDIT_PASTE,
	EDIT_ADD,
	EDIT_CLEAR,
	EDIT_REMOVE,
	EDIT_DELETE,
	EDIT_REDO,
	EDIT_UNDO,
	EDIT_EDIT,
	EDIT_SORT_ASC,
	EDIT_SORT_DES,
	
	SELECTION_ALL,
	SELECTION_NONE,
	
	OBJECT_FLIP_VERTICAL,
	OBJECT_FLIP_HORIZONTAL,
	OBJECT_INVERSE,
	OBJECT_ROTATE_LEFT,
	OBJECT_ROTATE_RIGHT,
	
	FIND,
	FIND_REPLACE,
	FIND_SELECT_ALL,
	
	GO_BOTTOM,
	GO_DOWN,
	GO_FIRST,
	GO_LAST,
	GO_NEXT,
	GO_PREVIOUS,
	GO_TOP,
	GO_UP,
	
	MIME_PDF,
	MIME_RASTER,
	MIME_RASTER_SYMBOLIC,
	MIME_SVG,
	MIME_TEXT,
	MIME_VIDEO,
	MIME_AUDIO,
	MIME_SPREADSHEET,
	MIME_PRESENTATION,
	MIME_DOCUMENT,
	MIME_ARCHIVE,
	MIME_HTML,
	MIME_FILE,
	
	APP_ABOUT,
	APP_HELP,
	
	
	MISC_EXECUTABLE,
	MISC_LOCKED,
	MISC_PLUGIN,
	
	PLACE_DESKTOP,
	PLACE_HOME,
	PLACE_FOLDER,
	
	PLACE_DESKTOP_SYMBOLIC,
	PLACE_HOME_SYMBOLIC,
	PLACE_FOLDER_SYMBOLIC,
	PLACE_FOLDER_NEW_SYMBOLIC,

	WINDOW_CLOSE,
	WINDOW_TAB_NEW,
	
	ZOOM_BEST_FIT,
	ZOOM_IN,
	ZOOM_ORIGINAL,
	ZOOM_OUT, 
	
	PROCESS_COMPLETED,
	PROCESS_ERROR,
	
	;
	
	public static final String PATH = "/stratus/icons/";

	@Override
	public String path() {
		return PATH;
	}
	
	public static StockIcon fromMimeType(File file) {

		if (file.isDirectory()) {
			return PLACE_FOLDER;
		}
		
		try {
			String mime = Files.probeContentType(file.toPath());
			if (mime == null) { mime = "text/plain"; }
			StockIcon stock;
			
			stock = switch (mime) {
				case "text/plain", "application/json" -> MIME_TEXT;
				case "image/jpeg", "image/png" -> MIME_RASTER;
				case "image/svg+xml" -> MIME_SVG;
				case "application/zip", "application/gzip", "application/x-tar", "application/bz2", "application/x-gtar-compressed", "application/java-archive" -> MIME_ARCHIVE;
				case "application/pdf" -> MIME_PDF;
				case "application/xml", "text/html" -> MIME_HTML;
				case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.spreadsheet", "text/csv" -> MIME_SPREADSHEET;
				case "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.oasis.opendocument.presentation" -> MIME_PRESENTATION;
				case "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> MIME_DOCUMENT;
				default -> null;
			};
			if (stock != null) return stock;
			
			String category = mime.split("/")[0];
			
			stock = switch (category) {
				case "image" -> MIME_RASTER;
				case "video" -> MIME_VIDEO;
				case "audio" -> MIME_AUDIO;
				case "text" -> MIME_TEXT;
				default -> null;
			};
			if (stock != null) return stock;
						
			return MIME_FILE;
		} catch (IOException e) {
			return MIME_FILE;
		}

	}
	
	public static void main(String args[]) {
		for (StockIcon icon : StockIcon.values()) {
			for (IconSize size : IconSize.values()) {
				icon.toImageIcon(size);
			}
		}
	}
	
		
}
