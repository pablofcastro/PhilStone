package FormulaSpec;

public class AX extends TemporalFormula {
	
	public AX(Formula e1){
        super(e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	public String toAlloy(String metaName, String state){
		String result = "AX(" + this.getExpr1().toAlloy(metaName, state)+ ")";
		return result;
	}
	
	public String toString(){
		return "AX["+ this.getExpr1().toString() + "]";
	}
}
