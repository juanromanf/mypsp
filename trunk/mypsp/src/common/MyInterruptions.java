package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("serial")
public class MyInterruptions extends Properties {

	public MyInterruptions(String path) throws FileNotFoundException,
			IOException {

		this.load(new FileInputStream(path + File.separator + "task.properties"));
	}
	
	public MyInterruption getInterruption(String interruptionId) {
		
		return new MyInterruption(interruptionId, this.getProperty(interruptionId));
	}

	public final class MyInterruption {
		String id;
		String title;

		public MyInterruption(String id, String title) {
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
