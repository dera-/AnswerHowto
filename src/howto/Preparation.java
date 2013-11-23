package howto;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.mozilla.universalchardet.UniversalDetector;

/** �񓚒��o�̑O�������s���N���X */
public class Preparation {
	//private String FoldrName;  /** �t�@�C���̕ۑ���t�H���_�̖��O */
	//private String KeyWardSorceName; /** �������ʂ̃\�[�X�t�@�C���̖��O */
	//private String SorcesName; /** ������T�C�g(���10��)�̃\�[�X�t�@�C���̖��O */
	//private String PassFileName;  /** �p�X�t�@�C���̖��O */
	
	public Preparation(){}
	
	/** �񓚒��o�����̎��ƂȂ�p�X�𕶎���Ƃ��ĕԂ����\�b�h */
	public String getPassFile_String(String key){
		String sorce = getKeyWardSorce(key);
		System.out.println(sorce);
		ArrayList<String> list = getURLList(sorce);
		sorce = null; //�������̊J��
		String Pass = "";
		//int index_test=0;
		for(String url : list){
			String elem = getSorce(url);
			if(elem.equals(""))continue;
			//System.out.println(elem);
			//Analyze.fileSave("sorce"+index_test+".txt", elem);
			//index_test++;
			Pass += getPass(elem);
		}
		return Pass;
	}
	
	/** �������ʂ̃\�[�X���l�����郁�\�b�h */
	private String getKeyWardSorce(String key){
		//StringBuffer sb=new StringBuffer();
		String str = "";
		try{
			String url_str = "http://search.yahoo.co.jp/search?p=";
			url_str += URLEncoder.encode( key, "UTF-8" );
			URL url = new URL( url_str ); // URL�I�u�W�F�N�g�̐���
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-agent","Mozilla/5.0");  
	
			// �������ʂ�ǂݍ���
			BufferedReader reader = new BufferedReader(
					new InputStreamReader( connection.getInputStream(), "UTF-8" ));
	
			// �������ʂ��t�@�C���ɏo��
			String line = null;
			while ( ( line = reader.readLine() ) != null ) {
				//System.out.println(line);
				str+=line;
			}
			return str;
		}catch(IOException ex){
			System.out.println("�������ʂ̃\�[�X�R�[�h���ǂݍ��߂܂���ł���");
			return "";
		}
	}
	
	/** �������ʂ̏��10���̃T�C�g��URL��String�̃��X�g�Ƃ��ĕԂ����\�b�h */
	private final ArrayList<String> getURLList(String sorce){
		//Google��url�\�L��<h3><a href=\"�Ŏn�܂�,\" onmousedown=�ŏI����Ă���
		ArrayList<String> list = new ArrayList<String>();  //URL���X�g
		String st_start = "<li><a href=\"";    //URL���n�܂镶��
		String st_last = "\">";    //URL�I���̕���
		//int length = sorce.length(); /** �\�[�X�R�[�h�̕����� */
		int length_st_start = st_start.length(); /*URL���n�܂镶���̒���*/
		for(int i=0;i<10;i++){
			if(sorce.indexOf(st_start) == -1) break;//�I������
			//System.out.println(sorce.indexOf(st_start));
			sorce = sorce.substring(sorce.indexOf(st_start)+length_st_start, sorce.length());//�\�L�O�̕����̍폜
			String elem = sorce.substring(0, sorce.indexOf(st_last));
			System.out.println(elem);
			list.add(elem);
		}
		return list;
	}
	
	/** ����URL�̃T�C�g�̃\�[�X�R�[�h��Ԃ����\�b�h */
	private String getSorce(String str_url){
	  	try{
	  		String str="";
	        // URL�̎w��
	        URL url = new URL(str_url);

	        HttpURLConnection urlconn1 = (HttpURLConnection)url.openConnection();
	        HttpURLConnection urlconn2 = (HttpURLConnection)url.openConnection();
	        urlconn1.setRequestProperty("User-agent","Mozilla/5.0");  
	        urlconn2.setRequestProperty("User-agent","Mozilla/5.0");  
	        
	        String code = getEncoding(urlconn1.getInputStream());
	  		BufferedReader reader; 
	  		if(code!=null)reader = new BufferedReader(new InputStreamReader(urlconn2.getInputStream(), code));//URL��e�L�X�g�擾
	  		else reader = new BufferedReader(new InputStreamReader(urlconn2.getInputStream(),"JISAutoDetect"));//URL��e�L�X�g�擾
	  		String line;
	  		while((line = reader.readLine()) != null){
	  			str += line;
	  		}
	  		return str;
	  	}catch(IOException e){
	      System.out.println("Fail\n*** Error in GetHTML Class, GetSorceCode::Following URL is not exist. ***\n" + e.getMessage());
	      return "";
	  	}
	}
	
	/** �����R�[�h��Ԃ����\�b�h */
	private String getEncoding(InputStream is){
		try{
		  byte[] buf = new byte[4096];
		  UniversalDetector detector = new UniversalDetector(null);
		  int nread;
		  while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
		    detector.handleData(buf, 0, nread);
		  }
		  detector.dataEnd();
		  String encoding = detector.getDetectedCharset();
		  /*
		  if (encoding == null) {
		    encoding = "utf-8"; // �f�t�H���g��UTF8�ɂ���
		  }
		  */
		  return encoding;
		}catch(IOException ex){
			return null;
		}
	}
	
	/** 1�̃T�C�g�̃\�[�X�R�[�h���p�X�t�@�C���ɂ������̂𕶎���Ƃ��ĕԂ����\�b�h */
	private String getPass(String sorce){
		//System.out.println(sorce);
		//StringBuffer sb=new StringBuffer();
		String pass = "";
	    String line = sorce;
	    String lineStc = new String();
	    String tag = new String();
	    //String oldLine = "";
	    int pareL = 0;
	    int pareR = 0;
	    int countHtml = 0;
	    boolean flgSkip = false;
	    boolean fs = false;
	    char delc;
	    line = line.toUpperCase();//�S�đ啶��
	    while(line != null || line == ""){//line��null�܂ő�����
	    	pareL = line.indexOf('<');//'<'�����߂Č����ӏ�
	    	if(pareL > 0){//'<'�����߂Č����ӏ����擪�łȂ����擪�܂�stc����
	    		if(!flgSkip){
	    			for(int i = 0; i < pareL; i++){
	    				if(line.charAt(i) != ' ' && line.charAt(i) != '�@' && line.charAt(i) != '\t'){
	    					lineStc += line.charAt(i);
	    					if(line.charAt(i) == '�B' || line.charAt(i) == '�D' || line.charAt(i) == '.' || line.charAt(i) == ';')
	    						lineStc += "**Pass**";
	    				}
	    			}
	    		}
	    		line = line.substring(pareL, line.length());
	    	}
	    	else if(pareL == -1){//'<'������
	    		pareR = line.indexOf('>');//'>'�����߂Č����ӏ�
	    		if(flgSkip == true && line.indexOf("-->") == pareR - 2){//<!--���ȑO���݂��C-->����������
	    			line = line.substring(pareR + 1, line.length());
	    			flgSkip = false;
	    		}
	    		else if(!flgSkip){
	    			for(int i = 0; i < line.length(); i++){
	    				if(line.charAt(i) != ' ' && line.charAt(i) != '�@' && line.charAt(i) != '\t'){
	    					lineStc += line.charAt(i);
	    					if(line.charAt(i) == '�B' || line.charAt(i) == '�D' || line.charAt(i) == '.' || line.charAt(i) == ';')
	    						lineStc += "**Pass**";
	    				}
	    			}
	    		}
	    		line = null;
	    	}
	    	else{//'<'���擪
	    		pareR = line.indexOf('>');//'>'�����߂Č����ӏ�
	    		if(line.indexOf("!--") == 1)//<!--����������flgSkip = true;
	    			flgSkip = true;
	    		if(pareR == -1){//'>'������
	    			if(flgSkip)
	    				line = null;
	    			else{
	    				//System.out.println("non '>'  " + line);
	    				//oldLine = line;
	    				line = null;
	    			}
	    		}
	    		else{//'>'������
	    			if(flgSkip == true && line.indexOf("-->") == pareR - 2){//<!--���ȑO���݂��C-->����������
	    				line = line.substring(pareR + 1, line.length());
	    				flgSkip = false;
	    			}
	    			else{//'<'�Ŏn�܂�'>'�ŏI�����̂���������
	    				tag = line.substring(pareL + 1, pareR);
	    				if(pareR == line.length() - 1){
	    					line = null;
	    				}
	    				else
	    					line = line.substring(pareR + 1, line.length());
	    				if (tag.startsWith("HTML")){
	    					lineStc += "**HTML**";
	    					countHtml++;
	    					System.out.println("find HTML " + countHtml);
	    					pass+=makePassage(lineStc,fs);
	    					fs = true;
	    					flgSkip = false;
	    					lineStc = "";
	    				}
	    				else if(tag.startsWith("BR")){
	    					lineStc += "**Pass**";
	    				}
	    				else if(tag.equals("TITLE")){
	    					lineStc += "**Pass**";
	    				}
	    				else if(tag.equals("/TITLE"))
	    					lineStc += "**Pass**";
	    				else if(tag.startsWith("A HREF"))
	    					lineStc += "**Pass**";
	    				else if(tag.startsWith("SCRIPT"))
	    					flgSkip = true;
	    				else if(tag.startsWith("/SCRIPT"))
	    					flgSkip = false;
	    				else if(tag.startsWith("STYLE"))
	    					flgSkip = true;
	    				else if(tag.startsWith("/STYLE"))
	    					flgSkip = false;
	    			}
	    		}
	    	}
	    }
	    pass+=makePassage(lineStc,fs);//�ŏI����	
	    return pass;
	}
	
	/** getPass���\�b�h�̓r������ */
	public String makePassage(String lineStc,boolean fs){
		//\n������
		int dbl = 0;
		String lineStcS = new String();
		while(true){
			dbl = lineStc.indexOf("s****P");
			if(dbl == -1) break;
			lineStc = lineStc.substring(0, dbl - 5) + lineStc.substring(dbl + 3, lineStc.length());
		}
		while(true){
			dbl = lineStc.indexOf("s****H");
			if(dbl == -1) break;
		  	lineStc = lineStc.substring(0, dbl - 5) + lineStc.substring(dbl + 3, lineStc.length());
		}
		while(true){
			dbl = Math.max(lineStc.lastIndexOf("**Pass**"), lineStc.lastIndexOf("**HTML**"));
			if(dbl == -1)break;
		  	if(dbl + 8 == lineStc.length())
		  		lineStcS =  "\n" + lineStc.substring(dbl, dbl + 8) + lineStcS;
		  	else{
		  		if((lineStc.substring(dbl + 8, lineStc.length())).length() > 1000)//pass��1000������������o�^���Ȃ�
		  			System.out.println("over 1000 words :: " + lineStc.substring(dbl + 8, lineStc.length()));
		  		else
		  			lineStcS = "\n" + lineStc.substring(dbl, dbl + 8) + "\n" + lineStc.substring(dbl + 8, lineStc.length()) + lineStcS;
		  	}
			lineStc = lineStc.substring(0, dbl);
		}
		lineStcS = lineStcS.substring(1, lineStcS.length()) + "\n";
		return lineStcS;
	}
	

}
