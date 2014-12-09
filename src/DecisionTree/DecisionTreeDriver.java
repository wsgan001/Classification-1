/*
 * Execution: 
 */
package DecisionTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import DecisionTree.DataStructure.Customer;
import DecisionTree.DataStructure.CustomerDB;
import DecisionTree.DataStructure.Feature;
import DecisionTree.DataStructure.Node;

public class DecisionTreeDriver {

	public static String trainingData;
	public static String testingData;
	public static String OutFilePath;
	public static String target;
	public static int DiscretizedRange = 4;
	
	public static void main(String[] args) {
		if(!cmdParser(args)){
			System.out.println("Error");
			System.exit(0);
		}
		CustomerDB bankDB = new CustomerDB();
		Feature targetFeature = null;
		int[][] results = null;
		
		ArrayList<Feature> features = readInputFile(bankDB, trainingData);
		//bankDB.printDB();
		
		setFeatureProperties(bankDB, features);

		int targetId=0;
		for(Feature f:features){
			if(f.isTarget()){
				targetId = f.getFeatureId();
				targetFeature = f;
				break;
			}
		}
		
		DecisionTree ID3 = new DecisionTree(bankDB, targetId, features);
		ID3.run();
		
		results = testing(ID3.getSplitNode(), targetFeature);
		
		for(int i=0;i<targetFeature.getNumValues();i++){
			for(int j=0;j<targetFeature.getNumValues();j++){
				System.out.print(results[j][i]+" ");
			}
			System.out.println();
		}
	}

	private static int[][] testing(Node splitNode, Feature targetFeature) {
		CustomerDB testDB = new CustomerDB();
		ArrayList<Feature> features = readInputFile(testDB, testingData);
		setFeatureProperties(testDB, features);
		ArrayList<String> hResultValue = new ArrayList<String>();
		int[][] results = new int[targetFeature.getNumValues()][targetFeature.getNumValues()];
		int modelResult;
		int actualResult;
		System.out.println(targetFeature.getNumValues());
		for(Customer customer:testDB.getDB()){
			Node nextNode = splitNode;
			while(!nextNode.isLeafNode()){
				String value = customer.getAtt(nextNode.getSplitPointId());

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
			
			if(!hResultValue.contains(customer.getAtt(targetFeature.getFeatureId()))){
				hResultValue.add(customer.getAtt(targetFeature.getFeatureId()));
				actualResult = hResultValue.indexOf(customer.getAtt(targetFeature.getFeatureId()));
			}else{
				actualResult = hResultValue.indexOf(customer.getAtt(targetFeature.getFeatureId()));
			}
			System.out.println("model: "+modelResult+" actural: "+actualResult);
			System.out.println("model: "+nextNode.getPrediction()+" actural: "+customer.getAtt(targetFeature.getFeatureId()));
			results[modelResult][actualResult]++;
		}
		
		return results;
		
	}

	private static ArrayList<Feature> readInputFile(CustomerDB bankDB, String inFile) {
		BufferedReader br = null;
		ArrayList<Feature> features = new ArrayList<Feature>();
		
		try {
			br = new BufferedReader(new FileReader(inFile));
			String newLine;
			
			features = readFeatures(newLine = br.readLine());
			
			while((newLine = br.readLine()) != null){
				String[] customerInfo = newLine.split(",");
				ArrayList<String> attributes =  new ArrayList<String>();
				
				if(customerInfo.length != 19){
					customerInfo[2].concat(" "+customerInfo[3]);
					for(int i=3;i<customerInfo.length-1;i++){
						customerInfo[i] = customerInfo[i+1];
					}
				}
				
				for(String value:customerInfo){
					attributes.add(value);
				}
				
				Customer newCustomer = new Customer(attributes);

				bankDB.addCustomer(newCustomer);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return features;
	}

	private static void setFeatureProperties(CustomerDB bankDB,
			ArrayList<Feature> features) {
		for(Feature feature:features){
			
			if(    feature.getFeatureId() == 0 || feature.getFeatureId() == 1 || feature.getFeatureId() == 2
				|| feature.getFeatureId() == 3 || feature.getFeatureId() == 4 || feature.getFeatureId() == 7
				|| feature.getFeatureId() == 9 || feature.getFeatureId() == 10){
				feature.setDiscriminated(false);
			}else{
				if(!feature.isTarget()){
					feature.setDiscriminated(true);
				}
				
				if(feature.getType() == 0){
					ArrayList<String> values = bankDB.getAttList(feature.getFeatureId());
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
						
						bankDB.discretizeValues(maxValue, minValue, feature.getRange(), feature.getFeatureId());
					}
					
				}else{
					ArrayList<String> values = new ArrayList<String>();
					ArrayList<String> valueList = new ArrayList<String>();
					
					values = bankDB.getAttList(feature.getFeatureId());
					
					for(String value:values){
						if(!valueList.contains(value)){
							valueList.add(value);
						}
					}
					
					feature.setValueNum(valueList.size());
				}
			}
			
		}
		
	}

	private static ArrayList<Feature> readFeatures(String strFeatures) {
		ArrayList<Feature> features = new ArrayList<Feature>();
		StringTokenizer tokens = new StringTokenizer(strFeatures,",\" ");
		String feature;
		int id = 0;
		//feature.equals("city") ||  feature.equals("state_province") ||
		while(tokens.hasMoreElements()){
			feature = tokens.nextToken();
			Feature newFeature = new Feature(id);
			newFeature.setFeatureName(feature);
			newFeature.setTarget(false);
			id++;
			if(    feature.equals("customer_region_id") || feature.equals("total_children") 
				|| feature.equals("num_children_at_home") || feature.equals("age") || feature.equals("year_income")){
				newFeature.setFeatureType(0);
			}else{
				newFeature.setFeatureType(1);
			}
			
			if(feature.equals(target)){
				newFeature.setTarget(true);
			}
			features.add(newFeature);	
		}
		
		return features;
	}

	private static boolean cmdParser(String[] args) {
		boolean pass = false;
		Options options = new Options();
		
		//add parameter
		options.addOption("i", true, "[input] training data");
		options.addOption("t", true, "[input] testing data");
		options.addOption("o", true, "[output] output directory");
		options.addOption("f", true, "[target] the prediction target");

		
		//parse parameters and initialize the program settings
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			if(cmd.hasOption("i") && cmd.hasOption("o")){
				trainingData = cmd.getOptionValue("i");
				testingData = cmd.getOptionValue("t");
				OutFilePath = cmd.getOptionValue("o");
				target = cmd.getOptionValue("f");
				
				System.out.println("Parameter Setting: -i "+trainingData
													+" -t "+testingData
													+" -o "+OutFilePath
													+" -f "+target);
				
				pass = true;
			}else{
				pass = false;
			}
		} catch (org.apache.commons.cli.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return pass;
	}

}
