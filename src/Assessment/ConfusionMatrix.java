package Assessment;

public class ConfusionMatrix {
	private int TP;
	private int TN;
	private int FP;
	private int FN;
	
	public ConfusionMatrix(){
		TP=0;
		TN=0;
		FP=0;
		FN=0;
	}
	
	ConfusionMatrix(int truePositive,int trueNegative,int falsePositive,int falseNegative){
		this.TP=truePositive;
		this.TN=trueNegative;
		this.FP=falsePositive;
		this.FN=falseNegative;
	}
	
	public void setTP(int tp){
		TP = tp;
	}
	
	public void setTN(int tn){
		TN = tn;
	}
	
	public void setFP(int fp){
		FP = fp;
	}
	
	public void setFN(int fn){
		FN = fn;
	}
	
	
	public double getAccuracy(){
		return (TP+TN)/(double)(TP+TN+FP+FN);
	}
	public double getPrecision(){
		return TP/(double)(TP+FP);
	}
	public double getRecall(){
		return TP/(double)(TP+FN);
	}
	public double getFPR(){
		return FP/(double)(TN+FP);
	}
	public double getFScore(){
		return 2*TP/(double)(2*TP+FN+FP);
	}
}
