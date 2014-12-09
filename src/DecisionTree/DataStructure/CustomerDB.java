package DecisionTree.DataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomerDB {
	private ArrayList<Customer> DB = new ArrayList<Customer>();
	
	public CustomerDB(){
		
	}
	
	public void addCustomer(Customer newCustomer){
		DB.add(newCustomer);
	}
	
	public Customer getCustomer(int index){
		return  DB.get(index);
	}

	
	public int size(){
		return DB.size();
	}
	
	public void printDB(){

		for(Customer c:DB){
			System.out.println(c.toString());
		}
	}



	public void discretizeValues(int maxValue, int minValue, int range,
			int id) {
		for(Customer customer:DB){		
			customer.setDiscretizeValue(id, minValue, range);
		}
	}

	public boolean isValueSame(int id) {
	
		String value = DB.get(0).getAtt(id);
		for(int i=0;i<DB.size();i++){
			if(!value.equals( DB.get(i).getAtt(id))){
				return false;	
			}
		}

		return true;
	}

	public ArrayList<String> getAttList(int featureId) {
		ArrayList<String> list = new ArrayList<String>();
		
		for(Customer customer:DB){
			list.add(customer.getAtt(featureId));
		}
		
		return list;
	}

	public String getSummary(int target) {
		HashMap<String, Integer> valueCount =  new HashMap<String, Integer>();
		ArrayList<String> values = getAttList(target);
		String targetSummary="";
		
		if(isValueSame(target)){
			targetSummary = DB.get(0).getAtt(target);
			if(targetSummary.equals("")){
				System.out.println(" value the smae targetSummary = null");
				System.exit(0);
			}
		}else{
			int v;

			for(String value:values){
				if(valueCount.containsKey(value)){
					v = valueCount.get(value)+1;
					valueCount.put(value, v);
				}else{
					valueCount.put(value, 1);
				}
			}
			
			int max=0;
			
			Iterator<Map.Entry<String, Integer>> it = valueCount.entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry<String,Integer> entry = it.next();
			    if(entry.getValue()>max){
			    	max = entry.getValue();
			    	targetSummary = entry.getKey();
			    }
			}
			if(targetSummary.equals("")){
				System.out.println("targetSummary = null");
				System.exit(0);
			}
		}
		return targetSummary;
	}

	public ArrayList<Customer> getDB() {
		return DB;
	}


}
