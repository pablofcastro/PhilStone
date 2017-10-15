package FormulaSpec;

public interface FormulaVisitor {
	
	public void visit(BoolVar v);
	public void visit(OldBoolVar v);
	public void visit(BoolConstant c);
	public void visit(Negation n);
	public void visit(Implication i);
	public void visit(EqComparison e);
    public void visit(Conjunction c);
	public void visit(Disjunction d);
	public void visit(AX a);
	public void visit(AU a);
	public void visit(AG a);
	public void visit(AW a);
	public void visit(EX e);
	public void visit(EU e);
	public void visit(EW e);
	public void visit(ElemFormula e);
	public void visit(ElemNegation e);
	public void visit(Clause c);
	public void visit(IntConstant c);
	public void visit(Own o);
	public void visit(Av a);
	public void visit(EG e);
	public void visit(EF e);
	public void visit(AF a);
}


