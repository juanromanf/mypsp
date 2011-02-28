package common;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;

import annotation.Loc;

public class DirectoryHelper {

	public List<File> findClasses(File directory) {

		String[] extensions = { "class" };
		return (List<File>) FileUtils.listFiles(directory, extensions, true);
	}

	public List<File> findSources(File directory) {

		String[] extensions = { "java" };
		return (List<File>) FileUtils.listFiles(directory, extensions, true);
	}	

	@Loc(size = 21)
	public boolean compileSources(List<File> sourceFileList, String destinationDir) {

		boolean result = false;
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
			
			String[] options = new String[]{"-d", destinationDir};

			CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList(options), null, compilationUnits);
			result = task.call();

			fileManager.close();
			
		} catch (NullPointerException e) {
			System.out.println("Imposible ejecutar tarea de compilacion, utilize Java JDK !!");
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
