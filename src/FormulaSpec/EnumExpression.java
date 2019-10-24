package FormulaSpec;

public interface EnumExpression extends Expression {
	public void accept(EnumVisitor v);
}
