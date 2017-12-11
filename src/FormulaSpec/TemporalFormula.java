package FormulaSpec;
import java.util.*;

public abstract class TemporalFormula implements Formula {

	private Formula expr1;
	private Formula expr2;
	private int id;
	
	public TemporalFormula(Formula e1, Formula e2){
		expr1 = e1;
		expr2 = e2; 
		id = FormulaID.id; // a temporal formula has a unique id
		FormulaID.id++;
	}
	
	//@Override
    //public abstract  String toString();
	
	 /***
     * @return the first expression.
     */
    public Formula getExpr1(){
    	return this.expr1;
    	
    }
    
    /**
     * @return	the second expression
     */
    public Formula getExpr2(){
    	return this.expr2;
    }
    
    /**
     * @return	A fresh ID for the actual formula
     */
    public int getId(){
    	return id;
    }
    
    /**
     * For those temporal formula that need additional variables to be translated to Alloy 
     * we use this function, it generates fresh variables for this formula and all its temporal subformulas
     * @param modelName
     * @return	a fresh variable representing this formula
     */
    public  LinkedList<String> generateAuxProps(String modelName){
    	LinkedList<String> result = new LinkedList<String>();
    	if (expr1 instanceof TemporalFormula)
    		result.addAll(((TemporalFormula) expr1).generateAuxProps(modelName));
    	if (expr2 instanceof TemporalFormula)
    		result.addAll(((TemporalFormula) expr2).generateAuxProps(modelName));		
    	if ((this instanceof AU) || (this instanceof AW) || (this instanceof EG)){
    		result.add("one sig Prop_Form"+this.getId()+" extends Prop{}");
    		String propName = "Prop_Form"+this.getId();
    		result.add("pred "+propName+"[m:"+modelName+",n:Node]{"+propName+" in m.val[n]}");
    		//return result;
    	}
    	if (this instanceof AF){
    		result.add("one sig Prop_Form"+this.getId()+" extends Prop{}");
    		String propName = "Prop_Form"+this.getId();
    		result.add("pred "+propName+"[m:"+modelName+",n:Node]{"+propName+" in m.val[n]}");
    		
    		// aux proposition for the auxiliar formula
    		EG auxForm = ((AF) this).getAuxForm();
    		result.add("one sig Prop_Form"+auxForm.getId()+" extends Prop{}");
    		String auxPropName = "Prop_Form"+auxForm.getId();
    		result.add("pred "+auxPropName+"[m:"+modelName+",n:Node]{"+auxPropName+" in m.val[n]}");
    	}
    	return result;
    }
    
    /**
     * 
     * @return an axiom binding each new proposition with the corresponding predicate
     */
    public String getAxiom(){
    	if ((this instanceof AF) || (this instanceof AU) || (this instanceof AW) || (this instanceof EG))
    		return "all s:nodes | Prop_Form"+this.getId()+" in val[s] iff Form"+this.getId()+"[this,s] ";
    	else
    		return "";
    }
    
    /**
     * 
     * @return	the axioms needed for this formula and all its subformulas
     */
    public LinkedList<String> generateAxioms(){
    	LinkedList<String> result = new LinkedList<String>();
    	//result += this.getAxiom();
    	result.add(this.getAxiom());
    	if (this instanceof AF){
			result.add(((AF) this).getAuxForm().getAxiom()); // we only need one axiom for this subformulas are shared with the main formula
		}
    	if (this.getExpr1() instanceof TemporalFormula)
    		result.addAll(((TemporalFormula) this.getExpr1()).generateAxioms());
    	if (this.getExpr2() instanceof TemporalFormula)
    		result.addAll(((TemporalFormula) this.getExpr2()).generateAxioms());
    	return result;
    }
    
    /**
     * For those temporal formula that need additional variables to be translated to Alloy 
     * we use this function
     * @param modelName
     * @return	A predicate for this formula
     */
    public abstract String getAuxPred(String modelName);
    
    /**
     * It generates all the predicates needed for the current formula and all its subformulas
     * @param modelName
     * @return	A stirng with all the needed predicates
     */
    public abstract LinkedList<String> generatePreds(String modelName);//{
    	//LinkedList<String> result = new LinkedList<String>();
    	//if (!this.getAuxPred(modelName).equals("")){
    	//	result.add(this.getAuxPred(modelName));
    	//	if (this instanceof AF){
    	//		result.add(((AF) this).getAuxForm().getAuxPred(modelName));
    	//		result.add(((AF) this).getAuxForm().getAuxSucc(modelName));
    	//	}
    	//	if (this instanceof EG){
    	//		result.add(((EG) this).getAuxSucc(modelName));
    	//	}
    	//	if (this instanceof EU){
    	//		result.add(((EU) this).getAuxSucc(modelName));
    	//	}
    	//}
    	//if (this.getExpr1() instanceof TemporalFormula)
    	//	result.addAll(((TemporalFormula) this.getExpr1()).generatePreds(modelName));//result+=((TemporalFormula) this.getExpr1()).generatePreds(modelName);
    	//if (this.getExpr2() instanceof TemporalFormula)
    	//	result.addAll(((TemporalFormula) this.getExpr2()).generatePreds(modelName));//result+=((TemporalFormula) this.getExpr2()).generatePreds(modelName);
    	//return result;
    //}
    

}
