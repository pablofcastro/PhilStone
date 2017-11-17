package FormulaSpec;

public interface Var extends Expression {

	public String getName();
	public String getUnqualifiedName();
	public Type getType();
	public boolean usesVar(String name);
}
