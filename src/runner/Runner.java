package runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import listeners.ClassInfo;
import listeners.DaoMethods;
import antlr.ParserRunner;

public class Runner {

	public static void main(String[] args) throws IOException {
		
		FileLister files = new FileLister(args[0]);
		Set<String> enumerators = new HashSet<String>();
		Map<String, Set<String>> subtypes = new HashMap<String, Set<String>>();
		
		for(File f : files.getAllProductionFiles()) {
			ClassInfo info = new ClassInfo();
			new ParserRunner(info).run(new FileInputStream(f));
			
			if(info.isEnum()) enumerators.add(info.getName());
			if(info.isSubtypeOrImplementsInterface()) {
				subtypes.put(info.getName(), new HashSet<String>());
				for(String interfaceName : info.subtypeAndInterfaces()) {
					subtypes.get(info.getName()).add(interfaceName);
				}
			}
		}
		
		for(File f : files.getAllDaoFiles()) {
			
			DaoMethods allMethods = new DaoMethods(enumerators, subtypes);
			new ParserRunner(allMethods).run(new FileInputStream(f));
			
			if(args[2]==null) {
				printSummary(args, f, allMethods);
			} else {
				printTheProblematics(args, f, allMethods);
			}
		}
	}

	private static void printTheProblematics(String[] args, File f, DaoMethods methods) {
		Set<String> methodsToBePrinted = methods.getProblematicOnes();
		
		for(String m : methodsToBePrinted) {
			System.out.println(
				args[1] + "," + 
				f.getAbsolutePath() + "," + 
				m
			);
		}
	}

	private static void printSummary(String[] args, File f, DaoMethods methods) {
		System.out.println(
			args[1] + "," + 
			f.getAbsolutePath() + "," + 
			methods.getRightOnes().size() + "," + 
			methods.getProblematicOnes().size()
		);
	}
}
