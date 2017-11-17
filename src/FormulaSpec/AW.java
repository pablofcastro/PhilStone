package FormulaSpec;

public class AW extends TemporalFormula {
	
	public AW(Formula e1, Formula e2){
        super(e1,e2);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	public String toAlloy(String metaName, String state){
		String result = "A(" + this.getExpr1().toAlloy(metaName, state) + "W" + this.getExpr2().toAlloy(metaName,state) + ")";
		return result;
	}
	
	public boolean usesVar(String name){
		return (this.getExpr1().usesVar(name) || this.getExpr2().usesVar(name));
	}
	
	public String toString(){
		return "A["+ this.getExpr1().toString() + "W"+ this.getExpr2().toString() +"]";
	}
}
