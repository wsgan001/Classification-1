package DecisionTree.DataStructure;

import java.util.ArrayList;

public class Node {
	private Feature splitPoint;
	private String splitValue;
	private String prediction;
	private ArrayList<Node> children = new ArrayList<Node>();
	private boolean leaf;
	
	public Node(){
		leaf = false;
	}

	public Node(String splitValue) {
		this.splitValue = splitValue;
	}

	public void setSplitPoint(Feature splitPoint) {
		this.splitPoint = splitPoint;
	}

	public void addChildNode(Node newSplitNode) {
		children.add(newSplitNode);
	}

	public void setLeafNode(boolean leaf) {
		this.leaf = leaf;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

	public boolean isLeafNode() {
		return leaf;
	}

	public String getSplitPointName() {
		return splitPoint.getName();
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public String getSplitValue() {
		// TODO Auto-generated method stub
		return splitValue;
	}

	public String getPrediction() {
		return prediction;
	}
	
	
	
}
