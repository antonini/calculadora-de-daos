package antlr;

import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import antlr.JavaParser.CompilationUnitContext;
import dao.DaoMethods;

public class ParserRunner {

	public DaoMethods run(InputStream f) {
		try {
			CharStream input;
			input = new ANTLRInputStream(f);
			
			JavaLexer lex = new JavaLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			JavaParser parser = new JavaParser(tokens);
			CompilationUnitContext r = parser.compilationUnit();
			
			DaoMethods metodos = new DaoMethods();
			new ParseTreeWalker().walk(metodos, r);
			return metodos;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
