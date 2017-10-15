package Parser;

import java_cup.runtime.*;
import java.io.*;
import java.util.LinkedList;

/**
 * This class represents the compiler.
 */
public class SpecParser {
	
	private parser parser;	// Parser
	//private LinkedList<String> errorList; // Errors
    //private sym symbolsTable;
    //private FileReader programFile;
    
    
    public SpecParser(){
    	//this.errorList = new LinkedList<String>();
    }
    
    public void parse(String nameFile){
    	FileReader specFile;
        try {
            specFile = new FileReader(nameFile);
         
            // Read file
 			this.parser = new parser(new Scanner(specFile));
 			parser.parse();	 
 			
 			// Check Types
 			//Type result = checkTypes(program);
 			//if(result == Type.ERROR){
            //    for(int i=0; i<errorList.size(); i++){
            //        System.out.println(errorList.get(i).getErrorMsg());
            //    }               
            //}
            //else{ // If not errors, build the concrete Faulty program    
            //	
            //    faultyProg = buildProgram(program);
            //}    
        } catch (Exception e) {        	
 			System.out.println("Program Error." + e.getMessage());
            //e.printStackTrace(System.out);
 		}
        
        //return faultyProg;
 		
 	}

    /**
     * 
     * @return Return the symbols table of the model.
     */
    //public  SymbolsTable getSymbolsTable(){
    	//return symbolsTable;
    //}
    
 	
 	/**
 	 * Check types
 	 */
 	private void checkTypes() {
        //TypeCheckerVisitor typeV = new TypeCheckerVisitor();
        //prog.accept(typeV);
        //Ty/pe result = typeV.getType();
        //errorList = typeV.getErrorList();
        //symbolsTable =typeV.getSymbolTable();
        //return result;
 	}
 	
}