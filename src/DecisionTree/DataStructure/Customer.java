package DecisionTree.DataStructure;

import java.util.ArrayList;

public class Customer {
	private int customerId;
	private ArrayList<String> attributes = new ArrayList<String>();
	
	public Customer(ArrayList<String> attributes){		
	 this.attributes = attributes;
	}
		
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(String attr:attributes){
			sb.append(attr+" ");
		}
		
		return sb.toString();		
	}

	public String getAtt(int featureId) {
		return attributes.get(featureId);
	}
	
	public ArrayList<String> getAllAtt() {
		return attributes;
	}

	public void setDiscretizeValue(int id, double minValue, double range) {
		String strValue = attributes.get(id);
		double value = Double.parseDouble(strValue);
		
		value = (int) ((int) (value - minValue)/range);
		
		attributes.set(id, value+"");
		
	}

}
