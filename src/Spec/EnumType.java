package Spec;
import FormulaSpec.*;
import java.util.*;
/**
 * It provides the behavior of enum domain, it has the collection of variables with the type of this enum
 * it also provides methods to produce the alloy and NuSMV specs
 * @author Pablo
 *
 */
public class EnumType{
	LinkedList<EnumVar> vars; // the vars with the enum type
	LinkedList<String> values; // the values of the type
	String name; // the name of the type enum, this can be assigned automatically by the parser
	
	public EnumType(){
		this("");
	}
	
	public EnumType(String name){
		this.name = name;
		vars = new LinkedList<EnumVar>();
		values = new LinkedList<String>();
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void addValue(String val){
		values.add(val);
	}
	
	public void addValues(LinkedList<String> values){
		this.values.addAll(values);
	}
	
	public void addVar(EnumVar v){
		if (!vars.contains(v))
			vars.add(v);
	}
	
	/**
	 * 
	 * @param v
	 * @return	true iff teh given values are the values of the type
	 */
	public boolean checkEqValues(LinkedList<String> values){
		boolean result = false;
		result = values.size() == this.values.size();
		if (result){
			for (int i=0; i< this.values.size();i++){
				result = result && this.values.get(i).equals(values.get(i));		
			}
		}
		return result;
	}
	
	
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @return	the alloy code corresponding to this type
	 */
	public String getAlloyCode(){
		String result = "";
		result += "fun "+this.name+"():Enum -> Enum{\n ";
		for (int i=0; i<this.values.size();i++){
			if (i==0 && this.values.size() > 1)
				result += this.values.get(0)+" ->"+this.values.get(1);
			if (i==0 && this.values.size() == 1)
				result += this.values.get(0)+" ->"+this.values.get(0);
			if (i>0 && i<this.values.size()-1)
				result += "+" + this.values.get(i)+" ->"+this.values.get(i+1);
			if (i>0 && i==this.values.size()-1)
				result += "+" + this.values.get(i)+" ->"+this.values.get(0);
		}
		result += "\n"+"}\n";
		return result;
	}
	
	public LinkedList<String> getValues(){
		return this.values;
	}
}
