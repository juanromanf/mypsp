package main;

import java.io.FileNotFoundException;
import java.io.IOException;

import annotation.Defects;
import annotation.Interruptions;
import annotation.Loc;
import annotation.LogD;
import annotation.LogInt;
import annotation.LogT;
import annotation.PhaseDefects;
import annotation.Plan;
import annotation.PlanQ;
import annotation.Tasks;

import common.AnnotationProcessor;
import common.AnnotationStatistics;
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
	@LogT(date = "2011/02/21", taskId = 999, time = 60),
	//ciclo 2
	@LogT(date = "2011/03/08", taskId = 1, time = 25),
	@LogT(date = "2011/03/08", taskId = 2, time = 17),
	@LogT(date = "2011/03/08", taskId = 3, time = 5),
	@LogT(date = "2011/03/08", taskId = 4, time = 5),
	@LogT(date = "2011/03/08", taskId = 5, time = 56),
	@LogT(date = "2011/03/08", taskId = 6, time = 13),
	@LogT(date = "2011/03/08", taskId = 7, time = 10),
	@LogT(date = "2011/03/08", taskId = 8, time = 37),
	@LogT(date = "2011/03/08", taskId = 999, time = 30)
})

@Interruptions({ 
	@LogInt(date = "2011/02/20", intId = 1, time = 30),
	@LogInt(date = "2011/02/20", intId = 2, time = 180),
	@LogInt(date = "2011/02/21", intId = 1, time = 10),
	// ciclo 2
	@LogInt(date = "2011/03/08", intId = 3, time = 10),
	@LogInt(date = "2011/03/08", intId = 4, time = 10)
	
})

@PhaseDefects({
	@PlanQ(phase = 1, injected = 0, removed = 0),
	@PlanQ(phase = 2, injected = 0, removed = 0),
	@PlanQ(phase = 3, injected = 5, removed = 5),
	@PlanQ(phase = 4, injected = 7, removed = 6),
	@PlanQ(phase = 5, injected = 3, removed = 2)
})

@Defects({
	@LogD(phase = 3, type = 12),
	@LogD(phase = 3, type = 13),
	@LogD(phase = 4, type = 1),
	@LogD(phase = 4, type = 14),
	@LogD(phase = 4, type = 20),
	@LogD(phase = 4, type = 21),
	@LogD(phase = 5, type = 17)
})
public class MyPSP {

	private AnnotationProcessor processor;

	public MyPSP(String path) throws FileNotFoundException, IOException {

		processor = new AnnotationProcessor(path);
	}

	private void processDirectory(String path) {

		try {
			processor.scanDirectory(path);
			printStatistics();
			
		} catch (RuntimeException e) {
			
			System.err.println(e.getMessage());
		}
	}

	@Loc(size = 28)
	public void printStatistics() {
		
		AnnotationStatistics stats = processor.getStatistics();

		System.out.println("");
		System.out.println("                MyPSP                  ");
		System.out.println("_______________________________________");
		System.out.println(" - Numero @LogT          : " + stats.getLogTList().size());
		System.out.println(" - Numero @LogInt        : " + stats.getLogIntList().size());
		System.out.println(" - Numero @Loc           : " + stats.getLocList().size());
		System.out.println("_______________________________________");
		System.out.println(" - Tiempo Planeado       : " + stats.getPlannedTime());
		System.out.println(" - LOCs Estimadas        : " + stats.getPlan().size());
		System.out.println(" - Tiempo Total          : " + stats.getLogTSum());
		System.out.println(" - Tiempo Interrupciones : " + stats.getLogIntSum());
		System.out.println("_______________________________________");
		System.out.println(" - LOCs Totales          : " + stats.getLocSum());
		System.out.println(" - Productividad         : " + stats.getRealTime());
		System.out.println("");
		
		System.out.println("               Defectos                ");
		System.out.println("_______________________________________");
		
		for(PlanQ pq : stats.getPlanQList()) {
			System.out.println(String.format(" - Estimado Fase %d: Inyectar = %02d  Remover = %02d",
					pq.phase(), pq.injected(), pq.removed()));
			System.out.println("  * Removidos = " + stats.getTotalDefects(pq.phase()));
			System.out.println("");
		}
	}

	/**
	 * MyPSP Main
	 * 
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
				app.processDirectory(path);
			}
			System.out.println("Finalizado...");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
