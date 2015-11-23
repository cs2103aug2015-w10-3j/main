//@@author A0110917M
package cs2103;

public class Settings {
	
	private static final String DEFAULT_DATA_FILE = "data.txt";

	private String dataFileUrl = DEFAULT_DATA_FILE;

	public Settings() {
		
	}

	public String getDataFileUrl() {
		return dataFileUrl;
	}

	public void setDataFileUrl(String newUrl) {
		dataFileUrl = newUrl;
	}

}
