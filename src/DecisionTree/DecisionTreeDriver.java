/* blalbababf
 * Execution: -i Dataset/test1.txt -t  Dataset/test1.txt  -o output -f marital_status 
 */
package DecisionTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import DecisionTree.DataStructure.Customer;
import DecisionTree.DataStructure.CustomerDB;
import DecisionTree.DataStructure.Feature;
import DecisionTree.DataStructure.Node;

public class DecisionTreeDriver {

	public static String trainingData;
	public static String testingData;
	public Map<String, Boolean> isContinuous;
	public static String OutFilePath;
	public static String target;
	ArrayList<Feature> features = new ArrayList<Feature>();
	public static int DiscretizedRange = 2;
	
	public void Driver(String trainingFile, String testFile, String targetAttr, Map <String, Boolean> isContinous) {
		
		//read input parameters
		if(!cmdParser(trainingFile, testFile, targetAttr, isContinous)){
			System.out.println("Error");
			System.exit(0);
		}
		CustomerDB dataDB = new CustomerDB();
		Feature targetFeature = null;
		
		int[][] results = null;
		
		//identify features and the feature data type whether is string or integer
		features = readFeatures(isContinous, targetAttr);
		
		//read all the features and the customers from the input data and store in memory
		dataDB = readInputFile(trainingData);
		//dataDB.printDB();
		
		//set the properties of each feature e.g., discretize the continuous variables 
		//or calculate the number of class of each categorical variables
		setFeatureProperties(dataDB, features);

		//set the target of the decision tree
		int targetId=0;
		for(Feature f:features){
			if(f.isTarget()){
				targetId = f.getFeatureId();
				targetFeature = f;
				break;
			}
		}
		
		//run the decision tree model training
		DecisionTree ID3 = new DecisionTree(dataDB, targetAttr, features);
		ID3.run();
		
		//testing the trained decision tree
		Node root = ID3.getSplitNode();
		results = testing(root, targetFeature);
		
		//TODO need to be edited
		System.out.println("Result table");
		for(int i=0;i<targetFeature.getNumValues();i++){
			for(int j=0;j<targetFeature.getNumValues();j++){
				System.out.print(results[j][i]+" ");
			}
			System.out.println();
		}
	}

	private int[][] testing(Node splitNode, Feature targetFeature) {
		CustomerDB testDB = new CustomerDB();
		testDB = readInputFile(testingData);
		setFeatureProperties(testDB, features);
		ArrayList<String> hResultValue = new ArrayList<String>();
		int[][] results = new int[targetFeature.getNumValues()][targetFeature.getNumValues()];
		int modelResult;
		int actualResult;
		int targetId = testDB.getColNumOfFeature(targetFeature.getName());

		for(Customer customer:testDB.getDB()){
			Node nextNode = splitNode;
			
			//traversing the training model
			while(!nextNode.isLeafNode()){
				String value = customer.getAtt(testDB.getColNumOfFeature(nextNode.getSplitPointName()));

				for(Node child:nextNode.getChildren()){
					if(value.equals(child.getSplitValue())){
						nextNode = child;
						break;
					}
				}
			}
			
			if(!hResultValue.contains(nextNode.getPrediction())){
				hResultValue.add(nextNode.getPrediction());
				modelResult = hResultValue.indexOf(nextNode.getPrediction());
			}else{
				modelResult = hResultValue.indexOf(nextNode.getPrediction());
			}
			
			if(!hResultValue.contains(customer.getAtt(targetId))){
				hResultValue.add(customer.getAtt(targetId));
				actualResult = hResultValue.indexOf(customer.getAtt(targetId));
			}else{
				actualResult = hResultValue.indexOf(customer.getAtt(targetId));
			}
			//System.out.println("model: "+modelResult+" actural: "+actualResult);
			//System.out.println("model: "+nextNode.getPrediction()+" actural: "+customer.getAtt(targetFeature.getFeatureId()));
			results[modelResult][actualResult]++;
		}
		
		return results;
		
	}

	private static CustomerDB readInputFile(String inFile) {
		CustomerDB dataDB = new CustomerDB();
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(inFile));
			String newLine;
			
			//the header line
			newLine = br.readLine();
			ArrayList<String> header =  new ArrayList<String>(Arrays.asList(newLine.split(",")));
			dataDB.setHeader(header);
			
			//add all customers into database
			while((newLine = br.readLine()) != null){
				String[] customerInfo = newLine.split(",");
				ArrayList<String> attributes =  new ArrayList<String>();
				
				if(customerInfo.length != header.size()){
					continue;
				}
				
				for(String value:customerInfo){
					attributes.add(value);
				}
				
				Customer newCustomer = new Customer(attributes);

				dataDB.addCustomer(newCustomer);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dataDB;
	}

	private static void setFeatureProperties(CustomerDB bankDB,
			ArrayList<Feature> features) {
		for(Feature feature:features){
			
			if(!feature.isTarget()){
				feature.setDiscriminated(true);
			}

			//discretize continuous variables
			if(feature.getIsContinuous()){
				ArrayList<String> values = bankDB.getAttList(feature.getName());
				ArrayList<Integer> valueList = new ArrayList<Integer>();

				int maxValue = 0;
				int minValue = Integer.parseInt(values.get(0));
				int v;
				for(String value:values){
					v = Integer.parseInt(value);
					if(v > maxValue){
						maxValue = v;
					}else if(v < minValue){
						minValue = v;
					}

					if(!valueList.contains(v)){
						valueList.add(v);
					}
				}

				if(valueList.size() <= 10){
					feature.setValueNum(valueList.size());
				}else{
					feature.setRange((maxValue-minValue)/DiscretizedRange);
					feature.setMaxValue(maxValue);
					feature.setMinValue(minValue);
					feature.setValueNum(DiscretizedRange);

					bankDB.discretizeValues(maxValue, minValue, feature.getRange(), feature.getName());
				}

			}else{//calculate the number of the class of a category variable 
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> valueList = new ArrayList<String>();
				//get the column of the feature from the database
				values = bankDB.getAttList(feature.getName());

				for(String value:values){
					if(!valueList.contains(value)){
						valueList.add(value);
					}
				}

				feature.setValueNum(valueList.size());
			}
		}
		
	}

	private static ArrayList<Feature> readFeatures(Map<String, Boolean> isContinuous, String targetAttr) {
		ArrayList<Feature> features = new ArrayList<Feature>();
		int id = 0;
		
		//for feature set
		for(Map.Entry<String, Boolean> entry:isContinuous.entrySet()){
			Feature newFeature = new Feature(id);
			newFeature.setFeatureName(entry.getKey());
			newFeature.setTarget(false);
			newFeature.setFeatureType(entry.getValue());
			features.add(newFeature);
			id++;
		}
		
		//for the target feature
		Feature newFeature = new Feature(id);
		newFeature.setFeatureName(targetAttr);
		newFeature.setTarget(true);
		newFeature.setFeatureType(false);
		features.add(newFeature);
		
		return features;
	}

	private static boolean cmdParser(String trainingFile, String testFile, String targetAttr, Map <String, Boolean> isContinous) {
		boolean pass = false;

		if(trainingFile.length() != 0 && testFile.length() != 0 && targetAttr.length() != 0 && !isContinous.isEmpty()){
			trainingData = trainingFile;
			testingData = testFile;
			OutFilePath = "output.txt";//TODO for output interface
			target = targetAttr;

			System.out.println("Parameter Setting: -i "+trainingData
					+" -t "+testingData
					+" -o "+OutFilePath
					+" -f "+target);

			pass = true;
		}	
		
		return pass;
	}

}
