import java.util.ArrayList;
import java.util.HashSet;


public class GroupMetadata {

	HashSet<String> constituents;
	int count;
	int groupID;
	private static int lastUsedID;
	ArrayList<Integer> sentiments;
	
	public GroupMetadata(){
		lastUsedID = -1;
	}
	
	public GroupMetadata(HashSet<String> constituents, int sentiScore){
		this.constituents = constituents;
		this.groupID = lastUsedID + 1;
		lastUsedID++;
		this.count = 1;
		this.sentiments = new ArrayList<Integer>();
		this.sentiments.add(sentiScore);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
