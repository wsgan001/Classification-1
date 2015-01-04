package DecisionTree.DataStructure;

public class Feature {
	private String name;
	private boolean discriminated;
	private boolean target;
	private int featureId;
	private boolean isContinuous;
	private int numValues;
	private int maxValue;
	private int minValue;
	private int range; //discretize continuous value
	
	public Feature(){
		maxValue = 0;
		minValue = 0;
		range = 0;
	}

	public Feature(int id) {
		featureId = id;
	}

	public void setFeatureName(String featureName) {
		this.name = featureName;		
	}

	public void setFeatureType(boolean isContinuous) {
		this.isContinuous = isContinuous;		
	}

	public String getName() {
		return name;
	}

	public boolean getIsContinuous() {
		return isContinuous;
	}

	public void setValueNum(int size) {
		this.numValues = size;
		
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	
	public String toString(){
		return 	"Name("+name+") ID("+featureId+") Type("+isContinuous+") numValues("+numValues+") max("+maxValue+") min("+minValue+")"
			  + " discriminated("+discriminated+") ";
	}

	public int getRange() {
		return range;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}

	public void setDiscriminated(boolean discriminated) {
		this.discriminated = discriminated;
	}

	public int getFeatureId() {
		return featureId;
	}

	public boolean isTarget() {
		return target;
	}

	public boolean isDiscriminated() {
		return discriminated;
	}

	public int getNumValues() {
		return numValues;
	}

}
