import java.util.ArrayList;
import java.util.HashMap;


public class GroupProcessing {

	static HashMap<String, int[]> groupSentiDistro;
	static HashMap<Integer,ArrayList<Integer>> groupTransitionMatrix;
	static final double similarityThreshold = 0.75;
	static int groupIDCounter = 0;
	
	public GroupProcessing()
	{
		groupSentiDistro = new HashMap<String, int[]>();
	}
	
	public void createGroupMetadata(){
	}
	
	public void createGroupTransitiontable(){
		
	}
	
	public static void addTransition(int prev, int current){
	}
	
	public static int getOrCreateGroupIDFromSentence(String sentence){
		return -1;
	}
	
	public double calculateSimilarityBetweenGroups(int grp1, int grp2){
		return 0;
	}

	public double[] getSentimentDistributionByGroupID(String groupID){
		return new double[5];
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
