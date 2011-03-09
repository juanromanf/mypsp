package common;

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

import annotation.Defects;
import annotation.Interruptions;
import annotation.Loc;
import annotation.LogInt;
import annotation.LogT;
import annotation.PhaseDefects;
import annotation.Plan;
import annotation.Tasks;

/**
 * Clase encargada de procesar las anotaciones.
 * 
 * @author Juan Carlos Roman
 */
public class AnnotationProcessor {

	/**
	 * Tareas de planeacion.
	 */
	private MyTasks tasks;
	/**
	 * Interrupciones.
	 */
	private MyInterruptions interruptions;
	/**
	 * Helper para procesar directorios.
	 */
	private DirectoryHelper directoryHelper;
	/**
	 * Estadisticas de las anotaciones.
	 */
	private AnnotationStatistics statistics;

	/**
	 * Contructor de la clase.
	 * 
	 * @param path
	 *            Ruta del directorio donde estan los archivos .properties
	 * @throws FileNotFoundException
	 *             Si no encuentra los archivos
	 * @throws IOException
	 *             Si no puede leer los archivos.
	 */
	public AnnotationProcessor(String path) throws FileNotFoundException,
			IOException {

		this.tasks = new MyTasks(path);
		this.interruptions = new MyInterruptions(path);
		this.directoryHelper = new DirectoryHelper();
		this.statistics = new AnnotationStatistics();
	}

	/**
	 * Getter de Tareas.
	 * 
	 * @return Objeto para manipular el archivo de tareas.
	 */
	public MyTasks getTasks() {
		return tasks;
	}

	/**
	 * Getter de Interrupciones.
	 * 
	 * @return Objeto para manipular el archivo de interrupciones.
	 */
	public MyInterruptions getInterruptions() {
		return interruptions;
	}

	/**
	 * Getter del helper para directorios.
	 * 
	 * @return Objeto para facilitar el escaneo de directorios y compilacion del
	 *         codigo fuente.
	 */
	public DirectoryHelper getDirectoryHelper() {
		return directoryHelper;
	}

	/**
	 * Getter de Estadisticas.
	 * 
	 * @return Objeto que reune las estadisticas del procesamiento de las
	 *         anotaciones.
	 */
	public AnnotationStatistics getStatistics() {
		return statistics;
	}

	/**
	 * Scanea las anotaciones de todas las Clases en un directorio.
	 * 
	 * @param path
	 *            Directorio a scanear.
	 * @throws RuntimeException
	 *             Si no es posible compilar los sources.
	 */
	@Loc(size = 49)
	public void scanDirectory(String path) throws RuntimeException {

		if (!path.endsWith(File.separator)) {
			path = path.concat(File.separator);
		}

		System.out.println("Escaneando directorio " + path);

		List<File> sourceFileList = directoryHelper.findSources(new File(path));

		String compileDestination = path + ".." + File.separator + "bin"
				+ File.separator;

		boolean result = directoryHelper.compileSources(sourceFileList,
				compileDestination);

		if (result) {
			System.out.println("Procesando clases...");

			List<File> classesList = directoryHelper.findClasses(new File(
					compileDestination));

			for (File classFile : classesList) {
				String className = classFile.getPath();
				className = className
						.replace(path, "")
						.replace(
								".." + File.separator + "bin" + File.separator,
								"").replace(".class", "")
						.replace(File.separator, ".");

				try {
					URL[] urls = new URL[] { new URL("file://"
							+ compileDestination) };
					URLClassLoader ucl = new URLClassLoader(urls);

					Class<?> _class = ucl.loadClass(className);

					if (!_class.isInterface() && !_class.isAnnotation()) {
						System.out.println("Procesando " + className);
						this.processAnnotations(_class);

					} else {
						System.out.println("Ignorando " + className);
					}

				} catch (ExceptionInInitializerError e) {
					System.err.println(e.getMessage());

				} catch (ClassNotFoundException e) {
					System.err.println(e.getMessage());

				} catch (MalformedURLException e) {
					System.err.println(e.getMessage());
				}
			}

		} else {
			throw new RuntimeException("Imposible compilar el codigo fuente !");
		}
	}

	/**
	 * Extrae todas las anotaciones de una Clase.
	 * 
	 * @param cls
	 *            Class a procesar.
	 */
	@Loc(size = 21)
	public void processAnnotations(Class<?> cls) {

		List<Annotation> annotations = new ArrayList<Annotation>();

		// add class annotations
		annotations.addAll(Arrays.asList(cls.getAnnotations()));

		// add each method annotations
		for (Method m : cls.getMethods()) {
			annotations.addAll(Arrays.asList(m.getAnnotations()));
		}

		for (Annotation a : annotations) {
			classifyAnnotation(a);
		}
	}

	/**
	 * Clasifica la anotacion de acuerdo a el nombre para las estadisticas.
	 * 
	 * @param a
	 *            Anotacion a clasificar.
	 */
	@Loc(size = 52)
	private void classifyAnnotation(Annotation a) {

		String annotationName = a.annotationType().getName();

		if (annotationName.endsWith("Tasks")) {
			Tasks tasks = (Tasks) a;
			for (int i = 0; i < tasks.value().length; i++) {
				statistics.addLogT(tasks.value()[i]);
			}
			
		} else if (annotationName.endsWith("Interruptions")) {
			Interruptions interruptions = (Interruptions) a;
			for (int i = 0; i < interruptions.value().length; i++) {
				statistics.addLogInt(interruptions.value()[i]);
			}
			
		} else if (annotationName.endsWith("PhaseDefects")) {
			PhaseDefects phaseDefects = (PhaseDefects) a;
			for (int i = 0; i < phaseDefects.value().length; i++) {
				statistics.addPlanQ(phaseDefects.value()[i]);
			}
			
		} else if (annotationName.endsWith("Defects")) {
			Defects defects = (Defects) a;
			for (int i = 0; i < defects.value().length; i++) {
				statistics.addLogD(defects.value()[i]);
			}
			
		} else if (annotationName.endsWith("LogT")) {
			statistics.addLogT((LogT) a);
			
		} else if (annotationName.endsWith("LogInt")) {
			statistics.addLogInt((LogInt) a);
			
		} else if (annotationName.endsWith("Loc")) {
			statistics.addLoc((Loc) a);
			
		} else if (annotationName.endsWith("Plan")) {
			statistics.setPlan((Plan) a);
		}
	}
}
