package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import annotation.Interruptions;
import annotation.Loc;
import annotation.LogInt;
import annotation.LogT;
import annotation.Plan;
import annotation.Tasks;

import common.AnnotationStatistics;
import common.DirectoryHelper;
import common.MyInterruptions;
import common.MyTasks;
import common.SimpleCommandLineParser;

@Plan(time = 694, size = 190)
@Tasks({
	@LogT(date = "2011/02/20", taskId = 1, time = 54),
	@LogT(date = "2011/02/20", taskId = 2, time = 300),
	@LogT(date = "2011/02/20", taskId = 3, time = 10),
	@LogT(date = "2011/02/21", taskId = 4, time = 5),
	@LogT(date = "2011/02/21", taskId = 5, time = 33),
	@LogT(date = "2011/02/21", taskId = 6, time = 10),
	@LogT(date = "2011/02/21", taskId = 7, time = 5),
	@LogT(date = "2011/02/21", taskId = 8, time = 126),
	@LogT(date = "2011/02/21", taskId = 9, time = 68),
	@LogT(date = "2011/02/21", taskId = 999, time = 60)
})
	
@Interruptions({ 
	@LogInt(date = "2011/02/20", intId = 1, time = 30),
	@LogInt(date = "2011/02/20", intId = 2, time = 180),
	@LogInt(date = "2011/02/21", intId = 1, time = 10)
})
public class MyPSP {

	private MyTasks tasks;
	private MyInterruptions interruptions;
	private DirectoryHelper directoryHelper;
	private AnnotationStatistics statistics;

	public MyPSP(String path) throws FileNotFoundException, IOException {

		this.tasks = new MyTasks(path);
		this.interruptions = new MyInterruptions(path);
		this.directoryHelper = new DirectoryHelper();
		this.statistics = new AnnotationStatistics();
	}

	@SuppressWarnings("rawtypes")
	@Loc(size = 49)
	public void scanDirectory(String path) {
		
		if (! path.endsWith(File.separator)) path = path.concat(File.separator);
		
		System.out.println("Escaneando directorio "+ path);
		
		List<File> sourceFileList = directoryHelper.findSources(new File(path));
		
		String compileDestination = path + ".." + File.separator + "bin" + File.separator;
		boolean result = directoryHelper.compileSources(sourceFileList, compileDestination);
		
		if(result) {
			System.out.println("Procesando clases...");
			
			List<File> classesList = directoryHelper.findClasses(new File(compileDestination));
			
			for(File classFile: classesList) {
				String className = classFile.getPath();
				className = className.replace(path, "").replace(".." + File.separator + "bin" + File.separator, "").replace(".class", "").replace(File.separator, ".");
				
				try {
					URL[] urls = new URL[] { new URL( "file://"+ compileDestination) };
					URLClassLoader ucl = new URLClassLoader(urls);
					
					Class _class = ucl.loadClass(className);
				
					System.out.println("Procesando " + className);
					if (!_class.isInterface()) {						
						
						processAnnotations(_class);
					}

				} catch (ExceptionInInitializerError e) {
					e.printStackTrace();
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			
			printStatistics();
			
		} else {
			System.err.println("Imposible compilar fuentes !");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Loc(size = 42)
	public void processAnnotations(Class cls) {
		
		List<Annotation> annotations = new ArrayList<Annotation>();
		
		// add class annotations
		annotations.addAll( Arrays.asList(cls.getAnnotations()) );
		
		// add each method annotations
		for (Method m : cls.getMethods()) {			
			annotations.addAll( Arrays.asList(m.getAnnotations()) );		
		}
		
		for (Annotation a : annotations) {
			
			if (a.annotationType().getName().endsWith("Tasks")) {
				Tasks tasks = (Tasks) a;
				for (int i = 0; i < tasks.value().length; i++) {
					statistics.addLogT(tasks.value()[i]);
				}
			}			
			if (a.annotationType().getName().endsWith("Interruptions")) {
				Interruptions interruptions = (Interruptions) a;
				for (int i = 0; i < interruptions.value().length; i++) {
					statistics.addLogInt(interruptions.value()[i]);
				}
			}
			if (a.annotationType().getName().endsWith("LogT")) {
				statistics.addLogT((LogT) a);
			}				
			if (a.annotationType().getName().endsWith("LogInt")) {
				statistics.addLogInt((LogInt) a);
			}				
			if (a.annotationType().getName().endsWith("Loc")) {
				statistics.addLoc((Loc) a);
			}
			if (a.annotationType().getName().endsWith("Plan")) {
				statistics.setPlan((Plan) a);
			}
		}
	}
	
	@Loc(size = 28)
	public void printStatistics() {
		
		System.out.println("");
		System.out.println("                MyPSP                  ");
		System.out.println("_______________________________________");
		System.out.println(" - Numero @LogT          : "+ statistics.getLogTList().size());
		System.out.println(" - Numero @LogInt        : "+ statistics.getLogIntList().size());
		System.out.println(" - Numero @Loc           : "+ statistics.getLocList().size());
		System.out.println("_______________________________________");
		System.out.println(" - Tiempo Planeado       : "+ statistics.getPlannedTime());
		System.out.println(" - LOCs Estimadas        : "+ statistics.getPlan().size());
		System.out.println(" - Tiempo Total          : "+ statistics.getLogTSum());
		System.out.println(" - Tiempo Interrupciones : "+ statistics.getLogIntSum());
		System.out.println("_______________________________________");
		System.out.println(" - LOCs Totales          : "+ statistics.getLocSum());
		System.out.println(" - Productividad         : "+ statistics.getRealTime());
		System.out.println("");
	}

	/**
	 * MyPSP Main
	 * @param args
	 */
	@Loc(size = 30)
	public static void main(String[] args) {
		
		System.out.println("Iniciando MyPSP...");

		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String propertiesDir = "./";

		if (parser.containsKey("p")) {
			propertiesDir = parser.getValue("p");
		}

		try {
			MyPSP app = new MyPSP(propertiesDir);

			if (parser.containsKey("d")) {
				// Scan annotations in directory
				String path = parser.getValue("d");
				app.scanDirectory(path);
			}
			System.out.println("Finalizado...");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
