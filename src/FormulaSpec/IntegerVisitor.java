package FormulaSpec;

public interface IntegerVisitor {

	public void visit(SumExpression v);
	public void visit(NegExpression c);
	public void visit(MultExpression n);
	public void visit(DivExpression i);
	public void visit(IntVar e);
	public void visit(OldIntVar e);
	public void visit(IntPar e);
 
}
