package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("serial")
public class MyTasks extends Properties {
	
	public static final String TIME_SEPARATOR = "#";

	public MyTasks(String path) throws FileNotFoundException, IOException {

		this.load(new FileInputStream(path + File.separator + "task.properties"));
	}
	
	public MyTask getTask(String taskId) {
		
		String title = this.getProperty(taskId).split(TIME_SEPARATOR)[0];
		int time = Integer.valueOf(this.getProperty(taskId).split(TIME_SEPARATOR)[1]);
		
		return new MyTask(taskId, title, time);
	}
	

	public final class MyTask {

		private String id;
		private String title;
		private int time;

		public MyTask(String id, String title, int time) {

			this.id = id;
			this.title = title.trim();
			this.time = time;
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

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}
	}
}
