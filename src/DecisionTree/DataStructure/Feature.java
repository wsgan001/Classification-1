package DecisionTree.DataStructure;

public class Feature {
	private String name;
	private boolean discriminated;
	private boolean target;
	private boolean isContinuous;
	private int numValues;
	private double maxValue;
	private double minValue;
	private double range; //discretize continuous value
	
	public Feature(){
		target = false;
		maxValue = 0;
		minValue = 0;
		range = 0;
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

	public void setRange(double range) {
		this.range = range;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	
	public String toString(){
		return 	"Name("+name+") Type("+isContinuous+") numValues("+numValues+") max("+maxValue+") min("+minValue+")"
			  + " discriminated("+discriminated+") ";
	}

	public double getRange() {
		return range;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}

	public void setDiscriminated(boolean discriminated) {
		this.discriminated = discriminated;
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
