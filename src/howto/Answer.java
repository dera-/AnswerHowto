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
	private String Sentence; /** ���� */
	private ArrayList<Query> QueryList = new ArrayList<Query>();  /** �N�G���̏W�� */
	private final int Rank;  /** ��ʉ��ʂ̓����܂ł�\�����邩������ */
	private String[] Answers;  /** �ŏI�I�ɒ񎦂��铚���̕��͌Q */
	
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
	
	/** �p�X�����當�͂��擾���郁�\�b�h */
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
				passage = passage.substring(index_N+length_N, passage.length());//�\�L�O�̕����̍폜    		
				Token[] token = tagger.analyze(line);
				boolean flag=false;   //�����������ɑ��݂��邩�ǂ���(�����ǂ���)
				boolean flag2=false;
				boolean flag3=false;
				for(int i=0;i < token.length;i++){
					String hinshi=token[i].getPos();
					int H = hinshi.indexOf("-");
					if(H != -1){
						String sub=hinshi.substring( 0 , H );
						if(sub.equalsIgnoreCase("����")){
							flag = true;
						}
					}
					if(flag==true){
						for(int j=0;j < token.length;j++){
							hinshi=token[j].getPos();
							H = hinshi.indexOf("-");
							if(H != -1){
								String sub=hinshi.substring( 0 , H );
								if(sub.equalsIgnoreCase("����")){
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
						if(((sub1.equalsIgnoreCase("�L��"))&&(sub2.equalsIgnoreCase("����"))) || ((sub1.equalsIgnoreCase("�L��"))&&(sub2.equalsIgnoreCase("����")))){
							flag3 = true;
						}
					}
					if(L1 != -1){
						String sub1=Lhinshi1.substring( 0 , L1 );
						if((sub1.equalsIgnoreCase("����"))||(sub1.equalsIgnoreCase("����"))||(sub1.equalsIgnoreCase("�L��"))&&(Lhinshi2.equalsIgnoreCase("������"))){
							flag3 = true;
						}
					}
					if(Lhinshi1.equalsIgnoreCase("������")){
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
	
	/** �����̕��͂���`�ԑf���l�����郁�\�b�h */
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
				sentence = sentence.substring(index_N+length_N, sentence.length());//�\�L�O�̕����̍폜    	
    	    	Token[] token = tagger.analyze(line);
    	    	boolean flag=false;   //�����������ɑ��݂��邩�ǂ���(�����ǂ���)
    	    	for(int i=0;i < token.length;i++){
    	    		String hinshi=token[i].getPos();
    	    		int H = hinshi.indexOf("-");
    	    		if(H != -1){
    	    			String sub=hinshi.substring( 0 , H );
    	    			if(sub.equalsIgnoreCase("����")){
    	    				flag = true;
    	    				break;
    	    			}
    	    		}
    	    	}
    	    	if(flag==false)continue;
                for (int i = 0; i < token.length; i++) {
                	morpheme+=(token[i] + "\t" + token[i].getBasicString()  + "\t" + token[i].getPos() +"\r\n");  //�t�@�C���o��
                }
    	    }
    	    return morpheme;
        } catch (IOException e) {
        	e.printStackTrace();
        	return "";
        }
	}
	
	/** �����̌`�ԑf����P��𒊏o���郁�\�b�h(QueryList�̍\�z���s�����\�b�h) */
	private void extractWord(String morpheme){
		HashMap<String,Integer> tm = new HashMap<String,Integer>();
		String N = "\n";
		int length_N=N.length();
		String line;
		
		/* HashMap�I�u�W�F�N�g�̍\�z */
		while(true){//�P�F�t�@�C�����當�����ǂݍ���
			int index_N = morpheme.indexOf(N);
			if( index_N == -1 )break;
			line = morpheme.substring(0,index_N+length_N);
			morpheme = morpheme.substring(index_N+length_N, morpheme.length());//�\�L�O�̕����̍폜    	
			String[] words = line.split("\\s");//�Q�F�����񂩂�P��𒊏o����
			String hinshi=words[2];
			if(hinshi.equalsIgnoreCase("���m��"))continue;
			if(hinshi.equalsIgnoreCase("�A�̎�"))continue;
			if(hinshi.equalsIgnoreCase("����-�񎩗�"))continue;
			if(hinshi.equalsIgnoreCase("����-�ڔ�"))continue;
		
			int H = hinshi.lastIndexOf("-");
			if(H != -1){
				String sub=hinshi.substring( 0 , H );
				if(sub.equalsIgnoreCase("����-�񎩗�"))continue;
			}
			
			char mozi=hinshi.charAt(0);
			if(mozi=='�L')continue;
			if(mozi=='��')continue;
			//if(mozi=='��')continue;
			if(mozi=='��')continue;
			String kihon=words[1];
			if(kihon.equalsIgnoreCase("����"))continue;
			if(kihon.equalsIgnoreCase("����"))continue;
			if(kihon.equalsIgnoreCase("�Ȃ�"))continue;
			if(kihon.equalsIgnoreCase("����"))continue;
			if(kihon.equalsIgnoreCase("��"))continue;
			if(kihon.equalsIgnoreCase("����"))continue;
			//�R�F�����̒P����}�b�v�ɓo�^����
			String tango=words[1];
			if(!tm.containsKey(tango)){
				tm.put(tango,1);
			}else{
				tm.put(tango,tm.get(tango).intValue()+1);
			}
		}
		
		/* QueryList�̍\�z */
		//int threshold = 10;  //�P��̒��o����臒l
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
		int rank = 25; //QueryList�̗v�f��
		for(int i=0;i<length-rank;i++){
			QueryList.remove(rank);
		}
	}
	
	/** �����̕��͂�T�����\�b�h */
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
				sentence = sentence.substring(index_N+length_N, sentence.length());//�\�L�O�̕����̍폜    	
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
        	
    		/* ���ۂ�Answers�̍\�z���s�� */
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
	
	/** Query�I�u�W�F�N�g�̃��X�g���~���Ƀ\�[�g���郁�\�b�h */
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
	
	/** �t�B�[���hAnswers�̒l���擾���郁�\�b�h */
	public String[] getAnswers(){
		return Answers;
	}
	

}

class Query{
	String Word;//�P��
	int score;  //���̃N�G���̃X�R�A
	
	Query(String s,int p){
		Word=s; 
		score=p;
	}
	
}
