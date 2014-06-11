package listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import antlr.JavaBaseListener;
import antlr.JavaParser;
import antlr.JavaParser.FormalParameterContext;
import antlr.JavaParser.MethodDeclarationContext;

public class DaoMethods extends JavaBaseListener {

	private Stack<String> classes;
	private Set<String> rightOnes;
	private Set<String> problematicOnes;
	private String lastModifier;
	private List<String> primitivesList;
	private Set<String> enumerators;
	private Map<String, Set<String>> interfaces;
	
	public DaoMethods(Set<String> enumerators, Map<String, Set<String>> interfaces) {
		this.enumerators = enumerators;
		this.interfaces = interfaces;
		
		rightOnes = new HashSet<String>();
		problematicOnes = new HashSet<String>();
		classes = new Stack<String>();
		
		primitivesList = new ArrayList<String>();
		primitivesList.add("int");
		primitivesList.add("boolean");
		primitivesList.add("double");
		primitivesList.add("long");
		primitivesList.add("char");
		primitivesList.add("float");
		primitivesList.add("byte");
		primitivesList.add("short");
		primitivesList.add("Integer");
		primitivesList.add("Byte");
		primitivesList.add("Short");
		primitivesList.add("Double");
		primitivesList.add("Boolean");
		primitivesList.add("Long");
		primitivesList.add("Character");
		
		primitivesList.add("String");
		primitivesList.add("BigDecimal");
		primitivesList.add("Calendar");
		primitivesList.add("Date");
	}

	public Set<String> getRightOnes() {
		return rightOnes;
	}
	
	public Set<String> getProblematicOnes() {
		return problematicOnes;
	}

	@Override public void enterModifier(JavaParser.ModifierContext ctx) { 
		if(classes.size()!=1) return;
		lastModifier = ctx.getText();
	}
	
	@Override public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
		if(classes.size()!=1) return;
		if(notPublic()) return;

		
		try {
			String returnType = removeGenerics(ctx.type().getText());
			String methodName = ctx.Identifier().getText();
			
			if(typeMatches(clazz(), returnType) || parameterIsFromType(ctx) ||
					isPrimitive(returnType) || isEnum(returnType) || isInterface(returnType)) {
				rightOnes.add(methodName);
			} else {
				problematicOnes.add(methodName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private String clazz() {
		return classes.peek();
	}

	private boolean isInterface(String returnType) {
		Set<String> interfacesImplemented = interfaces.get(returnType);
		if(interfacesImplemented == null) return false;
		return interfacesImplemented.contains(clazzWithoutDao());
	}

	private String clazzWithoutDao() {
		return clazz().substring(0, clazz().length()-3);
	}

	private boolean isEnum(String returnType) {
		return enumerators.contains(returnType);
	}

	private boolean isPrimitive(String returnType) {
		return primitivesList.contains(returnType);
	}

	@Override public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		classes.add(ctx.Identifier().getText());
	}
	@Override public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		classes.pop();
	}


	private boolean typeMatches(String daoClazz, String type) {
		return daoClazz.startsWith(type);
	}
	
	private boolean parameterIsFromType(MethodDeclarationContext ctx) {
		if(ctx.formalParameters().formalParameterList() == null) return false;

		for(FormalParameterContext param : ctx.formalParameters().formalParameterList().formalParameter()) {
			String parameterType = param.type().getText();
			if(typeMatches(clazz(), parameterType)) return true;
		}
		return false;
	}

	private boolean notPublic() {
		return !"public".equals(lastModifier);
	}

	private String removeGenerics(String text) {
		int start = text.indexOf("<");
		int end = text.indexOf(">");
		if(start>0) return text.substring(start+1, end);
		return text;
	}
	
}
