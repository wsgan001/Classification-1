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
import java.util.Map;

import Assessment.ConfusionMatrix;
import DecisionTree.DataStructure.Customer;
import DecisionTree.DataStructure.CustomerDB;
import DecisionTree.DataStructure.Feature;
import DecisionTree.DataStructure.Node;

import com.opencsv.CSVReader;

public class DecisionTreeDriver {

	public static String trainingData;
	public static String testingData;
	public Map<String, Boolean> isContinuous;
	public static String OutFilePath;
	public static String target;
	static ArrayList<Feature> features = new ArrayList<Feature>();
	public static int DiscretizedRange = 2;
	
	public static String run(String trainingFile, String testFile, String targetAttr, Map <String, Boolean> isContinous) {
		
		//read input parameters
		if(!cmdParser(trainingFile, testFile, targetAttr, isContinous)){
			System.out.println("Error");
			System.exit(0);
		}
		CustomerDB dataDB = new CustomerDB();
		Feature targetFeature = null;
		
		//identify features and the feature data type whether is string or integer
		features = readFeatures(isContinous, targetAttr);
		
		//read all the features and the customers from the input data and store in memory
		dataDB = readInputFile(trainingData);
		//dataDB.printDB();
		
		//set the properties of each feature e.g., discretize the continuous variables 
		//or calculate the number of class of each categorical variables
		setFeatureProperties(dataDB, features);

		//set the target of the decision tree
		for(Feature f:features){
			if(f.isTarget()){
				targetFeature = f;
				break;
			}
		}
		
		//run the decision tree model training
		DecisionTree ID3 = new DecisionTree(dataDB, targetAttr, features);
		ID3.run();
		
		//testing the trained decision tree
		Node root = ID3.getSplitNode();
		CustomerDB resultDB = new CustomerDB();
		resultDB = testing(root, targetFeature);
		resultDB.printDB();
		
		ArrayList<ConfusionMatrix> report = modelAssessment(resultDB);
		
		double precision = 0;
		double recall = 0;
		double accuracy = 0;
		double fpr = 0;
		double fScore = 0;
		for(ConfusionMatrix cm:report){
			precision += cm.getPrecision();
			recall += cm.getRecall();
			accuracy += cm.getAccuracy();
			fpr += cm.getFPR();
			fScore += cm.getFScore();
		}
		precision /= report.size();
		recall /= report.size();
		accuracy /= report.size();
		fpr /= report.size();
		fScore /= report.size();
		
		return "Precision = "+precision+"\n"
			  +"Recall(True Positve Rate) = "+recall+"\n"
			  +"Accuracy = "+accuracy+"\n"
			  +"False Positive Rate = "+fpr+"\n"
			  +"F1 Score = "+fScore+"\n";
	}

	private static ArrayList<ConfusionMatrix> modelAssessment(CustomerDB resultDB) {
		ArrayList<String> header = resultDB.getHeader();
		ArrayList<ConfusionMatrix> report = new ArrayList<ConfusionMatrix>();
		
		int size = 0;
		for(int i = 1; i<header.size();i++){
			ArrayList<String> recall = resultDB.getAttList(header.get(i));
			for(int j = 1; j<header.size();j++){
					size += Integer.parseInt(recall.get(j-1));
			}
		}
		
		for(int i = 1; i<header.size();i++){
			ConfusionMatrix cm = new ConfusionMatrix();
			ArrayList<String> recall = resultDB.getAttList(header.get(i));
			int tp = Integer.parseInt(recall.get(i-1));
			cm.setTP(tp);
			
			int fp = 0;
			for(int j = 1; j<header.size();j++){
				if(j != i){
					fp += Integer.parseInt(recall.get(j-1));
				}
			}
			cm.setFP(fp);
			
			ArrayList<String> row = resultDB.getCustomer(i-1).getAllAtt();
			int fn = 0;
			for(int j = 1; j<header.size();j++){
				if(j != i){
					fn += Integer.parseInt(row.get(j));
				}
			}
			cm.setFN(fn);
			
			int tn = size - tp - fn - fp;
			cm.setTN(tn);
			
			report.add(cm);
		}
		
		return report;
	}

	private static CustomerDB testing(Node splitNode, Feature targetFeature) {
		CustomerDB testDB = new CustomerDB();
		testDB = readInputFile(testingData);
		setFeatureProperties(testDB, features);
		ArrayList<String> hResultValue = new ArrayList<String>();
		int[][] results = new int[targetFeature.getNumValues()+1][targetFeature.getNumValues()+1];
		int modelResult;
		int actualResult;
		int targetId = testDB.getColNumOfFeature(targetFeature.getName());

		hResultValue.add("Model Result");
		
		for(Customer customer:testDB.getDB()){
			Node nextNode = splitNode;
			Node preNode = null;
			
			//traversing the training model
			while(!nextNode.isLeafNode() && preNode != nextNode){
				String value = customer.getAtt(testDB.getColNumOfFeature(nextNode.getSplitPointName()));
				preNode = nextNode;

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
		
		//store the result into DB
		CustomerDB resultDB = new CustomerDB();
		
		resultDB.setHeader(hResultValue);
		
		for(int i=1;i<hResultValue.size();i++){
			ArrayList<String> attributes = new ArrayList<String>();
			attributes.add(hResultValue.get(i));
			
			for(int j=1;j<hResultValue.size();j++){
				attributes.add(results[i][j]+"");
			}
			
			resultDB.addCustomer(new Customer(attributes));
		}
		
		return resultDB;
		
	}

	private static CustomerDB readInputFile(String inFile) {
		CustomerDB dataDB = new CustomerDB();
		BufferedReader br = null;
		
		try {
			CSVReader reader = new CSVReader(new FileReader(inFile));
		    String [] nextLine;
		    
		    //the header line
		    nextLine = reader.readNext();
			ArrayList<String> header =  new ArrayList<String>(Arrays.asList(nextLine));
			dataDB.setHeader(header);
			
		    while ((nextLine = reader.readNext()) != null) {
		    	String[] customerInfo = nextLine;
				ArrayList<String> attributes =  new ArrayList<String>();
				
				if(customerInfo.length != header.size()){
					System.out.println("one data has been deleted");
					continue;
				}
				
				for(String value:customerInfo){
					attributes.add(value);
				}
				
				Customer newCustomer = new Customer(attributes);

				dataDB.addCustomer(newCustomer);
		    }
			
			reader.close();
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
				ArrayList<Double> valueList = new ArrayList<Double>();

				double maxValue = 0;
				double minValue = Double.parseDouble(values.get(0));
				double v;
				for(String value:values){
					v = Double.parseDouble(value);
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
			Feature newFeature = new Feature();
			newFeature.setFeatureName(entry.getKey());
			newFeature.setTarget(false);
			newFeature.setFeatureType(entry.getValue());
			features.add(newFeature);
			if(targetAttr.equals(newFeature.getName())){
				newFeature.setTarget(true);
			}
		}
		
		return features;
	}

	private static boolean cmdParser(String trainingFile, String testFile, String targetAttr, Map <String, Boolean> isContinous) {
		boolean pass = false;

		if(trainingFile.length() != 0 && testFile.length() != 0 && targetAttr.length() != 0 && !isContinous.isEmpty()){
			trainingData = trainingFile;
			testingData = testFile;
			OutFilePath = "output.txt";
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
