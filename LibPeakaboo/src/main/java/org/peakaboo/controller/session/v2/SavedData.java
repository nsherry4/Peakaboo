package org.peakaboo.controller.session.v2;

import java.util.List;

public class SavedData{

	public List<Integer> discards;
	public List<String> files;
	public SavedPlugin datasource;
	public String title;
	
	public SavedData() {}
	
	public SavedData(List<Integer> discards, List<String> files, SavedPlugin datasource, String title) {
		this.discards = discards;
		this.files = files;
		this.datasource = datasource;
		this.title = title;
	}
}