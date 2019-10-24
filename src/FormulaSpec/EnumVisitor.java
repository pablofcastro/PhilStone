package FormulaSpec;

public interface EnumVisitor {
	
	public void visit(EnumVar e);
	public void visit(EnumPar e);
	public void visit(IncEnum e);
	public void visit(DecEnum e);
    public void visit(EnumConstant e);
}
