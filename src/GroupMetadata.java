import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;


public class GroupMetadata {

	HashSet<String> constituents;
	int count;
	int groupID;
	private static int lastUsedID;
	TreeMap<Integer,Integer> sentiFV;
	
	public GroupMetadata(){
		lastUsedID = -1;
	}
	
	public GroupMetadata(HashSet<String> constituents, int sentiScore){
		this.constituents = constituents;
		this.groupID = lastUsedID + 1;
		lastUsedID++;
		this.count = 1;
		this.sentiFV = new TreeMap<Integer, Integer>();
		this.sentiFV.put(sentiScore,1);
	}
	
	public void addSentiScoreToSentiFV(int sentiScore){
		
		int x;
		if(sentiFV.containsKey(sentiScore))
			x = sentiFV.get(sentiScore);
		else
			x = 0;
		
		x++;
		sentiFV.put(sentiScore,x);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
