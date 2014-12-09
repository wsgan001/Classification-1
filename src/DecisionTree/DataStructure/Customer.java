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
		
		sb.append("Customer ID("+attributes.get(0)+
				  "), Account Number("+attributes.get(1)+
				  "), Last Name("+attributes.get(2)+
				  "), First Name("+attributes.get(3)+
				  "), Address("+attributes.get(4)+
				  "), City("+attributes.get(5)+
				  "), State("+attributes.get(6)+
				  "), Post Code("+attributes.get(7)+
				  "), Country("+ attributes.get(8)+
				  "), Customer Region ID("+attributes.get(9)+
				  "), Phone("+attributes.get(10)+
				  "), Marital Status("+attributes.get(11)+
				  "), Gender("+attributes.get(12)+
				  "), Total Number of Children("+attributes.get(13)+
				  "), Number of Children at Home("+attributes.get(14)+
				  "), Education("+attributes.get(15)+
				  "), Member Card("+attributes.get(16)+
				  "), Age("+attributes.get(17)+
				  "), Year Income("+ attributes.get(18)+")");
		
		return sb.toString();		
	}

	public String getAtt(int featureId) {
		return attributes.get(featureId);
	}

	public void setDiscretizeValue(int id, int minValue, int range) {
		String strValue = attributes.get(id);
		int value = Integer.parseInt(strValue);
		
		value = (value - minValue)/range;
		
		attributes.set(id, value+"");
		
	}

}
