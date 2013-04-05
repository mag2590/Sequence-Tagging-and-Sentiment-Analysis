import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class GroupProcessing {

	static HashMap<Integer, int[]> groupSentiDistro;
	TreeMap<Integer,GroupMetadata> groupMap;
	static final double similarityThreshold = 0.75;
	static int groupIDCounter = 0;
	
	public GroupProcessing()
	{
		groupSentiDistro = new HashMap<Integer, int[]>();
		groupMap = new TreeMap<Integer, GroupMetadata>();
	}
	
	public void createGroupMetadata(){
	}
	
	public void createGroupTransitiontable(){
		
	}
	
	public int getOrCreateGroupIDFromSentence(HashSet<String> processedWords, int sentiScore){
		
		Iterator itr = groupMap.entrySet().iterator();
		HashSet<String> temp, intersectionMap, result = new HashSet<String>();
		int currentGroupID, maxSize = Integer.MIN_VALUE, mappedGroup = -1, c;
		GroupMetadata grpMeta;
		boolean found = false;
		Map.Entry me;
		
		while(itr.hasNext()){
			
			me = (Map.Entry) itr.next();
			currentGroupID = (Integer) me.getKey();
			temp = ((GroupMetadata) me.getValue()).constituents;
			
			if(temp==null) continue;
			
			intersectionMap = new HashSet<String>(processedWords);
			intersectionMap.retainAll(temp);
			
			if(intersectionMap.size() > maxSize){
				maxSize = intersectionMap.size();
				result = intersectionMap;
				mappedGroup = currentGroupID;
			}
		}
		
		if(maxSize > 5){
			found = true;
			grpMeta = groupMap.get(mappedGroup);
			grpMeta.count++;
			grpMeta.sentiments.add(sentiScore);
			groupMap.put(mappedGroup, grpMeta);
//			System.out.println(result.toString());
		}
		else{
			grpMeta = new GroupMetadata(processedWords, sentiScore);
			groupMap.put(grpMeta.groupID, grpMeta);
		}
		
//		System.out.println(result.toString());
//		System.out.println(mappedGroup);
		return mappedGroup;
	}
	
	public double calculateSimilarityBetweenGroups(int grp1, int grp2){
		return 0;
	}

	public double[] getSentimentDistributionByGroupID(String groupID){
		return new double[5];
	}
	
	public void printGroupMap(){
		
		Iterator itr = groupMap.entrySet().iterator();
		Map.Entry me;
		GroupMetadata meta;
		
		while(itr.hasNext()){
			
			me = (Map.Entry) itr.next();
			meta = (GroupMetadata)me.getValue();
			if(meta.count > 9){
			System.out.println((Integer)me.getKey() + "\t" + meta.count + "\t" + listToString(meta.sentiments));// + "\t" + meta.constituents.toString());
			System.out.println(meta.constituents.toString()+"\n");
			}
		}
		
		System.out.println("\n Size of Group : " + groupMap.size());
	}
	
	public String listToString(ArrayList<Integer> list){
		StringBuffer sb = new StringBuffer();
		for(int ss : list)
			sb.append(ss + " ");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
