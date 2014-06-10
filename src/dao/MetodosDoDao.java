package dao;

import java.util.HashSet;
import java.util.Set;

import antlr.JavaBaseListener;
import antlr.JavaParser;

public class MetodosDoDao extends JavaBaseListener {

	private String classe;
	private Set<String> metodosQueDevolvemOTipo;
	private Set<String> metodosQueNaoDevolvemOTipo;
	private String lastModifier;
	
	public MetodosDoDao() {
		metodosQueDevolvemOTipo = new HashSet<String>();
		metodosQueNaoDevolvemOTipo = new HashSet<String>();
	}
	
	public Set<String> getMetodosQueDevolvemOTipo() {
		return metodosQueDevolvemOTipo;
	}
	
	public Set<String> getMetodosQueNaoDevolvemOTipo() {
		return metodosQueNaoDevolvemOTipo;
	}

	@Override public void enterModifier(JavaParser.ModifierContext ctx) { 
		lastModifier = ctx.getText();
	}
	
	@Override public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
		
		if(notPublic()) return;
		
		String metodo = null;
		try {
			String tipo = removeGenerico(ctx.type().getText());
			metodo = ctx.Identifier().getText();
			
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
		classe = ctx.Identifier().getText();
	}

	
}
