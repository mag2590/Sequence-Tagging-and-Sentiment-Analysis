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
	
	public HashMap<String,String> attachPOS(String filename)
	{
		try
		{
			fr=new FileReader(filename);
			br=new BufferedReader(fr);
			String tempstr;
			String word,pos;
			String []tokens;
			while((s=br.readLine())!=null)
			{
				tempstr=tagger.tagString(s);
				tokens=tempstr.split(" ");
				for(int i=0;i<tokens.length;i++)
				{
					word=tokens[i].substring(0, tokens[i].indexOf('_'));
					pos=tokens[i].substring(tokens[i].indexOf('_')+1);
					wordPosMap.put(word, pos);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return wordPosMap;
	}
	public static void main(String args[])
	{
		HashMap<String,String> hm=new HashMap<String,String>();
		POSTagging pt=new POSTagging();
		hm=pt.attachPOS("Sample.txt");
		System.out.println(hm);
		
	}

}

