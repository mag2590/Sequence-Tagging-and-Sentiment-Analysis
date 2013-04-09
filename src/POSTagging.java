import java.io.*;
import java.util.HashMap;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTagging
{
	MaxentTagger tagger;
	
	FileReader fr;
	BufferedReader br;
	String inputFileName;
	HashMap<String,String> wordPosMap=new HashMap<String,String>();
	String s, tagged;

	public POSTagging()
	{
		try {
			tagger = new MaxentTagger("english-left3words-distsim.tagger");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String,String> attachPOS(String line)
	{
		try
		{
			String tempstr;
			String word,pos;
			String []tokens;
			tempstr=tagger.tagString(line);
			tokens=tempstr.split(" ");
			for(int i=0;i<tokens.length;i++){
				
				word=tokens[i].substring(0, tokens[i].indexOf('_'));
				pos=tokens[i].substring(tokens[i].indexOf('_') + 1);
				wordPosMap.put(word, pos);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return wordPosMap;
	}
	
	
	public HashMap<String,Integer> getPOSFeatureVector(String line){
		
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		String tempstr;
		String word,pos;
		String []tokens;
		int count;
		tempstr=tagger.tagString(line);
		tokens=tempstr.split(" ");
		for(int i=0;i<tokens.length;i++){
			
			word= tokens[i].substring(0, tokens[i].indexOf('_'));
			pos= tokens[i].substring(tokens[i].indexOf('_') + 1);
			
			if(!map.containsKey(pos))
				count = 0;
			else
				count = map.get(pos);
			
			count++;
			map.put(pos, count);
		}
		
		return map;
	}
	
	public static void main(String args[])
	{
		HashMap<String,String> hm=new HashMap<String,String>();
		POSTagging pt=new POSTagging();
		hm=pt.attachPOS("Sample txt");
		System.out.println(hm);
		
	}

}

