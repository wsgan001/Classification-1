package DecisionTree.DataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomerDB {
	private ArrayList<Customer> DB = new ArrayList<Customer>();
	private ArrayList<String> Header = new ArrayList<String>();
	
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
			String featureName) {
		int attrId = Header.indexOf(featureName);
		for(Customer customer:DB){		
			customer.setDiscretizeValue(attrId, minValue, range);
		}
	}

	public boolean isValueSame(String featureName) {
		int attrId = Header.indexOf(featureName);
		String value = DB.get(0).getAtt(attrId);
		for(int i=0;i<DB.size();i++){
			if(!value.equals( DB.get(i).getAtt(attrId))){
				return false;	
			}
		}

		return true;
	}

	public ArrayList<String> getAttList(String featureName) {
		ArrayList<String> list = new ArrayList<String>();
		int attrId = Header.indexOf(featureName);
		
		for(Customer customer:DB){
			list.add(customer.getAtt(attrId));
		}
		
		return list;
	}

	public String getSummary(String targetFeature) {
		HashMap<String, Integer> valueCount =  new HashMap<String, Integer>();
		int target = Header.indexOf(targetFeature);
		ArrayList<String> values = getAttList(targetFeature);
		String targetSummary="";
		
		if(isValueSame(targetFeature)){
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

	public void setHeader(ArrayList<String> header) {
		Header = header;
	}

	public int getColNumOfFeature(String featureName) {
		return Header.indexOf(featureName);
	}


}
