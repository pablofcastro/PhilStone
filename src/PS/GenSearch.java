package PS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import FormulaSpec.Formula;
import JFlex.Out;
import LTS.LTS;
import Spec.Spec;
import faulty.Program;
import formula.FormulaElement;
import mc.DCTL_MC;
import mc.FormulaParser;
import mc.ProgramParser;

/** 
 * GenSearch is a variation oh CounterExampleSearch that implements which generalizes a solution to N parameters,
 * that is, given N processes P1,...,PN it tries to solve the problem for few parameters, and then generalizes it 
 * by adding the rest of the processes
 * @author Pablo
 *
 */

public class GenSearch{
	String syntProgram; 								// the program synthesized
	LinkedList<String> processes;						// a list of processes
	LinkedList<String> instancesList; 					// a list of instances
	HashMap<String, String> instances; 
	HashMap<String, LTS> mapInsModels; 					// a hashmap mapping each INSTANCE to its candidate model,
	HashMap<String, LTS> mapProcessModels;  			// a hashmap mapping each PROCESS to its laxest model
	HashMap<String, Boolean> changed; 					// a hash map to indicate if a new process has been generated for a given instance
	Spec mySpec;			// the specification
	Spec smallSpec; 		// a small specification which will be generalised
	String outputPath;		// the output path for the synthesized program
	String templatePath;	// the path to the template,
	int numberIns; 			// number of the running processes
	boolean showInfo = false; // when true the methods will show the info of the search
	boolean printPDF;
	int scope;
	
	public GenSearch(Spec mySpec, Spec smallSpec, String outputPath, String templatePath, boolean showInfo, boolean printPDF, int scope){
		
		this.syntProgram = "";
		this.mySpec = mySpec;
		this.smallSpec = smallSpec;
		this.showInfo = showInfo;
		this.printPDF = printPDF;
		this.scope = scope;
		processes = mySpec.getProcessesNames();
		this.instances = mySpec.getInstanceTypes(); // this can only be done if all the instances have the same type
		
		instancesList = new LinkedList<String>(instances.keySet());
		for (int i=0; i<instancesList.size(); i++){
			if (!instances.get(instancesList.get(i)).equals(instances.get(instancesList.get(0)))){
				throw new RuntimeException("For now, synthesis by Generalization only can be used when all the process instances has the same type.");
			}
				
		}
		// if not exception allthe instances has the same type
		
		
		this.mapInsModels = new HashMap<String, LTS>(); // insLaxModels
		this.mapProcessModels = new HashMap<String, LTS>(); // laxModels
		this.changed = new HashMap<String, Boolean>();
		
		this.outputPath = outputPath;
		this.templatePath = templatePath;
		this.numberIns = this.instancesList.size();
		
	}
	
	public String getSyntProgram(){
		return this.syntProgram;
	}
	
	public void startGenSearch(){
		HashMap<String, String> smallSpecIns = this.smallSpec.getInstanceTypes();
		LinkedList<String> smallSpecInsList = new LinkedList<String>(smallSpecIns.keySet());
				
		System.out.println("Trying the small Spec");
		CounterExampleSearch cex = new CounterExampleSearch(this.smallSpec, outputPath, templatePath, showInfo, printPDF, scope);
		cex.startSearch();
		if (!cex.getSyntProgram().equals("")){ // if found a program, we check if there is some process not changed
			System.out.println("The Small Specification was Synthesized");
			
			// the initial models are saved
			for (int j=0; j< this.processes.size(); j++){
				this.mapProcessModels.put(this.processes.get(j), cex.getLTSForProcess(this.processes.get(j)));
			}
			for (int k=0; k<smallSpecIns.size(); k++){
				this.mapInsModels.put(instancesList.get(k), cex.getLTSForInstance(smallSpecInsList.get(k)));
				this.changed.put(instancesList.get(k), new Boolean(true));
			}
			for (int k=0; k<smallSpecIns.size(); k++){
				for (int i=smallSpecInsList.size(); i<instancesList.size(); i++){
					this.mapInsModels.put(instancesList.get(i), cex.getLTSForInstance(smallSpecInsList.get(k)));
					this.changed.put(instancesList.get(i), new Boolean(true));				
				}
				if (this.modelCheck()){
					System.out.println("Program Synthesized.");
					return;
				}
			}
			
			System.out.println("Program not Found!");
		}
	// now we start inspecting restricted sets of instances Spec mySpec, String outputPath, String templatePath, boolean showInfo, boolean printPDF, int scope
	/*	if (instancesList.size()>2){
			for (int i=2; i<instancesList.size(); i++){
				LinkedList<String> subset = new LinkedList<String>();
				subset.addAll(instancesList.subList(0, i));
				Spec restSpec = mySpec.restrictTo(subset);
				CounterExampleSearch cex = new CounterExampleSearch(restSpec, outputPath, templatePath, showInfo, printPDF, scope);
				cex.startSearch();
				if (!cex.getSyntProgram().equals("")){ // if found a program, we check if there is some process not changed
					// we implement some heuristics 
					// first, we try replicating the found processes
					for (int k=0; k<=i; i++){
						for (int j=0; j< this.processes.size(); j++){
							this.mapProcessModels.put(this.processes.get(j), cex.getLTSForProcess(this.processes.get(j)));
						}
						for (int j=0; j<=i; j++){
							this.mapInsModels.put(instancesList.get(j), cex.getLTSForInstance(instancesList.get(j)));
							this.changed.put(instancesList.get(j), new Boolean(true));
						}
						for (int j=i+1; j<instancesList.size(); j++){
							this.mapInsModels.put(instancesList.get(j), cex.getLTSForInstance(instancesList.get(j)));
							this.changed.put(instancesList.get(j), new Boolean(true));
						}
						if (this.modelCheck()){
							//System.out.println("Program Synthesized.");
							return;
						}
					}
					
				}
			}
		}
		else{
			CounterExampleSearch cex = new CounterExampleSearch(mySpec, outputPath, templatePath, showInfo, printPDF, scope);
			cex.startSearch();
			this.syntProgram = cex.getSyntProgram();
		}	*/
		
	}
	
	/**
	 * A private method to model check a collection of instances and a set of global vars
	 * @param ins	the current instance that the algorithms is inspecting
	 * @param cexs			a collection of counterexamples, if any counterexample is found it must be added here
	 * @return	true when a program was found
	 */
	private boolean modelCheck(){
			
		// WE CONSTRUCT THE PROGRAM
		String program = "";
		
		LinkedList<String> definedProcesses = new LinkedList<String>(); // a list to save the processes that must be defined in the program
		Iterator<String> it = mapInsModels.keySet().iterator();
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
			if (this.mapProcessModels.containsKey(currentProcess)) // if it is a process defined in the program
				currentLTS = mapProcessModels.get(currentProcess);
			else // otherwise is an instance with its own process definition
				currentLTS = this.mapInsModels.get(currentProcess);
			program += "Enum state"+currentProcess +" = {";
			LinkedList<String> nodes = currentLTS.getEqClassesNames();		
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
				//program += "Global "+currentVar+" : "+ globalVars.get(currentVar)+";\n"; // this has to be added when we have monitors
				program += "Global "+"Av_"+currentVar+" : BOOL;\n"; // for each global var we have a lock
		}
		program += "\n";
		
		// the processes are written down
		//Iterator<String> it3 = processes.keySet().iterator();
		Iterator<String> it3 = definedProcesses.iterator();
		while (it3.hasNext()){
			HashMap<String, String> pars = new HashMap<String, String>();
			String currentProcess = it3.next();
			if (mapProcessModels.containsKey(currentProcess)){
				LinkedList<String> processPars = mySpec.getProcessByName(currentProcess).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += mapProcessModels.get(currentProcess).toMCProcess(pars, currentProcess, currentProcess); // no parameters by now
				
			}
			else{
				LinkedList<String> processPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += mapInsModels.get(currentProcess).toMCProcess(pars, currentProcess+"Process", currentProcess);
			}
		}
		
		program += "\n";
		// and the main program
		program += "Main(){\n";
		//Iterator<String> it4 = instances.keySet().iterator();
		Iterator<String> it4 = mapInsModels.keySet().iterator();
		while (it4.hasNext()){
			String currentInstance = it4.next();
			//program  += currentInstance +":"+"NoName;\n"; //+instances.get(currentInstance)+";\n";
			if (changed.get(currentInstance))
				program  += currentInstance +":"+currentInstance+"Process"+";\n";
			else
				program  += currentInstance +":"+ mySpec.getInstanceTypes().get(currentInstance)+";\n";
		}
		
		// we run the instances
		//Iterator<String> it5 = instances.keySet().iterator();
		Iterator<String> it5 = mapInsModels.keySet().iterator();
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
		
		
		// we get the global property and parse it
		String formString = prop.toString()+";";
		
		//System.out.println(formString);
		FormulaParser formulaParser = new FormulaParser(pparser.getSymbolsTable(), model);
	    FormulaElement form = formulaParser.parseFromString(formString);
	    //model.buildModel();
	   
	    if (showInfo)
	    	System.out.println(program);
	    // we model check the specification together with the formula
	    if (DCTL_MC.mc_algorithm_eq(form, model)){
	    	syntProgram = program; // if true we save the program
	    	return true;
	    }
	    else{ // if the model checking is not sucessful we search for counterexamples
	    	//FormulaElement negForm = new formula.Negation("!", form);
	    	//CounterExample c = new CounterExample();
	    	//System.out.println(c);
	    	// TBD add the counterexample to the collection of counterexamples
	    	Program.myFactory.done();
	    	return false; // otherwise it is false
	    }
	   
	    //syntProgram = program;
	    //return DCTL_MC.mc_algorithm_eq(form, model);
	    //return true;
	}

}
