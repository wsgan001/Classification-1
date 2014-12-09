package DecisionTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import DecisionTree.DataStructure.Customer;
import DecisionTree.DataStructure.CustomerDB;
import DecisionTree.DataStructure.Feature;
import DecisionTree.DataStructure.Node;

public class DecisionTree {

	private CustomerDB DB;
	private Node splitNode;
	private ArrayList<CustomerDB> childrenDB = new ArrayList<CustomerDB>();
	private ArrayList<Feature> features;
	private int target;
	private Feature splitPoint;

	public DecisionTree(CustomerDB bankDB, int target, ArrayList<Feature> features) {
		this.DB = bankDB;
		this.target = target;
		this.features = features;
		this.splitNode = new Node();
	}

	public DecisionTree(Node newSplitNode, CustomerDB bankDB, int target, ArrayList<Feature> features) {
		this.DB = bankDB;
		this.target = target;
		this.features = features;
		this.splitNode = newSplitNode;
	}

	public void run() {
		ArrayList<String> values = DB.getAttList(target);
		double targetE = calEntropy(values);
		
		//select the feature for splitting the data
		splitPoint = featureSelection(targetE);
		int attId = splitPoint.getFeatureId();
		splitNode.setSplitPoint(splitPoint);
		
		ArrayList<String> hDB = new ArrayList<String>();
		for(int i=0;i<DB.size();i++){
			Customer customer = DB.getCustomer(i);
			
			if(hDB.contains(customer.getAtt(attId))){
				CustomerDB childDB = childrenDB.get(hDB.indexOf(customer.getAtt(attId)));
				childDB.addCustomer(customer);
			}else{
				CustomerDB childDB =new CustomerDB();
				childDB.addCustomer(customer);
				hDB.add(customer.getAtt(attId));
				childrenDB.add(childDB);
			}
		}
		
		ArrayList<Feature> newFeatures = new ArrayList<Feature>();
		for(Feature f:features){
			if(attId != f.getFeatureId() && f.isDiscriminated() && !f.isTarget()){
				newFeatures.add(f);
			}
		}
		System.out.println("Feature Name: "+splitPoint.getName());
		System.out.println("childrenDB size: "+childrenDB.size());
		if(newFeatures.size()>0){
			//recursively growing the tree
			for(int i=0;i<childrenDB.size();i++){
				if(!childrenDB.get(i).isValueSame(target)){
					Node newSplitNode = new Node(hDB.get(i));
					splitNode.addChildNode(newSplitNode);
					DecisionTree ID3 = new DecisionTree(newSplitNode, childrenDB.get(i), target, newFeatures);
					ID3.run();
				}else{
					// stop condition 1: the value of the child node is the same
					Node newSplitNode = new Node(hDB.get(i));
					newSplitNode.setLeafNode(true);
					newSplitNode.setPrediction(childrenDB.get(i).getSummary(target));
					splitNode.addChildNode(newSplitNode);
					
				}
			}
		}else{
			//stop condition 2: run out of splitting features
			for(int i=0;i<childrenDB.size();i++){
				Node newSplitNode = new Node(hDB.get(i));
				newSplitNode.setLeafNode(true);
				newSplitNode.setPrediction(childrenDB.get(i).getSummary(target));
				splitNode.addChildNode(newSplitNode);
			}
		}
		
	}

	private double calEntropy(ArrayList<String> values) {
		HashMap<String, Integer> valueCount =  new HashMap<String, Integer>();
		int v;
		double size = (double)values.size();
		double entropy = 0.0;
		for(String value:values){
			if(valueCount.containsKey(value)){
				v = valueCount.get(value)+1;
				valueCount.put(value, v);
			}else{
				valueCount.put(value, 1);
			}
		}
		
		Iterator<Map.Entry<String, Integer>> it = valueCount.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,Integer> entry = it.next();
		    entropy += -((double)entry.getValue()/size)*log2((double)entry.getValue()/size);
		}
		return entropy;
	}

	private double log2(double d) {
		return Math.log(d)/Math.log(2.0);
	}

	//TODO the function for feature selection
	private Feature featureSelection(double targetE) {
		HashMap<Integer, Double> Entropy = new HashMap<Integer, Double>();
		for(Feature f:features){
			if(!f.isDiscriminated() || f.isTarget()){
				continue;
			}
			
			ArrayList<String> attValues = DB.getAttList(f.getFeatureId());
			ArrayList<String> targetValues = DB.getAttList(target);
			HashMap<String,ArrayList<String>> valueLists = new HashMap<String, ArrayList<String>>();
			
			for(int i=0;i<attValues.size();i++){
				if(valueLists.containsKey(attValues.get(i))){
					valueLists.get(attValues.get(i)).add(targetValues.get(i));
				}else{
					ArrayList<String> values = new ArrayList<String>();
					values.add(targetValues.get(i));
					valueLists.put(attValues.get(i), values);
				}
			}
			
			double valueEntropy = 0.0;
			Iterator<Map.Entry<String, ArrayList<String>>> it = valueLists.entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry<String, ArrayList<String>> entry = it.next();
			    valueEntropy += (double)entry.getValue().size()*calEntropy(entry.getValue())/(double)attValues.size();
			}
			
			Entropy.put(f.getFeatureId(), valueEntropy);
		}

		double max = 0.0;
		int selectFeature=0;
		for(int i=0;i<features.size();i++){
			if(!features.get(i).isDiscriminated() || features.get(i).isTarget()){
				continue;
			} 
			if((targetE-Entropy.get(features.get(i).getFeatureId()))>max){
				max = (targetE-Entropy.get(features.get(i).getFeatureId()));
				selectFeature = i;
			}
		}
		return features.get(selectFeature);
	}

	public Node getSplitNode() {
		return splitNode;
	}
}
