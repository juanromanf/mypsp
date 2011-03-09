package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("serial")
public class MyDefects extends Properties {

	public MyDefects(String path) throws FileNotFoundException, IOException {

		this.load(new FileInputStream(path + File.separator
				+ "checklist.properties"));
	}

	public MyDefect getDefect(String defectId) {

		return new MyDefect(defectId, this.getProperty(defectId));
	}

	public final class MyDefect {
		String id;
		String description;

		public MyDefect(String id, String desc) {
			this.id = id;
			this.description = desc;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String desc) {
			this.description = desc;
		}
	}
}
