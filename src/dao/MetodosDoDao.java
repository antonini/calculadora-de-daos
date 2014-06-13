package dao;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import antlr.JavaBaseListener;
import antlr.JavaParser;

public class MetodosDoDao extends JavaBaseListener {

	private String classe;
	private Set<String> metodosQueDevolvemOTipo;
	private Set<String> metodosQueNaoDevolvemOTipo;
	private String lastModifier;
	
	private Stack<String> classes;
	
	public MetodosDoDao() {
		metodosQueDevolvemOTipo = new HashSet<String>();
		metodosQueNaoDevolvemOTipo = new HashSet<String>();
		classes = new Stack<String>();
	}
	
	public Set<String> getMetodosQueDevolvemOTipo() {
		return metodosQueDevolvemOTipo;
	}
	
	public Set<String> getMetodosQueNaoDevolvemOTipo() {
		return metodosQueNaoDevolvemOTipo;
	}

	@Override public void enterModifier(JavaParser.ModifierContext ctx) {
		if(classes.size()!=1) return;
		lastModifier = ctx.getText();
	}
	
	@Override public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
		if(classes.size()!=1) return;
		if(notPublic()) return;

		try {
			String tipoDigitado;
			if(ctx.type()==null) tipoDigitado = "void";
			else tipoDigitado = ctx.type().getText();
			
			String tipo = removeGenerico(tipoDigitado);
			String metodo = FullMethodName.fullMethodName(ctx.Identifier().getText(), ctx.formalParameters().formalParameterList());
			
			if(classe.startsWith(tipo)) {
				metodosQueDevolvemOTipo.add(metodo);
			} else {
				metodosQueNaoDevolvemOTipo.add(metodo);
			}
		} catch (Exception e) {
		}
	}
	private boolean notPublic() {
		return !"public".equals(lastModifier);
	}

	private String removeGenerico(String text) {
		int menor = text.indexOf("<");
		int maior = text.indexOf(">");
		if(menor>0) return text.substring(menor+1, maior);
		return text;
	}

	@Override public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) { 
	
	}
	
	@Override public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		if(classes.isEmpty()) classe = ctx.Identifier().getText();
		classes.add(ctx.Identifier().getText());
	}
	@Override public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		classes.pop();
	}

	
}
