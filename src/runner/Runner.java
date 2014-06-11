package runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import antlr.ParserRunner;
import dao.DaoMethods;

public class Runner {

	public static void main(String[] args) throws IOException {
		
		FileLister files = new FileLister(args[0]);
		
		for(File f : files.getAllDaoFiles()) {
			
			DaoMethods allMethods = new ParserRunner().run(new FileInputStream(f));
			
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
