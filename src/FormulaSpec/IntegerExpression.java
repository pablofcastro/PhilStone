package FormulaSpec;

public interface IntegerExpression extends Expression {
	
	 public void accept(IntegerVisitor v);

}
