package howto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
	
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.java.sen.StringTagger;
import net.java.sen.Token;

public class Answer {
	private String Sentence; /** 文章 */
	private ArrayList<Query> QueryList = new ArrayList<Query>();  /** クエリの集合 */
	private final int Rank;  /** 上位何位の答えまでを表示するかを示す */
	private String[] Answers;  /** 最終的に提示する答えの文章群 */
	
	static{
        System.setProperty("sen.home", ".\\sen");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	}
	
	public Answer(String passage,int rank){
		Rank = rank;
		Sentence = makeSentence(passage);
		String morpheme = getMorpheme(new String(Sentence));
		//Analyze.fileSave("morpheme.txt", morpheme);
		extractWord(morpheme);
		for(Query q: QueryList){
			System.out.println("word:"+q.Word+",score:"+q.score);
		}
		createAnswers(new String(Sentence));
		for(int i=0;i<Answers.length;i++){
			System.out.println(Answers[i]);
		}
	}
	
	/** パス文から文章を取得するメソッド */
	private String makeSentence(String passage){
		try{
			String sentence="";
			StringTagger tagger = StringTagger.getInstance();
			String N = "\n";
			int length_N=N.length();
			String line;
			while(true){
				int index_N = passage.indexOf(N);
				if( index_N == -1 )break;
				line = passage.substring(0,index_N+length_N);
				passage = passage.substring(index_N+length_N, passage.length());//表記前の部分の削除    		
				Token[] token = tagger.analyze(line);
				boolean flag=false;   //助詞が文中に存在するかどうか(文かどうか)
				boolean flag2=false;
				boolean flag3=false;
				for(int i=0;i < token.length;i++){
					String hinshi=token[i].getPos();
					int H = hinshi.indexOf("-");
					if(H != -1){
						String sub=hinshi.substring( 0 , H );
						if(sub.equalsIgnoreCase("助詞")){
							flag = true;
						}
					}
					if(flag==true){
						for(int j=0;j < token.length;j++){
							hinshi=token[j].getPos();
							H = hinshi.indexOf("-");
							if(H != -1){
								String sub=hinshi.substring( 0 , H );
								if(sub.equalsIgnoreCase("動詞")){
									flag2 = true;
									break;
								}
							}
						}
					}
					if(flag2==true)break;
				}
				if(flag2==true){
					int I = token.length;
					String Lhinshi1=token[I-1].getPos();
					String Lhinshi2=token[I-2].getPos();
					//String Lhinshi3=token[I-3].getPos();
					int L1 = Lhinshi1.indexOf("-");
					int L2 = Lhinshi2.indexOf("-");
					if((L1 != -1)&&(L2 != -1)){
						String sub1=Lhinshi1.substring( 0 , L1 );
						String sub2=Lhinshi2.substring( 0 , L2 );
						if(((sub1.equalsIgnoreCase("記号"))&&(sub2.equalsIgnoreCase("動詞"))) || ((sub1.equalsIgnoreCase("記号"))&&(sub2.equalsIgnoreCase("助詞")))){
							flag3 = true;
						}
					}
					if(L1 != -1){
						String sub1=Lhinshi1.substring( 0 , L1 );
						if((sub1.equalsIgnoreCase("動詞"))||(sub1.equalsIgnoreCase("助詞"))||(sub1.equalsIgnoreCase("記号"))&&(Lhinshi2.equalsIgnoreCase("助動詞"))){
							flag3 = true;
						}
					}
					if(Lhinshi1.equalsIgnoreCase("助動詞")){
						flag3 = true;
					}
				}
				if(flag3==false)continue;
				for (int i = 0; i < token.length; i++) {
					String tango1=token[i].toString();
					sentence+=tango1;
				}
				sentence+=N;
			}
			return sentence;
		}catch(Exception ex){
			return "";
		}
	}
	
	/** 引数の文章から形態素を獲得するメソッド */
	private String getMorpheme(String sentence){
        try {
        	String morpheme="";
        	StringTagger tagger = StringTagger.getInstance();
			String N = "\n";
			int length_N=N.length();
    	    String line;
    	    while(true){
				int index_N = sentence.indexOf(N);
				if( index_N == -1 )break;
				line = sentence.substring(0,index_N+length_N);
				sentence = sentence.substring(index_N+length_N, sentence.length());//表記前の部分の削除    	
    	    	Token[] token = tagger.analyze(line);
    	    	boolean flag=false;   //助詞が文中に存在するかどうか(文かどうか)
    	    	for(int i=0;i < token.length;i++){
    	    		String hinshi=token[i].getPos();
    	    		int H = hinshi.indexOf("-");
    	    		if(H != -1){
    	    			String sub=hinshi.substring( 0 , H );
    	    			if(sub.equalsIgnoreCase("助詞")){
    	    				flag = true;
    	    				break;
    	    			}
    	    		}
    	    	}
    	    	if(flag==false)continue;
                for (int i = 0; i < token.length; i++) {
                	morpheme+=(token[i] + "\t" + token[i].getBasicString()  + "\t" + token[i].getPos() +"\r\n");  //ファイル出力
                }
    	    }
    	    return morpheme;
        } catch (IOException e) {
        	e.printStackTrace();
        	return "";
        }
	}
	
	/** 引数の形態素から単語を抽出するメソッド(QueryListの構築を行うメソッド) */
	private void extractWord(String morpheme){
		HashMap<String,Integer> tm = new HashMap<String,Integer>();
		String N = "\n";
		int length_N=N.length();
		String line;
		
		/* HashMapオブジェクトの構築 */
		while(true){//１：ファイルから文字列を読み込む
			int index_N = morpheme.indexOf(N);
			if( index_N == -1 )break;
			line = morpheme.substring(0,index_N+length_N);
			morpheme = morpheme.substring(index_N+length_N, morpheme.length());//表記前の部分の削除    	
			String[] words = line.split("\\s");//２：文字列から単語を抽出する
			String hinshi=words[2];
			if(hinshi.equalsIgnoreCase("未知語"))continue;
			if(hinshi.equalsIgnoreCase("連体詞"))continue;
			if(hinshi.equalsIgnoreCase("動詞-非自立"))continue;
			if(hinshi.equalsIgnoreCase("動詞-接尾"))continue;
		
			int H = hinshi.lastIndexOf("-");
			if(H != -1){
				String sub=hinshi.substring( 0 , H );
				if(sub.equalsIgnoreCase("名詞-非自立"))continue;
			}
			
			char mozi=hinshi.charAt(0);
			if(mozi=='記')continue;
			if(mozi=='助')continue;
			//if(mozi=='副')continue;
			if(mozi=='接')continue;
			String kihon=words[1];
			if(kihon.equalsIgnoreCase("ある"))continue;
			if(kihon.equalsIgnoreCase("する"))continue;
			if(kihon.equalsIgnoreCase("なる"))continue;
			if(kihon.equalsIgnoreCase("いる"))continue;
			if(kihon.equalsIgnoreCase("回答"))continue;
			if(kihon.equalsIgnoreCase("質問"))continue;
			//３：左側の単語をマップに登録する
			String tango=words[1];
			if(!tm.containsKey(tango)){
				tm.put(tango,1);
			}else{
				tm.put(tango,tm.get(tango).intValue()+1);
			}
		}
		
		/* QueryListの構築 */
		//int threshold = 10;  //単語の抽出時の閾値
		for(String s : tm.keySet()){
		//	System.out.println(s + tm.get(s));
			int score = tm.get(s);
			sortQueryList(QueryList,new Query(s,score));
			/*
			if(score >= threshold){
				QueryList.add(new Query(s,score));
				System.out.println("word:"+s+",score:"+score);
			}
			*/
		}
		int length = QueryList.size();
		int rank = 25; //QueryListの要素数
		for(int i=0;i<length-rank;i++){
			QueryList.remove(rank);
		}
	}
	
	/** 答えの文章を探すメソッド */
	private void createAnswers(String sentence){
    	try {
       		StringTagger tagger = StringTagger.getInstance();
       		String line;
    		String N = "\n";
    		int length_N=N.length();
       		ArrayList<Query> list = new ArrayList<Query>();
    		while(true){
				int index_N = sentence.indexOf(N);
				if( index_N == -1 )break;
				line = sentence.substring(0,index_N+length_N);
				sentence = sentence.substring(index_N+length_N, sentence.length());//表記前の部分の削除    	
       			Token[] token = tagger.analyze(line);
       			ArrayList<String> list_str = new ArrayList<String>();
       			int score=0;
    			for(int i=0;i < token.length;i++){
    				String tango=token[i].getBasicString();
    				for(Query q : QueryList){
    					if(tango.equals(q.Word) && !list_str.contains(q.Word)){
    						list_str.add(q.Word);
    						score+=q.score;
    						break;
    					}
    				}
				}
    			sortQueryList(list,new Query(line,score));
    		}
        	
    		/* 実際にAnswersの構築を行う */
    		int length;
    		if(list.size()<Rank)length=list.size();
    		else length=Rank;
    		Answers = new String[Rank];
    		for (int i = 0; i < length; i++) {
    			Query elem = list.get(i);
    			Answers[i] = elem.Word;
    		}
     	} catch (IOException e) {
        	e.printStackTrace();
    	}
	}
	
	/** Queryオブジェクトのリストを降順にソートするメソッド */
	private void sortQueryList(ArrayList<Query> list , Query q){
		int index;
		for(index=0;index<list.size();index++){
			Query elem = list.get(index);
			if(q.score>elem.score){
				list.add(index,q);
				break;
			}
		}
		if(index==list.size()) list.add(q);
	}
	
	/** フィールドAnswersの値を取得するメソッド */
	public String[] getAnswers(){
		return Answers;
	}
	

}

class Query{
	String Word;//単語
	int score;  //このクエリのスコア
	
	Query(String s,int p){
		Word=s; 
		score=p;
	}
	
}
