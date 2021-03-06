package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("serial")
public class MyPhases extends Properties {

	public MyPhases(String path) throws FileNotFoundException, IOException {

		this.load(new FileInputStream(path + File.separator
				+ "phases.properties"));
	}

	public MyPhase getPhase(String phaseId) {

		return new MyPhase(phaseId, this.getProperty(phaseId));
	}

	public final class MyPhase {
		String id;
		String title;

		public MyPhase(String id, String title) {
			this.id = id;
			this.title = title;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
}