package FormulaSpec;

public class EG extends TemporalFormula {
	public EG(Formula e1){
        super(e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	public String toAlloy(String metaName, String state){
		String result = ""; //TBD
		return result;
	}
	
	public boolean usesVar(String name){
		return this.getExpr1().usesVar(name);
	}
	
	public String toString(){
		return "EG["+ this.getExpr1().toString() + "]";
	}
}
