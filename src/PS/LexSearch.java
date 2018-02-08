package PS;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import FormulaSpec.Formula;
import LTS.LTS;
import Spec.Spec;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import faulty.Program;
import formula.FormulaElement;
import mc.DCTL_MC;
import mc.FormulaParser;
import mc.ProgramParser;

/**
 * THis class implements the algorithm of synthesis driven by lexicographic earch
 * @author Pablo
 *
 */

public class LexSearch{
	private String syntProgram=""; 								// the program synthesized
	private LinkedList<String> processes;						// a list of processes
	private LinkedList<String> instancesList; 					// a list of instances
	private HashMap<String, String> instances; 
	private HashMap<String, Boolean> changed; 					// a hash map to indicate if a new process has been generated for a given instance
	private HashMap<String, LTS> insLaxModels; 					// a hashmap mapping each INSTANCE to its candidate model,
	private HashMap<String, LTS> laxModels;  			// a hashmap mapping each PROCESS to its laxest model
	private LinkedList<CounterExample> counterExamples; 						// a set containing all the counter examples found until now,
	private Spec mySpec;			// the specification
	private String outputPath;		// the output path for the synthesized program
	private String templatePath;	// the path to the template,
	private int numberIns; 			// number of the running processes
	private boolean showInfo = false; // when true the methods will show the info of the search
	private boolean writePDF; // it signals if a pdf illustrating the processes will be written
	private int scope;	// the scope of the specification

	public LexSearch(Spec mySpec, String outputPath, String templatePath, boolean showInfo, boolean writePDF, int scope){
		this.syntProgram = "";
		this.showInfo = showInfo;
		this.mySpec = mySpec;
		this.scope = scope;
		this.writePDF = writePDF;
		processes = mySpec.getProcessesNames();
		this.instances = mySpec.getInstanceTypes();
		instancesList = new LinkedList<String>(instances.keySet());
		this.changed = new HashMap<String,Boolean>();
		
		// we initialise changed with false for every instance
		for (int i=0; i<instancesList.size();i++){
			changed.put(instancesList.get(i), new Boolean(false));
		}
		this.insLaxModels = new HashMap<String, LTS>(); // insLaxModels
		this.laxModels = new HashMap<String, LTS>(); // laxModels
		
		this.outputPath = outputPath;
		this.templatePath = templatePath;
		this.numberIns = this.instancesList.size();	
	}
	
	public String getSyntProgram(){
		return syntProgram;
	}
	
	/**
	 * It uses a lexicographic search for synthesizing a program satisfying the specification
	 * @return	A string with the synthesized program
	 */
	public String startLexSearch(){
		System.out.println("Using Exhaustive Search for Synthesis...");
		String result = "";
		
		// STEP 1: We generate the laxest model for each instance
		for (int i=0; i<processes.size(); i++){
			String currentProcess = processes.get(i);
			//String currentInstance = 
			
			// the output file for the Alloy model
			String outputfilename = outputPath+currentProcess+".xml";
			
			// we obtain the alloy specification for the laxest model
			//String myProcess = instances.get(currentProcess);
			String metamodel = mySpec.metamodelToString(currentProcess, templatePath, scope);
			try{			
				// we write the specification to a file
			    PrintWriter writer = new PrintWriter(outputPath+currentProcess+"Template.als", "UTF-8");
			    writer.print(metamodel);
			    writer.close();
			} catch (IOException e) {
				System.out.println("Error trying to write the alloy specifications for the processes.");
				System.out.println(e.getStackTrace());
			}
		
			A4Reporter rep = new A4Reporter();
			Module world = null;
			LTS lts = new LTS();
			try{
				world = CompUtil.parseEverything_fromFile(rep, null, outputPath+currentProcess+"Template.als");
				A4Options opt = new A4Options();
				opt.originalFilename = outputPath+currentProcess+"Template.als"; // the specification metamodel
				opt.solver = A4Options.SatSolver.SAT4J;
				Command cmd = world.getAllCommands().get(0);
				A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
				assert sol.satisfiable();
				
				// we obtain a first candidate for the coarsest model
				sol.writeXML(outputfilename); 
				
				// we read the LTS
				lts.fromAlloyXML(outputfilename);
				//System.out.println("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
				//lts.toDot("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
				
			}catch(Exception e){
				System.out.println("Input-Output Error trying to write Alloy files.");
				System.out.println(e);
			}	
			// we store the laxest model for each process
			laxModels.put(currentProcess, lts);
			// we store the laxest model for each instance, at the beginning they coincide with those of the processes
			for (int j=0; j<instancesList.size();j++){
				if (instances.get(instancesList.get(j)).equals(currentProcess))
					insLaxModels.put(instancesList.get(j), lts); // if the instance has as type the current process then the lts is set
			}
			
		}
		
		// a hashmap to keep track of which candidates have been changed
		HashMap<String, Boolean> changed = new HashMap<String,Boolean>();
		for (int i=0; i<instancesList.size();i++){
			changed.put(instancesList.get(i), new Boolean(false));
		}
		// an iterator for the laxest models
		//Iterator<String> itModels = insLaxModels.keySet().iterator();
		boolean found = lexSearch(0);
		if (found){
			System.out.println("Program Synthesized, saved to output folder.."); 
			for (int k=0; k<instancesList.size();k++){
				insLaxModels.get(instancesList.get(k)).toDot(outputPath+instancesList.get(k)+".dot");
			}
			
			// the program is written to the output folder
			try{
				PrintWriter writer = new PrintWriter(outputPath+mySpec.getName()+".imp", "UTF-8");
				writer.print(syntProgram);
				writer.close();
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		else
			System.out.println("Program not found.."); 
		// STEP 3: We start inspecting the submodels of the laxest models for each process
		//searchSubmodel(laxModels, laxModels.keySet().iterator(), outputPath);
		return result;
	}
	
	/**
	 * It implements a backtracking search over the different instances
	 * @param it 			a number indicating the current process
	 * @return	true if a method can be synthesized
	 */
	private boolean lexSearch(int it){
		//String currentInstance = it.next(); // we assume the iterator is not empty!
		String currentInstance = instancesList.get(it);
		LTS originalModel = insLaxModels.get(currentInstance);
		//if (!it.hasNext()){// base case
		if (it == instancesList.size()-1){
			if (modelCheck()){
					return true;
			}else{ // we search for another instance
				A4Reporter rep = new A4Reporter();
				Module world = null;
				LTS lts = new LTS();
				try{
					//LTS actualModel = map.get(currentProcess);
					PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
					insLaxModels.get(currentInstance).getAlloyInstancesSpec(writer,scope, new LinkedList<LinkedList<String>>());				
					world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
					A4Options opt = new A4Options();
					opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
					opt.solver = A4Options.SatSolver.SAT4J;
					Command cmd = world.getAllCommands().get(0);
					A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
					//assert sol.satisfiable();
					boolean found = false;
					while (sol.satisfiable() && !found){ // while not found try all the instances
									
						// we obtain a first candidate for the coarsest model
						sol.writeXML(outputPath+"temp.xml"); 				
						// we read the LTS
						
						//lts.fromAlloyXML(outputPath+"temp.xml");
						//lts.toDot(outputPath+"instance"+i+".dot");
						//System.out.println("Instance Number:"+i);
						//i++;
						insLaxModels.put(currentInstance, lts);
						changed.put(currentInstance, new Boolean(true));
						found = modelCheck();
						changed.put(currentInstance, new Boolean(false));
						if (found){
							// we have found an instance and indicate the change in changed map
							changed.put(currentInstance, new Boolean(true));
							return true;
						}
						sol = sol.next();// dont know about this line
					}
					// if not program found we reestablished the original model to continue with the model checking
					insLaxModels.put(currentInstance, originalModel);// the original model is reestablished
					return found;
			
				}catch(Exception e){
					System.out.println("Input-Output Error trying to write Alloy files.");
					e.printStackTrace();//System.out.println(e);
				}	
			}
		}else{	//recursive case
			if (lexSearch(it+1)){
				return true; // model found
			}
			else{ // otherwise we do a backtracking
				A4Reporter rep = new A4Reporter();
				Module world = null;
				LTS lts = new LTS();
				try{
					//LTS actualModel = map.get(currentProcess);
					PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
					insLaxModels.get(currentInstance).getAlloyInstancesSpec(writer,scope,new LinkedList<LinkedList<String>>());
					world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
					A4Options opt = new A4Options();
					opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
					opt.solver = A4Options.SatSolver.SAT4J;
					Command cmd = world.getAllCommands().get(0);
					A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
					//assert sol.satisfiable();
					boolean found = false;
					while (sol.satisfiable() && !found){ // while not found try all the instances
						// we obtain a first candidate for the coarsest model
						sol.writeXML(outputPath+"temp.xml"); 
						
						// we read the LTS
						lts.fromAlloyXML(outputPath+"temp.xml");
						lts.toDot(outputPath+"second.dot");
						insLaxModels.put(currentInstance, lts);
						// we refresh changed
						changed.put(currentInstance, new Boolean(true));
						//found = modelCheck(map, processMap, changed, mySpec);
						found = lexSearch(it+1);
						sol = sol.next();
					}
					insLaxModels.put(currentInstance, originalModel);// the original model is reestablished
					changed.put(currentInstance, new Boolean(false)); // we undo changed
					return found;
				}catch(Exception e){
					System.out.println("Input-Output Error trying to write Alloy files.");
					//System.out.println(e);
					e.printStackTrace();
				}	
				
			}
			
		}
		return false; // if we reach here we return false
	}
	
	/**
	 * A private method to model check a collection of instances and a set of global vars
	 * @return	t
	 */
	private boolean modelCheck(){
			
		// WE CONSTRUCT THE PROGRAM
		String program = "";
		
		// the hashmap instances records the type of each instance
		//HashMap<String, String> instances = new HashMap<String,String>();
		//Iterator<String> it = processesIns.keySet().iterator();
		//while(it.hasNext()){
		//	String currentIns = it.next();
		//	if (changed.get(currentIns)){
		//		instances.put(currentIns, currentIns+"Process"); // if changed a new process for it must be created	
		//	}
		//	else{
		//		instances.put(currentIns, mySpec.getInstanceTypes().get(currentIns)); // otherwise the instance is of the declared type	
		//	}
		//}
		LinkedList<String> definedProcesses = new LinkedList<String>(); // a list to save the processes that must be defined in the program
		Iterator<String> it = insLaxModels.keySet().iterator();
		while(it.hasNext()){
			String currentIns = it.next();
			if (!changed.get(currentIns) && !definedProcesses.contains(mySpec.getInstanceTypes().get(currentIns))) // if not changed and the process is not in the list
				definedProcesses.add(mySpec.getInstanceTypes().get(currentIns));
			if (changed.get(currentIns)) // if changed we add it
				definedProcesses.add(currentIns);
		}
		
		HashMap<String, String> globalVars = mySpec.getGlobalVarsTypes();
		Formula prop =  mySpec.getGlobalProperty();
		LinkedList<String> writtenProcesses = new LinkedList<String>(); // a list to keep track of the written processes until now to avoid repetitions
		
		// first we declare the enum types, an enum type for each process
		//Iterator<String> it1 = processes.keySet().iterator();
		Iterator<String> it1 = definedProcesses.iterator();
		while (it1.hasNext()){
			String currentProcess = it1.next();
			LTS currentLTS = null;
			if (laxModels.containsKey(currentProcess)) // if it is a process defined in the program
				currentLTS = laxModels.get(currentProcess);
			else // otherwise is an instance with its own process definition
				currentLTS = insLaxModels.get(currentProcess);
			program += "Enum state"+currentProcess +" = {";
			LinkedList<String> nodes = currentLTS.getNodeNames();
			for (int i=0; i<nodes.size(); i++){
				program += (i==0)? nodes.get(i) : ","+nodes.get(i);
			}
			program += "};\n";
		}
		
		// now for those 
		// now the global vars
		Iterator<String> it2 = globalVars.keySet().iterator();
		//program += "Global ";
		while (it2.hasNext()){
			String currentVar = it2.next();
			//if (it2.hasNext())
			//	program += currentVar+" : "+ globalVars.get(currentVar)+",";
			//else
				program += "Global "+currentVar+" : "+ globalVars.get(currentVar)+";\n";
				program += "Global "+"Av_"+currentVar+" : BOOL;\n"; // for each global var we have a lock
		}
		program += "\n";
		
		// the processes are written down
		//Iterator<String> it3 = processes.keySet().iterator();
		Iterator<String> it3 = definedProcesses.iterator();
		while (it3.hasNext()){
			HashMap<String, String> pars = new HashMap<String, String>();
			String currentProcess = it3.next();
			if (laxModels.containsKey(currentProcess)){
				LinkedList<String> processPars = mySpec.getProcessByName(currentProcess).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += laxModels.get(currentProcess).toMCProcess(pars, currentProcess, currentProcess); // no parameters by now
				
			}
			else{
				LinkedList<String> processPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += insLaxModels.get(currentProcess).toMCProcess(pars, currentProcess+"Process", currentProcess);
			}
		}
		
		program += "\n";
		// and the main program
		program += "Main(){\n";
		//Iterator<String> it4 = instances.keySet().iterator();
		Iterator<String> it4 = insLaxModels.keySet().iterator();
		while (it4.hasNext()){
			String currentInstance = it4.next();
			//program  += currentInstance +":"+"NoName;\n"; //+instances.get(currentInstance)+";\n";
			if (changed.get(currentInstance))
				program  += currentInstance +":"+currentInstance+"Process"+";\n";
			else
				program  += currentInstance +":"+ mySpec.getInstanceTypes().get(currentInstance)+";\n";
		}
		
		// we run the instances
		//Iterator<String> it5 = insLaxModels.keySet().iterator();
		//while (it5.hasNext()){
		//	String currentInstance = it5.next();
		//	program += "run " + currentInstance + "(";
		//	LinkedList<String> parameters = mySpec.getActualPars(currentInstance);
		//	for (int i=0; i<parameters.size();i++){
		//		if (i==0)
		//			program+=parameters.get(i) + ", Av_"+parameters.get(i);
		//		else
		//			program+=","+parameters.get(i)+ ", Av_"+parameters.get(i);
		//	}
		//	program += ");";
		//}
		//program += "}";
		
		Iterator<String> it5 = insLaxModels.keySet().iterator();
		while (it5.hasNext()){
			String currentInstance = it5.next();
			//program  += "run " + currentInstance +"();\n"; // change this for process with parameters
			program += "run " + currentInstance + "(";
			LinkedList<String> parameters = mySpec.getActualPars(currentInstance);
			for (int i=0; i<parameters.size();i++){
				if (i==0)
					//program+=parameters.get(i) + ", Av_"+parameters.get(i); // this must be changed for monitors
					program+=" Av_"+parameters.get(i);
				else
					//program+=","+parameters.get(i)+ ", Av_"+parameters.get(i);
					program+=", Av_"+parameters.get(i);
			}
			program += ");";
		}
		program += "}";
		
		//System.out.println(program);
		
		// we parse the program with the model checker
		ProgramParser pparser = new ProgramParser();
		Program model = pparser.parseFromString(program);
		//System.out.println(program);
		
		
		// we get the global property and parse it
		String formString = prop.toString()+";";
		
		//System.out.println(formString);
		FormulaParser formulaParser = new FormulaParser(pparser.getSymbolsTable(), model);
	    FormulaElement form = formulaParser.parseFromString(formString);
	    //model.buildModel();
	   
	    
	    // we model check the specification together with the formula
	    if (DCTL_MC.mc_algorithm_eq(form, model)){
	    	syntProgram = program; // if true we save the program
	    	return true;
	    }
	    else{
	    	Program.myFactory.done();
	    	return false; // otherwise it is false
	    }
	   
	    //syntProgram = program;
	    //return DCTL_MC.mc_algorithm_eq(form, model);
	    //return true;
	}
	
}
