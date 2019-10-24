package PS;
import java.util.*;

/**
 * This class is used for saving counterexamples, it implements the basic methods to manage coutnerexamples
 * @author Pablo
 *
 */

public class CounterExample {
	HashMap<String, LinkedList<String>> runs; // maps for each instance process the run corresponding to the counter example, the order is not important for us
											  // since the backtracking inspects every possibility
	
	public CounterExample(){
		this.runs = new HashMap<String, LinkedList<String>>();
	}
	
	public void addRun(String process, LinkedList<String> run){
		runs.put(process, run);
	}
	
	public LinkedList<String> getRuns(String process){
		return runs.get(process);
	}
	
	public void addRuns(LinkedList<HashMap<String, String>> cex){
		System.out.println(cex);
		for (int i=0; i< cex.size(); i++){
			if (i==0){ // if it is the first state
				HashMap<String, String> currentMap = cex.get(i);
				Iterator<String> it = currentMap.keySet().iterator();
				while (it.hasNext()){
					String currentProcess = it.next();
					LinkedList<String> listStates = new LinkedList<String>();
					listStates.add(cex.get(i).get(currentProcess));
					runs.put(currentProcess, listStates);
				}
				
			}
			else{ // otherwise it is another run
				HashMap<String, String> currentMap = cex.get(i);
				Iterator<String> it = currentMap.keySet().iterator();
				while (it.hasNext()){
					String currentProcess = it.next();
					if (!runs.get(currentProcess).getLast().equals(cex.get(i).get(currentProcess))) // avoid repeated elements
						runs.get(currentProcess).addLast(cex.get(i).get(currentProcess));
				}
			}
		}
	}
	
	public boolean equals(Object obj){
		if (obj == null || obj instanceof CounterExample)
			return false;
		else{
			CounterExample another = (CounterExample) obj;
			Iterator<String> it = runs.keySet().iterator();
			while (it.hasNext()){
				String currentProcess = it.next();
				LinkedList<String> anotherRun = another.getRuns(currentProcess);
				LinkedList<String> myRun = runs.get(currentProcess);
				if (anotherRun.size() == myRun.size()){
					for (int j = 0; j <  anotherRun.size(); j++){
						if (!anotherRun.get(j).equals(myRun.get(j)))
							return false;
					}
				}
				else
					return false;
			}
		}
		return true;
	}
	
	public String toString(){
		String result = "";
		Iterator<String> it = runs.keySet().iterator();
		while (it.hasNext()){
			String current = it.next();
			result += "["+current +" : "+ runs.get(current) + "]";
		}
		return result;
	}

}
