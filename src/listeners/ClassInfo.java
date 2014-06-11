package listeners;

import java.util.HashSet;
import java.util.Set;

import antlr.JavaBaseListener;
import antlr.JavaParser;
import antlr.JavaParser.TypeContext;

public class ClassInfo extends JavaBaseListener {

	private boolean isEnum;
	private String name;
	private Set<String> interfaces;
	private boolean implementsInterface;
	
	public ClassInfo() {
		interfaces = new HashSet<String>();
	}
	
	@Override public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		if(name!=null) return;
		name = ctx.Identifier().getText();
		
		if(ctx.typeList()!=null) {
			implementsInterface = true;
			for(TypeContext type : ctx.typeList().type()) {
				interfaces.add(type.getText());
			}
		}
	}

	@Override public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
		if(name!=null) return;
		
		isEnum = true;
		name = ctx.Identifier().getText();
	}
	
	public boolean isEnum() {
		return isEnum;
	}
	
	public boolean implementsInterface() {
		return implementsInterface;
	}
	
	public Set<String> interfaces() {
		return interfaces;
	}

	public String getName() {
		return name;
	}
}
