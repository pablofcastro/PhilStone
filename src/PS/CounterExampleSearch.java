package PS;
import FormulaSpec.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import FormulaSpec.Formula;
import FormulaSpec.Type;
import JFlex.Out;
import LTS.*;
import Spec.*;
import Utils.XMLAlloy;
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
 * This class implements the algorithm for performing search of program by counterexamples
 * @author Pablo
 *
 */
public class CounterExampleSearch {
	String syntProgram; 								// the program synthesized
	LinkedList<String> processes;						// a list of processes
	LinkedList<String> instancesList; 					// a list of instances
	HashMap<String, String> instances; 
	HashMap<String, Boolean> changed; 					// a hash map to indicate if a new process has been generated for a given instance
	HashMap<String, LTS> mapInsModels; 					// a hashmap mapping each INSTANCE to its candidate model,
	HashMap<String, LTS> mapProcessModels;  			// a hashmap mapping each PROCESS to its laxest model
	LinkedList<CounterExample> counterExamples; 						// a set containing all the counter examples found until now,
	HashMap<String, LinkedList<LinkedList<String>>> cexForInstance; // it contains for each instance a queue contain a collection of counterexamples
	HashMap<String, LinkedList<LinkedList<String>>> cexActualRun; // it keeps the counter examples found in the actual execution of the algorithm
	HashMap<String, Boolean> disjointCexFound; // says if a new coutnerexample was found for the corresponding instance
	Spec mySpec;			// the specification
	String outputPath;		// the output path for the synthesized program
	String templatePath;	// the path to the template,
	int numberIns; 			// number of the running processes
	HashMap<String, Integer> currentCex; // used for synthesis, the maps points out the current cex used for each process, -1 indicates no counterexample
	boolean showInfo = false; // when true the methods will show the info of the search
	boolean printPDF;
	boolean cexFound = false;
	boolean cexRefined = false;  
	boolean alloySearch = false; // to indicate  if the search is performed using Alloy
	int pathBound = 0; // the bound for the counterexamples in the case that BMC is used
	int scope;
	
	
	
	/**
	 * A basic constructor
	 * @param mySpec
	 * @param outputPath
	 * @param templatePath
	 */
	public CounterExampleSearch(Spec mySpec, String outputPath, String templatePath, boolean showInfo, boolean printPDF, int scope){
		// we just call to the extended constructor with the corresponding parameters
		this(mySpec,outputPath,templatePath,showInfo,printPDF,scope,false,0);
		
//		this.syntProgram = "";
//		this.mySpec = mySpec;
//		this.showInfo = showInfo;
//		this.printPDF = printPDF;
//		this.scope = scope;
//		processes = mySpec.getProcessesNames();
//		this.instances = mySpec.getInstanceTypes();
//		instancesList = new LinkedList<String>(instances.keySet());
//		this.changed = new HashMap<String,Boolean>();
//		
//		// we initialise changed with false for every instance
//		for (int i=0; i<instancesList.size();i++){
//			changed.put(instancesList.get(i), new Boolean(false));
//		}
//		this.mapInsModels = new HashMap<String, LTS>(); // insLaxModels
//		this.mapProcessModels = new HashMap<String, LTS>(); // laxModels
//		this.counterExamples = new LinkedList<CounterExample>();
//		this.cexForInstance = new HashMap<String, LinkedList<LinkedList<String>>>();
//		for (int i=0; i<instancesList.size(); i++){
//			this.cexForInstance.put(instancesList.get(i), new LinkedList<LinkedList<String>>());
//		}
//		
//		// we initialized all the elements of 
//		this.disjointCexFound = new HashMap<String, Boolean>();
//		for (int i=0; i<instancesList.size(); i++){
//			this.disjointCexFound.put(instancesList.get(i), new Boolean(false));
//		}
//		
//		this.cexActualRun = new HashMap<String, LinkedList<LinkedList<String>>>();
//		this.outputPath = outputPath;
//		this.templatePath = templatePath;
//		this.numberIns = this.instancesList.size();
	}
	
	
	/**
	 * An extended constructor 
	 * @param mySpec
	 * @param outputPath
	 * @param templatePath
	 */
	public CounterExampleSearch(Spec mySpec, String outputPath, String templatePath, boolean showInfo, boolean printPDF, int scope, boolean alloySearch, int pathBound){
		this.syntProgram = "";
		this.mySpec = mySpec;
		this.showInfo = showInfo;
		this.printPDF = printPDF;
		this.scope = scope;
		processes = mySpec.getProcessesNames();
		this.instances = mySpec.getInstanceTypes();
		instancesList = new LinkedList<String>(instances.keySet());
		this.changed = new HashMap<String,Boolean>();
		
		// we initialise changed with false for every instance
		for (int i=0; i<instancesList.size();i++){
			changed.put(instancesList.get(i), new Boolean(false));
		}
		this.mapInsModels = new HashMap<String, LTS>(); // insLaxModels
		this.mapProcessModels = new HashMap<String, LTS>(); // laxModels
		this.counterExamples = new LinkedList<CounterExample>();
		this.cexForInstance = new HashMap<String, LinkedList<LinkedList<String>>>();
		for (int i=0; i<instancesList.size(); i++){
			this.cexForInstance.put(instancesList.get(i), new LinkedList<LinkedList<String>>());
		}
		
		// we initialized all the elements of 
		this.disjointCexFound = new HashMap<String, Boolean>();
		for (int i=0; i<instancesList.size(); i++){
			this.disjointCexFound.put(instancesList.get(i), new Boolean(false));
		}
		
		this.cexActualRun = new HashMap<String, LinkedList<LinkedList<String>>>();
		this.outputPath = outputPath;
		this.templatePath = templatePath;
		this.numberIns = this.instancesList.size();
		this.alloySearch = alloySearch;
		this.pathBound = pathBound;
	}
	
	/**
	 * 
	 * @return	the synthesized program
	 */
	public String getSyntProgram(){
		return this.syntProgram;
	}
	
	/**
	 * A simple method to chose the synthesis method guided by alloy
	 */
	public void setAlloyBMC(int pathBound){
		this.alloySearch = true;
		this.pathBound = pathBound;
	}
	
	/**
	 * It starts the synthesis of programs guided by counterexamples
	 */
	public void startSearch(){
		System.out.println("Using Search Guided by Counterexamples for Synthesis...");
		
		// STEP 1: We generate the laxest model for each instance
		for (int i=0; i<processes.size(); i++){
			long startTime = System.currentTimeMillis();    

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
			LTS lts = new LTS(mySpec.getProcessByName(currentProcess));
			lts.setName(currentProcess);
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
			mapProcessModels.put(currentProcess, lts);
			
			// we store the laxest model for each instance, at the beginning they coincide with those of the processes
			for (int j=0; j<instancesList.size();j++){
				if (instances.get(instancesList.get(j)).equals(currentProcess)){
					mapInsModels.put(instancesList.get(j), lts); // if the instance has as type the current process 
					if (this.printPDF)
						lts.toDot(outputPath+"lax"+instancesList.get(j)+".dot");
				}
			}

			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println(currentProcess+" time:" + estimatedTime);
		}
		
		System.out.println("Local Models Generated...");
		
		// the map containing the collection of counterexamples for each instance is initialized
		for (int j=0; j<instancesList.size();j++){
			this.cexActualRun.put(instancesList.get(j), new LinkedList<LinkedList<String>>());
		}
		
		// an iterator for the laxest models
		//Iterator<String> itModels = insLaxModels.keySet().iterator();
		boolean found = counterExampleSearch(0, scope);
		if (found){
			System.out.println("Program Synthesized, saved to output folder.."); 
			
			if (this.printPDF){  // we print the dots			
				for (int i=0; i<this.instancesList.size(); i++)
					this.mapInsModels.get(instancesList.get(i)).toDot(outputPath+instancesList.get(i)+"final.dot");
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
	}
	
	public boolean counterExampleSearch(int insNumber, int scope){
		boolean result = false; // an attribute to save the result
		String currentIns = instancesList.get(insNumber);
		LTS originalModel = mapInsModels.get(currentIns);
		if (showInfo){
			System.out.println("Inspecting instance: "+currentIns);
		}
		
		if (insNumber == this.numberIns - 1){ // if so we are in the base case
			//this.alloyBoundedModelCheck("", new LinkedList<LinkedList<String>>(), 5, scope);			
			// if BMC on then we use Alloy else we use the model checker
			boolean checkResult = this.alloySearch?alloyBoundedModelCheck(currentIns, new LinkedList<LinkedList<String>>(), this.pathBound, this.scope):modelCheck(currentIns, new LinkedList<LinkedList<String>>());
			//if (modelCheck(currentIns, new LinkedList<LinkedList<String>>())){ // if false, we add the counterexamples to the corresponding queues
			if (checkResult){
				return true;
			}
			else{
				this.cexFound = false; 
				// we try to get a program for all the cex
				int cexNumber = 0; // we use -1 for the case when no counterexample is analyzed
				// we use a collection of actual counterexamples, the idea is to add new counterexample found during hte execution of the algorithm
				LinkedList<LinkedList<String>> actualCexs = new LinkedList<LinkedList<String>>();
				this.disjointCexFound.put(currentIns, new Boolean(false));
				
				while (cexNumber < this.cexForInstance.get(this.instancesList.get(insNumber)).size() & !this.disjointCexFound.get(currentIns)){
				//while (cexNumber < this.cexForInstance.get(this.instancesList.get(insNumber)).size()){	
					if (cexNumber > -1){
						actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber)); // we add the current cex
					}
					// WE GET A POSSIBLE PROGRAM AND MODEL CHECK IT
					try{
						int j = 0;
						cexRefined = false;
						A4Reporter rep = new A4Reporter();
						Module world = null;
						LTS lts = new LTS(mySpec.getProcessSpec(currentIns));
						lts.setName(currentIns);
						LTS formerLTS = mapInsModels.get(currentIns);
						PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
						mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);	
						A4Options opt = new A4Options();
						opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
						opt.solver = A4Options.SatSolver.SAT4J;
						world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
						Command cmd = world.getAllCommands().get(0);
						A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
						while (sol.satisfiable()  &  !disjointCexFound.get(currentIns) & !cexRefined){ //add refined & !disjointCexFound.get(currentIns)
							if (cexRefined){
								actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber));
								mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);
								world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
								cmd = world.getAllCommands().get(0);
								sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
								cexRefined = false;
							}								
							sol.writeXML(outputPath+"temp.xml");
							lts.fromAlloyXML(outputPath+"temp.xml");
							lts.toDot(outputPath+currentIns+".dot");
							if (showInfo)
								System.out.println("Instance "+ currentIns + ", Iteration Number:"+j);
							j++;
							mapInsModels.put(currentIns, lts);
							changed.put(currentIns, new Boolean(true));
							// if we generate a correct program then output true
							checkResult = this.alloySearch?alloyBoundedModelCheck(currentIns, actualCexs, this.pathBound, this.scope):modelCheck(currentIns, actualCexs);
							//if (modelCheck(currentIns, actualCexs)) // model check generates new counterexamples, TBD: we need to add any found instance to actualCexs
							if (checkResult)
								return true;
						
							// else continue with the search, and restore the previous values
							mapInsModels.put(currentIns, formerLTS);
							changed.put(currentIns, new Boolean(false));
							sol = sol.next();
						}
						//else{
						//	break;
						//}
					}// end of try
					catch(Exception e){
						System.out.println("Input-Output Error trying to write Alloy files.");
						try{
							PrintWriter writererror = new PrintWriter(outputPath+"InstancesError.als", "UTF-8");
							mapInsModels.get(currentIns).getAlloyInstancesSpec(writererror,scope, actualCexs);	
						}catch(Exception e1){
						}
						System.out.println(e.getMessage());
						e.printStackTrace();//System.out.println(e);
					}
					if (!cexRefined)
						cexNumber++;
					//j++;
				}
				// after this while we know that no program was found for this instance of the search
				return false;
			}
		}// end of base case
		else{ // recursive case
			int cexNumber = -1; // at the beginning we start with no counterexample
			LinkedList<LinkedList<String>> actualCexs = new LinkedList<LinkedList<String>>();
			// while there are counterexamples in the queue try...
			int k =0;
			
			while (cexNumber < this.cexForInstance.get(currentIns).size()){
						
				if (cexNumber == -1){ // if -1 then no counterexample will take into account
					if (counterExampleSearch(insNumber+1, scope))
						return true;
				}
				else{
					if (showInfo){
						System.out.println("Instance:"+currentIns);
						System.out.println(actualCexs.size());
						System.out.println(this.cexActualRun.get(currentIns).size());
						
					}
					actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber)); // we add the current cexs	
					int p = 0;
					// while we can get more counterexamples using the current counterexamples we try to find a program
					//while (cexNumber < this.cexForInstance.get(currentIns).size()){//actualCexs.size() < this.cexActualRun.get(currentIns).size()){
						
						// WE GET A POSSIBLE PROGRAM AND MODEL CHECK IT
						try{
							A4Reporter rep = new A4Reporter();
							Module world = null;
							LTS lts = new LTS(mySpec.getProcessSpec(currentIns));
							lts.setName(currentIns);
							LTS formerLTS = mapInsModels.get(currentIns);
							PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
							mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);		
							A4Options opt = new A4Options();
							opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
							opt.solver = A4Options.SatSolver.SAT4J;
							world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
							Command cmd = world.getAllCommands().get(0);
							A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
							while (sol.satisfiable()){  // !disjointCexFound.get(currentIns)??
								if (showInfo)
									System.out.println("Instance "+ currentIns + ", Iteration Number:"+p);
								sol.writeXML(outputPath+"temp.xml");
								lts.fromAlloyXML(outputPath+"temp.xml");
								lts.toDot(outputPath+"instance"+p+".dot");
								//System.out.println("Instance Number:"+i);
								p++;
								mapInsModels.put(currentIns, lts);
								changed.put(currentIns, new Boolean(true));
								
								// we try with this model recursively
								if (counterExampleSearch(insNumber+1, scope)) // model check generates new counterexamples, TBD: we need to add any found instance to actualCexs
									return true;
					
								// else continue with the search, and restore the previous values
								changed.put(currentIns, new Boolean(false));
								mapInsModels.put(currentIns, formerLTS);
								// and with add more counterexamples to the bag
								//actualCexs.addLast(this.cexActualRun.get(currentIns).get(actualCexs.size()));
								sol = sol.next();
							}
							
							System.out.println("trying...."+k);
							k++;
					
						}// end of try
						catch(Exception e){
							System.out.println("Input-Output Error trying to write Alloy files.");
							e.printStackTrace();//System.out.println(e);
						}
					//}
					// if we did not find anything the current cex, we reset the bag  of counter examples and try with the next one
					actualCexs.clear();
					this.cexActualRun.get(currentIns).clear();
				}
				cexNumber++;
			}
			return false; // if no program found then we return false	
		}				
	}
	
	private void addCounterExToProcess(String process, LinkedList<String> cex){
		if (showInfo){
			System.out.println("Cex to add:");
			System.out.println(cex);
			System.out.println("Cex in the Queue:");
			System.out.println(this.cexForInstance.get(process));
		}
		if (cex.size() < 2 ){ // For ACTL!: if size is less than 2 then the current process do not participate in the counterexample and all the branch of the backtracking can be pruned
			this.disjointCexFound.put(process, new Boolean(true));
			return;
		}
		boolean inCexList = false;
		LinkedList<LinkedList<String>> cexList = cexForInstance.get(process);
		for (int i=0; i<cexList.size(); i++){
			if (lInclusion(cexList.get(i), cex)){ // if the counterexample is already in the queue, cexList[i] is included in cex
				inCexList = true; // we do not need to add it
				if (showInfo)
					System.out.println("rinclusion");
			}
			if (rInclusion(cexList.get(i), cex)){
				cexList.remove(i);
				cexList.add(i, cex); // we swap the former cex for the new one
									    // since the left inclusion says that the right one is more refined		
				inCexList = true;
				if (showInfo)
					System.out.println("linclusion");
			}
			//if (disjoint(cexList.get(i), cex)){
			//	this.disjointCexFound.put(process, new Boolean(true)); // a disjoint cex was found
			//	cexList.addLast(cex);
			//}
				
		}
		if (!inCexList){
				cexList.addLast(cex);
				this.disjointCexFound.put(process, new Boolean(true));
		}
		boolean inRunningList = false;
		for (int i=0; i<this.cexActualRun.get(process).size(); i++){
			if (rInclusion(cexActualRun.get(process).get(i), cex)) // if the counterexample is already in the queue
				inRunningList = true; // we do not need to add it
			if (lInclusion(cexActualRun.get(process).get(i), cex)){
				cexActualRun.get(process).remove(i);
				cexActualRun.get(process).add(i, cex); // we swap the former cex for the new one
									    // since the left inculsion says that the right one is more refined		
				//cexActualRun.get(process).add(cex);
				inRunningList = true;
				cexRefined = true;
			}
			//if (disjoint(cexActualRun.get(process).get(i), cex)){
			//	this.disjointCexFound.put(process, new Boolean(true)); // a disjoint cex was found
			//	cexActualRun.get(process).addLast(cex);
			//}
		}
		if (!inRunningList){
			cexActualRun.get(process).addLast(cex);
			this.disjointCexFound.put(process, new Boolean(true));
		}
	}
	
	private void processCounterExample(CounterExample c){	
		if (this.counterExamples.contains(c)){
			this.cexFound = false;
		}
		else{
			this.cexFound = true;
			//System.out.println(this.counterExamples);
			//System.out.println("new counter example");
			if (showInfo){
				System.out.println("CounterExample found:");
				System.out.println(c);
			}
			this.counterExamples.add(c);
			for (int i=0; i<this.instancesList.size(); i++){
				addCounterExToProcess(this.instancesList.get(i), c.getRuns(this.instancesList.get(i)));
			}
		}
	}
	
	public LTS getAssociatedLTS(String ins){
		return this.mapInsModels.get(ins);
	}
	
	
	/**
	 * A private method to model check a collection of instances and a set of global vars
	 * @param ins	the current instance that the algorithms is inspecting
	 * @param cexs			a collection of counterexamples, if any counterexample is found it must be added here
	 * @return	true when a program was found
	 */
	private boolean modelCheck(String ins, LinkedList<LinkedList<String>> cexs){
			
		
		System.out.println("Using a Symbolic Model Checking...");
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
				if (!mySpec.isPrimVar(currentVar))
					program += "Global "+"Av_"+currentVar+" : BOOL;\n"; // for each global var we have a lock
				if (globalVars.get(currentVar).equals("BOOL") || globalVars.get(currentVar).equals("PRIMBOOL"))
					program += "Global "+ "Prop_"+currentVar+" : BOOL;\n"; // and the corresponding current var
				// TO DO: ADD INTEGERS
				// we need to distinguish between locks, ints and bools
		}
		program += "\n";
		
		// the processes are written down
		//Iterator<String> it3 = processes.keySet().iterator();
		Iterator<String> it3 = definedProcesses.iterator();
		while (it3.hasNext()){
			HashMap<String, String> pars = new HashMap<String, String>();
			String currentProcess = it3.next();
			if (mapProcessModels.containsKey(currentProcess)){
				LinkedList<String> processBoolPars = mySpec.getProcessByName(currentProcess).getBoolParNames();
				for (int i=0; i<processBoolPars.size();i++){
					pars.put(processBoolPars.get(i), "BOOL");
				}
				LinkedList<String> processPrimBoolPars = mySpec.getProcessByName(currentProcess).getBoolPrimParNames();
				for (int i=0; i<processPrimBoolPars.size();i++){
					pars.put(processPrimBoolPars.get(i), "PRIMBOOL");
				}
				
				LinkedList<String> processLockPars = mySpec.getProcessByName(currentProcess).getLockParNames();
				for (int i=0; i<processLockPars.size();i++){
					pars.put(processLockPars.get(i), "LOCK");
				}
				program += mapProcessModels.get(currentProcess).toMCProcess(pars, currentProcess, currentProcess); // no parameters by now
				
			}
			else{
				LinkedList<String> processPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				LinkedList<String> processPrimBoolPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolPrimParNames();
				for (int i=0; i<processPrimBoolPars.size();i++){
					pars.put(processPrimBoolPars.get(i), "PRIMBOOL");
				}
				
				LinkedList<String> processLockPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getLockParNames();
				for (int i=0; i<processLockPars.size();i++){
					pars.put(processLockPars.get(i), "LOCK");
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
			for (int i=parameters.size()-1; i>=0;i--){
				if (i==parameters.size()-1 && parameters.size()>1){
					//program+=parameters.get(i) + ", Av_"+parameters.get(i); // this must be changed for monitors
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.BOOL)
						program+= "Prop_"+parameters.get(i)+", Av_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.PRIMBOOL)
						program+= "Prop_"+parameters.get(i)+", ";
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.LOCK)
						program+= "Av_"+parameters.get(i)+", ";
				}
				else{
					//program+=","+parameters.get(i)+ ", Av_"+parameters.get(i);
					//program+=", Av_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.BOOL)
						program+= ","+"Prop_"+parameters.get(i)+", Av_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.PRIMBOOL)
						program+= ","+"Prop_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.LOCK)
						program+= "Av_"+parameters.get(i);
				}
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
	    //System.out.println(formString);
	    
	    
	    if (showInfo){
	    	System.out.println(program);
	    	//System.out.println(form);
	    }
	    //it coDCTL_MC.printMess();
	    // we model check the specification together with the formula
	    if (DCTL_MC.mc_algorithm_eq(form, model)){
	    	syntProgram = program; // if true we save the program
	    	return true;
	    }
	    else{ // if the model checking is not successful we search for counterexamples
	    	FormulaElement negForm = new formula.Negation("!", form);
	    	CounterExample c = new CounterExample();
	    	if (DCTL_MC.getWitnessAsMaps(negForm, model, ins).isEmpty()){ // NO SENSE AN EMPTY COUNTEREXAMPLE!
	    		syntProgram = program;
	    		return true;
	    	}
	    	if (showInfo)
	    		System.out.println("Cex from model checker: "+DCTL_MC.getWitnessAsMaps(negForm, model, ins));
	    	c.addRuns(DCTL_MC.getWitnessAsMaps(negForm, model, ins));
	    	//System.out.println(c);
	    	cexs.addLast(c.getRuns(ins)); // we add the counterexample to the collection of counterexamples of the current instance
	    	this.processCounterExample(c);
	    	// TBD add the counterexample to the collection of counterexamples
	    	Program.myFactory.done();
	    	return false; // otherwise it is false
	    }
	   
	    //syntProgram = program;
	    //return DCTL_MC.mc_algorithm_eq(form, model);
	    //return true;
	}
	
	/**
	 * A method to perform model checking via Alloy, the model check property is a LTL formula
	 * and the procedure is a bounded model checking
	 * To be improved: use a template to generate these files
	 */
	private boolean alloyBoundedModelCheck(String ins, LinkedList<LinkedList<String>> cexs, int pathBound, int modelSize){
		System.out.println("Using Alloy BMC...");
		// We construct the spec
		String spec = "";
		String space = "    ";
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
				
		//we define the nodes
		spec += "abstract sig Node{}\n";
		for (int i=0; i<modelSize; i++){
			spec += "one sig Node"+i+" extends Node {}"+"\n";
		}
		spec += "\n";
		
		// at this point, all the nodes has been defined 
		// now, we define the propositions
		HashSet<String> props = new HashSet<String>();
		Iterator<String> it1 = definedProcesses.iterator();
		while (it1.hasNext()){
			String currentProcess = it1.next();
			LTS currentLTS = null;
			if (this.mapProcessModels.containsKey(currentProcess)) // if it is a process defined in the program
				currentLTS = mapProcessModels.get(currentProcess);
			else // otherwise is an instance with its own process definition
				currentLTS = this.mapInsModels.get(currentProcess);	
			props.addAll(currentLTS.getProps());
		}		
		
		spec += "abstract sig Prop{} \n";
		//now we write down the props to the spec
		Iterator<String> propsIt = props.iterator();
		while (propsIt.hasNext()){
			String p = propsIt.next();
			spec += "one sig "+p+" extends Prop{} \n";
			
			// and thecorresponding predicates
			spec += "pred "+p+"[m:TS,n:Node]{"+p+" in m.val[n]}\n";
		}
		
		// we define an abstract signature for Transition Systems
		spec += "abstract sig TS{\n nodes: set Node, \n succs: nodes -> nodes,\n val: nodes -> Prop,\n local: nodes -> nodes,\n env: nodes ->nodes \n }\n";
		
		// now we define all the instances
		Iterator<String> it2 = definedProcesses.iterator();
		while (it2.hasNext()){
			String currentProcess = it2.next();
			LTS currentLTS = null;
			if (this.mapProcessModels.containsKey(currentProcess)) // if it is a process defined in the program
				currentLTS = mapProcessModels.get(currentProcess);
			else // otherwise is an instance with its own process definition
				currentLTS = this.mapInsModels.get(currentProcess);	
			spec += currentLTS.getAlloySign();
			spec += "\n";
		}		
		
		// now we produce the sig for a trace composed of an interleaved execution
		spec += "sig elem{"+"\n";
		Iterator<String> it3 = mapInsModels.keySet().iterator();
		LinkedList<String> declaredIns = new LinkedList<String>(); // a variable to keep track of the declared instances
		int i = 0;
		while (it3.hasNext()){
			String currentInsName = it3.next();
			String currentInsType = mapInsModels.get(currentInsName).getName();
			declaredIns.add(currentInsName);
			if (it3.hasNext())
				spec +=  space + currentInsName+ ":Node,\n";
			else
				spec +=  space + currentInsName + ":Node \n";
			i++;
		}			
		spec += "}\n";
		spec += "{\n";
		// facts for the elements of the trace
		// the instances must belong to  the process nodes
		Iterator<String> it4 = mapInsModels.keySet().iterator();		
		//HashMap<String,String> declaredInsToSig = new HashMap<String, String>(); // a map to keep track of the type of each defined instance
		i = 0;
		while (it4.hasNext()){
			String currentInsName = it4.next();
			String currentInsType = mapInsModels.get(currentInsName).getName();
			spec +=  space + currentInsName+ " in "+mapInsModels.get(currentInsName).getName()+"Process.nodes \n";
			//declaredInsToSig.put("ins"+i, currentInsType); 
			i++;
		}			
		// the coordination axioms:
		spec += "-- these are the coordination axioms"+"\n";
		
		
		// first we collect the locks and shared vars
		LinkedList<String> gvars = mySpec.getGlobalVarsNames(); // the global vars including the locks
		// this hashmap returns for each instance the local name given to the var
		// if it already exists
		HashMap<String,HashMap<String, Var>> localNames = new HashMap<String,HashMap<String, Var>>();
		Iterator<String> itIns = mySpec.getInstanceTypes().keySet().iterator();
		while (itIns.hasNext()){	
			String current = itIns.next();
			localNames.put(current, new HashMap<String,Var>());
			for (int k=0; k<mySpec.getActualPars(current).size();k++){
				localNames.get(current).put(mySpec.getActualPars(current).get(k),mySpec.getFormalIthPar(current, k));
			}	
		}	
		
		// let us calculate the coordination axioms for the locks
		for (int k=0;k<mySpec.getLocks().size();k++){
			String currentGlobalLock = mySpec.getLocks().get(k).getName();
			Var lastVar = null; // the last var that has that value
			String lastIns = "";
			
			itIns = mySpec.getInstanceTypes().keySet().iterator();
			while(itIns.hasNext()){
				String currentIns = itIns.next();
				if (localNames.get(currentIns).containsKey(currentGlobalLock)){
					if (lastVar!=null){
						String leftPart = "Av_"+lastVar.getName()+"["+this.mapInsModels.get(lastIns).getName()+"Process,"+lastIns+ "]";
						String rightPart = "Av_"+localNames.get(currentIns).get(currentGlobalLock).getName()+"["+this.mapInsModels.get(currentIns).getName()+"Process,"+ currentIns+"]";
						spec+= leftPart + " iff " + rightPart+"\n";
					}
					lastVar = localNames.get(currentIns).get(currentGlobalLock);
					lastIns = currentIns;
				}			
			}
		}
		// end of the coordination axioms for the locks
		
		// coordination axioms for global vars
		LinkedList<String> globals = mySpec.getGlobalVarsNames();
		for (int j=0; j<globals.size(); j++){
			for (int k=0; k<this.instancesList.size()-1;k++){
				String leftPart = "Prop_"+globals.get(j) + "["+this.mapInsModels.get(this.instancesList.get(k)).getName()+"Process,"+this.instancesList.get(k)+"]";
				String rightPart = "Prop_"+globals.get(j) + "["+this.mapInsModels.get(this.instancesList.get(k+1)).getName()+"Process,"+this.instancesList.get(k+1)+"]";
				spec+= leftPart + " iff " + rightPart+"\n";
			}
		}
		
		spec += "} \n";
		
		spec += "--LTL model generation \n";
		spec += "one sig Ord {\n";
		spec += space + "First:  set elem, \n";
		spec += space + "Next: elem -> elem, \n";
		spec += space + "loop: elem -> elem \n";
		spec += "}";
		
		// restrictions about the possible executions
		spec += "{"+"\n";
		spec += space + "all s:elem | all s':Next[s] | ";
		
		Iterator<String> it5 = mapInsModels.keySet().iterator();
		i=0;
		while (it5.hasNext()){
			String currentInsName = it5.next();
			if (it5.hasNext())
				spec +=  "someTrans["+mapInsModels.get(currentInsName).getName()+"Process,s."+currentInsName+", s'."+currentInsName+"] and "; 
			else 
				spec +=  "someTrans["+mapInsModels.get(currentInsName).getName()+"Process,s."+currentInsName+", s'."+currentInsName+"] \n";
			i++;
		}
		
		// now we add an axiom to state that only one process can execute a local step per time
		spec += space + "all s:elem | all s':Next[s] | ";
		
		for (int j=0; j<declaredIns.size(); j++){
			if (j == 0)
				spec += "(localTrans["+mapInsModels.get(declaredIns.get(j)).getName()+"Process,s."+declaredIns.get(j)+", s'."+declaredIns.get(j)+"] iff ";
			if (j > 0)
				spec += "\n                         and (localTrans["+mapInsModels.get(declaredIns.get(j)).getName()+"Process,s."+declaredIns.get(j)+", s'."+declaredIns.get(j)+"] iff ";
			
			int lastPos = (j==declaredIns.size()-1)?j-1:declaredIns.size()-1;
			for (int k=0; k<declaredIns.size();k++){
				if (declaredIns.size()==2 && j != k){
					spec += "(envTrans["+mapInsModels.get(declaredIns.get(k)).getName()+"Process, s."+declaredIns.get(k)+",s'."+declaredIns.get(k)+"]))";
					continue;
				}
				if (j != k && k==0 && k<lastPos){
					spec += "(envTrans["+mapInsModels.get(declaredIns.get(k)).getName()+"Process, s."+declaredIns.get(k)+",s'."+declaredIns.get(k)+"] and ";
					continue;
				}
				if (k<lastPos && k==1 && j==0){
					spec += "(envTrans["+mapInsModels.get(declaredIns.get(k)).getName()+"Process, s."+declaredIns.get(k)+",s'."+declaredIns.get(k)+"] and ";
					continue;
				}
				if (j != k && k == lastPos){
					spec += "envTrans["+mapInsModels.get(declaredIns.get(k)).getName()+"Process, s."+declaredIns.get(k)+",s'."+declaredIns.get(k)+"]))";
					continue;
				}
				if (j == declaredIns.size()-1 && k == declaredIns.size()-2){
					spec += "envTrans["+mapInsModels.get(declaredIns.get(k)).getName()+"Process, s."+declaredIns.get(k)+",s'."+declaredIns.get(k)+"]))";
					continue;
				}
				if (j != k && k>0 && k < lastPos){
					spec += "envTrans["+mapInsModels.get(declaredIns.get(k)).getName()+"Process, s."+declaredIns.get(k)+",s'."+declaredIns.get(k)+"] and ";
					continue;
				}
			}
		}
		spec += "\n";
		// standard axioms for the trace
		spec += space + "pred/totalOrder[elem,First,Next] \n";
		spec +=	space + "loop= last -> back \n";
		spec += "} \n";
		
		
		spec += "-- it says that the the transition is local \npred localTrans[m:TS,n,n':Node]{\n     n' in ((m.local)[n]) \n }\n";
		spec += "-- it says that the the transition is non-local\npred envTrans[m:TS,n,n':Node]{\n    (n' in ((m.env)[n])) or  (n'= n) \n }\n";
		spec += "-- local or non-local transitions including stuttering \npred someTrans[m:TS,n,n':Node]{\n     localTrans[m, n,n'] or envTrans[m, n,n'] \n }\n";
		
		
		spec += "lone sig back in elem {}\n";

		spec += "fun first: one elem { Ord.First }\n";

		spec += "fun last: one elem { elem - ((Ord.Next).elem) }\n";

		spec += "fun next : elem->elem { Ord.Next + Ord.loop }\n";

		spec += "fun prev : elem->elem { ~this/next }\n";

		spec += "fun past : elem->elem { ^(~this/next) }\n";

		spec += "fun future : elem -> elem { elem <: *this/next }\n";

		spec += "pred infinite { some loop }\n";
		
		spec += "pred finite { no loop }\n";
		
		// now we write down the property
		spec += "pred gProp[]{\n";
		
		// we set the initial states for each instance
		for (int j=0; j<declaredIns.size(); j++){
			spec += "first."+declaredIns.get(j)+" = "+mapInsModels.get(declaredIns.get(j)).getInitialNode()+"\n";
		}
		spec += "\n";
		spec += "finite\n";
		spec += generateBoundedFormula(toNNF(new Negation(prop)))+"\n";
		spec += "}\n";
		spec += "run gProp for "+modelSize + " but " + pathBound + " elem";
				
		// by now we just print the formula
		//System.out.println(spec);
		try{			
			// we write the specification to a file
		    PrintWriter writer = new PrintWriter(outputPath+"DistPath.als", "UTF-8");
		    writer.print(spec);
		    writer.close();
		} catch (IOException e) {
			System.out.println("Error trying to write the alloy specifications for the bounded model checking.");
			System.out.println(e.getStackTrace());
		}
	
		A4Reporter rep = new A4Reporter();
		Module world = null;
		try{
			world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"DistPath.als");
			A4Options opt = new A4Options();
			opt.originalFilename = outputPath+"DistPath.als"; // the specification metamodel
			opt.solver = A4Options.SatSolver.SAT4J;
			Command cmd = world.getAllCommands().get(0);
			A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
			if  (sol.satisfiable()){ // counterexamples found
				// we write the cex, of found
				sol.writeXML("boundedcex.xml");
				// we read the counterexample
				CounterExample c = new CounterExample();
				//System.out.println(readAlloyCex("boundedcex.xml", pathBound));
				c.addRuns(readAlloyCex("boundedcex.xml", pathBound));
				cexs.addLast(c.getRuns(ins)); // we add the counterexample to the collection of counterexamples of the current instance
		    	this.processCounterExample(c); // and we process the counterexample
		    	System.out.println(readAlloyCex("boundedcex.xml", pathBound));
				return false; // and return false
			}
			else{ //otherwise program found
				syntProgram = spec; // if true we save the program
		    	return true;
				
			}
			//System.out.println(readAlloyCex("boundedcex.xml", pathBound));
			
			// we read the LTS
			//lts.fromAlloyXML(outputfilename);
			//System.out.println("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
			//lts.toDot("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
			
		}catch(Exception e){
			System.out.println("Input-Output Error trying to write Alloy files.");
			System.out.println(e);
		}	
		return false;
	}
	
	
	/**
	 * @param ins
	 * @return whether a given instance was changed or not
	 */
	public boolean changed(String ins){
		if (changed.containsKey(ins)){
			return changed.get(ins);
		}
		else{
			return false;
		}
	}
	
	public LTS getLTSForInstance(String ins){
		return this.mapInsModels.get(ins);
	}
	
	public LTS getLTSForProcess(String process){
		return this.mapProcessModels.get(process);
	}
	
	/**
	 * NOTE: This method only work for formula of the type AGp 
	 * @param f	the formula for which we will generated the bounded propositional formula
	 * @return	a propositional formula in Alloy capturing a trace witnessing its falsity
	 */
	private String generateBoundedFormula(Formula f){
		String result = "";
		if (f instanceof BoolVar){
			BoolVar theVar = (BoolVar) f;
			if (theVar.getOwner().equals("global"))
				result += ((BoolVar) f).toAlloy(mapInsModels.get(this.instancesList.get(0)).getName()+"Process", "s."+this.instancesList.get(0));
			else
				result +=  ((BoolVar) f).toAlloy(mapInsModels.get(theVar.getOwner()).getName()+"Process", "s."+theVar.getOwner());
			return result;
		}
		if (f instanceof Own){
			Own theVar = (Own) f;
			result +=  ((Own) f).toAlloy(mapInsModels.get(theVar.getOwner()).getName()+"Process", "s."+theVar.getOwner());
			return result;
		}
		if (f instanceof Conjunction){
			Conjunction theCon = (Conjunction) f;
			result +=  "("+generateBoundedFormula(theCon.getExpr1())+ ") and ("+generateBoundedFormula(theCon.getExpr2())+")";
			return result;
		}
		if (f instanceof Disjunction){
			Disjunction theDis = (Disjunction) f;
			result +=  generateBoundedFormula(theDis.getExpr1())+ " or "+generateBoundedFormula(theDis.getExpr2());
			return result;
		}
		if (f instanceof Negation){
			Negation theNeg = (Negation) f;
			result +=  "(not "+generateBoundedFormula(theNeg.getExpr1()) + ")";
			return result;
		}
		if (f instanceof AG){
			result += "infinite and ";
			result += "(all s: first.*(this/next) | "+generateBoundedFormula(((AG) f).getExpr1())+")" ;
			return result;
		}
		if (f instanceof EG){
			result += "infinite and ";
			result += "(all s: first.*(this/next) | "+generateBoundedFormula(((EG) f).getExpr1())+")" ;
			return result;
		}
		if (f instanceof EF){
			result += "(some s: first.*(this/next) | "+generateBoundedFormula(((EF) f).getExpr1())+")";
			return result;
		}
		if (f instanceof AF){
			result += "(some s: first.*(this/next) | "+generateBoundedFormula(((AF) f).getExpr1())+")";
			return result;
		}
		
		
		throw new RuntimeException("Bounded Model Checking not defined for the given formula");
	}
	
	/*
	 * A methods to read a counterexample from a Alloy  .xml file
	 */
	private LinkedList<HashMap<String,String>> readAlloyCex(String fileName, int pathBound){
		
		// vars initialization
		File inputFile = new File(fileName); // change this for the correct file
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// we extract a run for only one instance for now
		LinkedList<HashMap<String,String>> run = new LinkedList<HashMap<String, String>>();
		try{
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			
			// this is the root node
			org.w3c.dom.NodeList root = doc.getChildNodes();
			org.w3c.dom.NodeList nodes = root.item(0).getChildNodes();
			org.w3c.dom.Node instance = nodes.item(1);
			Iterator<String> insIt = this.mapInsModels.keySet().iterator();
		
			
			//System.out.println(this.instancesList);
			//String ins = "ins1"; // we try with this one
			//int size = 11; // the size of the run
		
		
			// we initialize the hashmaps
			for (int i=0;i<pathBound;i++){
				HashMap<String,String> map = new HashMap<String, String>();
				run.add(map);
			}
			//String[] inss = {"ins1","ins2","ins3"};
			System.out.println(instancesList);
			System.out.println(instancesList.size());
			for(int k=0;k<instancesList.size();k++){
				org.w3c.dom.Node insNode = XMLAlloy.getItemFromAttr(instance, instancesList.get(k));
				org.w3c.dom.NodeList nexts = insNode.getChildNodes();
				int j=0;
				for (int i=0; i<nexts.getLength(); i++){
					if (nexts.item(i).getNodeName().equals("tuple")){
						//System.out.println(XMLAlloy.getIthFromTuple(nexts.item(i), 2).getAttributes().getNamedItem("label").getTextContent());
						run.get(j).put(instancesList.get(k),XMLAlloy.removeDollarSign(XMLAlloy.getIthFromTuple(nexts.item(i), 2).getAttributes().getNamedItem("label").getTextContent()));
						j++;
					}
				}
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
		return run;
	}
	
	/**
	 * s1 IS INCLUDE IN s2
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean lInclusion(LinkedList<String> s1, LinkedList<String> s2){
		if (s1.size()>1){
			if (s2.size() >= s1.size()){
				int i=0;
				int j=0;
				while (j<s2.size()-1){
					if (s1.get(i).equals(s2.get(j)) && s1.get(i+1).equals(s2.get(j+1))){
						i++; // if found we search the next element
						j++;
						if (i == s1.size()-1)
							break;
					}
					else{
						j++; // otherwise we continue searching
					}
				}
				return (i == s1.size()-1);
			}
			else{
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	/**
	 * s2 IS INCLUDED IN s1
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean rInclusion(LinkedList<String> s1, LinkedList<String> s2){
		return lInclusion(s2,s1);
	}
	
	private boolean someInclusion(LinkedList<String> s1, LinkedList<String> s2){
		return lInclusion(s1, s2) || rInclusion(s1, s2);
	}
	
	private boolean disjoint(LinkedList<String> s1, LinkedList<String> s2){
		return !someInclusion(s1,s2);
	}

	/**
	 * 
	 * @param f	a formula
	 * @return the formula in negated normal form
	 */
	public static Formula toNNF(Formula f){
		if (f instanceof BoolVar || f instanceof BoolPar || f instanceof EqComparison)
			return new Negation(f);
		if (f instanceof Conjunction){
			return new Conjunction(toNNF(((Conjunction) f).getExpr1()),toNNF(((Conjunction) f).getExpr2()));
		}
		if (f instanceof Disjunction){
			return new Disjunction(toNNF(((Disjunction) f).getExpr1()),toNNF(((Disjunction) f).getExpr2()));
		} 
		if (f instanceof EX){
			return new EX(toNNF(((EX) f).getExpr1()));
		}
		if (f instanceof EF){
			return new EF(toNNF(((EF) f).getExpr1()));
		}
		if (f instanceof EG){
			return new EG(toNNF(((EG) f).getExpr1()));
		}
		if (f instanceof EW){
			return new EW(toNNF(((EW) f).getExpr1()), toNNF(((EW) f).getExpr1()));
		}
		if (f instanceof EU){
			return new EU(toNNF(((EU) f).getExpr1()), toNNF(((EU) f).getExpr1()));
		}
		if (f instanceof AX){
			return new AX(toNNF(((AX) f).getExpr1()));
		}
		if (f instanceof AF){
			return new AF(toNNF(((AF) f).getExpr1()));
		}
		if (f instanceof AG){
			return new AG(toNNF(((AG) f).getExpr1()));
		}
		if (f instanceof AW){
			return new AW(toNNF(((AW) f).getExpr1()), toNNF(((AW) f).getExpr1()));
		}
		if (f instanceof AU){
			return new AU(toNNF(((AU) f).getExpr1()), toNNF(((AU) f).getExpr1()));
		}
		if (f instanceof Negation){
			Negation theForm = (Negation) f;
			if (theForm.getExpr1() instanceof BoolVar || theForm.getExpr1() instanceof BoolPar || theForm.getExpr1() instanceof EqComparison)
				return theForm;
			if (theForm.getExpr1() instanceof Negation)
				return ((Negation) theForm.getExpr1()).getExpr1();
			if (theForm.getExpr1() instanceof EX){ // !EX(f1) = AX(!f1)
				Formula f1 = ((EX) (theForm.getExpr1())).getExpr1();
				return new AX(toNNF(new Negation(f1)));
			}
			if (theForm.getExpr1() instanceof Conjunction){
				Formula f1 = ((Conjunction) (theForm.getExpr1())).getExpr1();
				Formula f2 = ((Conjunction) (theForm.getExpr1())).getExpr2();
				return new Disjunction(toNNF(new Negation(f1)), toNNF(new Negation(f2)));
			}
			if (theForm.getExpr1() instanceof Disjunction){
				Formula f1 = ((Disjunction) (theForm.getExpr1())).getExpr1();
				Formula f2 = ((Disjunction) (theForm.getExpr1())).getExpr2();
				return new Conjunction(toNNF(new Negation(f1)), toNNF(new Negation(f2)));
			}
			if (theForm.getExpr1() instanceof EF){
				Formula f1 = ((EF) (theForm.getExpr1())).getExpr1();
				return new AG(toNNF(new Negation(f1)));
			}
			if (theForm.getExpr1() instanceof EG){
				Formula f1 = ((EG) (theForm.getExpr1())).getExpr1();
				return new AF(toNNF(new Negation(f1)));
			}
			if (theForm.getExpr1() instanceof EW){
				Formula f1 = ((EW) (theForm.getExpr1())).getExpr1();
				Formula f2 = ((EW) (theForm.getExpr1())).getExpr2();
				return new AU(toNNF(new Negation(f2)), toNNF(new Conjunction(new Negation(f1), new Negation(f2))));
			}
			if (theForm.getExpr1() instanceof EU){
				Formula f1 = ((EU) (theForm.getExpr1())).getExpr1();
				Formula f2 = ((EU) (theForm.getExpr1())).getExpr2();
				return new AW(toNNF(new Negation(f2)), toNNF(new Conjunction(new Negation(f1), new Negation(f2))));
			}
			if (theForm.getExpr1() instanceof AX){
				Formula f1 = ((AX) (theForm.getExpr1())).getExpr1();
				return new EX(toNNF(new Negation(f1)));
			}
			if (theForm.getExpr1() instanceof AF){
				Formula f1 = ((AF) (theForm.getExpr1())).getExpr1();
				return new EG(toNNF(new Negation(f1)));
			}
			if (theForm.getExpr1() instanceof AG){
				Formula f1 = ((AG) (theForm.getExpr1())).getExpr1();
				return new EF(toNNF(new Negation(f1)));
			}
			if (theForm.getExpr1() instanceof AW){
				Formula f1 = ((AW) (theForm.getExpr1())).getExpr1();
				Formula f2 = ((AW) (theForm.getExpr1())).getExpr2();
				return new EU(toNNF(new Negation(f2)), toNNF(new Conjunction(new Negation(f1), new Negation(f2))));
			}
			if (theForm.getExpr1() instanceof AU){
				Formula f1 = ((AU) (theForm.getExpr1())).getExpr1();
				Formula f2 = ((AU) (theForm.getExpr1())).getExpr2();
				return new EW(toNNF(new Negation(f2)), toNNF(new Conjunction(new Negation(f1), new Negation(f2))));
			}
		}
		return f;
		
	}
	
}
