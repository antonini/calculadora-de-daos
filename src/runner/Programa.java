package runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import antlr.JavaLexer;
import antlr.JavaParser;
import antlr.JavaParser.CompilationUnitContext;
import dao.MetodosDoDao;

public class Programa {

	public static void main(String[] args) throws IOException {
		CharStream input = null;
		
		FileLister files = new FileLister(args[0]);
		
		for(File f : files.getAllDaoFiles()) {
			
			input = new ANTLRInputStream(new FileInputStream(f));
			
			JavaLexer lex = new JavaLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			JavaParser parser = new JavaParser(tokens);
			CompilationUnitContext r = parser.compilationUnit();
			
			MetodosDoDao metodos = new MetodosDoDao();
			new ParseTreeWalker().walk(metodos, r);
			
			if(args[2]==null) {
				imprimeTotalizadores(args, f, metodos);
			} else {
				imprimeOsQueNaoDevolvem(args, f, metodos);
			}
		}
	}

	private static void imprimeOsQueNaoDevolvem(String[] args, File f, MetodosDoDao metodos) {
		Set<String> metodosQueNaoDevolvemOTipo = metodos.getMetodosQueNaoDevolvemOTipo();
		for(String m : metodosQueNaoDevolvemOTipo) {
			System.out.println(args[1] + "," + f.getAbsolutePath() + "," + m);
		}
	}

	private static void imprimeTotalizadores(String[] args, File f, MetodosDoDao metodos) {
		System.out.println(args[1] + "," + f.getAbsolutePath() + "," + metodos.getMetodosQueDevolvemOTipo().size() + "," + metodos.getMetodosQueNaoDevolvemOTipo().size());
	}
}
