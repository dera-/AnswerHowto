package howto;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.mozilla.universalchardet.UniversalDetector;

/** 回答抽出の前処理を行うクラス */
public class Preparation {
	//private String FoldrName;  /** ファイルの保存先フォルダの名前 */
	//private String KeyWardSorceName; /** 検索結果のソースファイルの名前 */
	//private String SorcesName; /** 検索先サイト(上位10件)のソースファイルの名前 */
	//private String PassFileName;  /** パスファイルの名前 */
	
	public Preparation(){}
	
	/** 回答抽出処理の軸となるパスを文字列として返すメソッド */
	public String getPassFile_String(String key){
		String sorce = getKeyWardSorce(key);
		System.out.println(sorce);
		ArrayList<String> list = getURLList(sorce);
		sorce = null; //メモリの開放
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
	
	/** 検索結果のソースを獲得するメソッド */
	private String getKeyWardSorce(String key){
		//StringBuffer sb=new StringBuffer();
		String str = "";
		try{
			String url_str = "http://search.yahoo.co.jp/search?p=";
			url_str += URLEncoder.encode( key, "UTF-8" );
			URL url = new URL( url_str ); // URLオブジェクトの生成
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-agent","Mozilla/5.0");  
	
			// 検索結果を読み込む
			BufferedReader reader = new BufferedReader(
					new InputStreamReader( connection.getInputStream(), "UTF-8" ));
	
			// 検索結果をファイルに出力
			String line = null;
			while ( ( line = reader.readLine() ) != null ) {
				//System.out.println(line);
				str+=line;
			}
			return str;
		}catch(IOException ex){
			System.out.println("検索結果のソースコードが読み込めませんでした");
			return "";
		}
	}
	
	/** 検索結果の上位10件のサイトのURLをStringのリストとして返すメソッド */
	private final ArrayList<String> getURLList(String sorce){
		//Googleのurl表記は<h3><a href=\"で始まり,\" onmousedown=で終わっている
		ArrayList<String> list = new ArrayList<String>();  //URLリスト
		String st_start = "<li><a href=\"";    //URLが始まる文字
		String st_last = "\">";    //URL終了の文字
		//int length = sorce.length(); /** ソースコードの文字数 */
		int length_st_start = st_start.length(); /*URLが始まる文字の長さ*/
		for(int i=0;i<10;i++){
			if(sorce.indexOf(st_start) == -1) break;//終了条件
			//System.out.println(sorce.indexOf(st_start));
			sorce = sorce.substring(sorce.indexOf(st_start)+length_st_start, sorce.length());//表記前の部分の削除
			String elem = sorce.substring(0, sorce.indexOf(st_last));
			System.out.println(elem);
			list.add(elem);
		}
		return list;
	}
	
	/** 引数URLのサイトのソースコードを返すメソッド */
	private String getSorce(String str_url){
	  	try{
	  		String str="";
	        // URLの指定
	        URL url = new URL(str_url);

	        HttpURLConnection urlconn1 = (HttpURLConnection)url.openConnection();
	        HttpURLConnection urlconn2 = (HttpURLConnection)url.openConnection();
	        urlconn1.setRequestProperty("User-agent","Mozilla/5.0");  
	        urlconn2.setRequestProperty("User-agent","Mozilla/5.0");  
	        
	        String code = getEncoding(urlconn1.getInputStream());
	  		BufferedReader reader; 
	  		if(code!=null)reader = new BufferedReader(new InputStreamReader(urlconn2.getInputStream(), code));//URL先テキスト取得
	  		else reader = new BufferedReader(new InputStreamReader(urlconn2.getInputStream(),"JISAutoDetect"));//URL先テキスト取得
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
	
	/** 文字コードを返すメソッド */
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
		    encoding = "utf-8"; // デフォルトはUTF8にする
		  }
		  */
		  return encoding;
		}catch(IOException ex){
			return null;
		}
	}
	
	/** 1つのサイトのソースコードをパスファイルにしたものを文字列として返すメソッド */
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
	    line = line.toUpperCase();//全て大文字
	    while(line != null || line == ""){//lineがnullまで続ける
	    	pareL = line.indexOf('<');//'<'が初めて現れる箇所
	    	if(pareL > 0){//'<'が初めて現れる箇所が先頭でない→先頭までstcする
	    		if(!flgSkip){
	    			for(int i = 0; i < pareL; i++){
	    				if(line.charAt(i) != ' ' && line.charAt(i) != '　' && line.charAt(i) != '\t'){
	    					lineStc += line.charAt(i);
	    					if(line.charAt(i) == '。' || line.charAt(i) == '．' || line.charAt(i) == '.' || line.charAt(i) == ';')
	    						lineStc += "**Pass**";
	    				}
	    			}
	    		}
	    		line = line.substring(pareL, line.length());
	    	}
	    	else if(pareL == -1){//'<'が無い
	    		pareR = line.indexOf('>');//'>'が初めて現れる箇所
	    		if(flgSkip == true && line.indexOf("-->") == pareR - 2){//<!--が以前存在し，-->が見つかった
	    			line = line.substring(pareR + 1, line.length());
	    			flgSkip = false;
	    		}
	    		else if(!flgSkip){
	    			for(int i = 0; i < line.length(); i++){
	    				if(line.charAt(i) != ' ' && line.charAt(i) != '　' && line.charAt(i) != '\t'){
	    					lineStc += line.charAt(i);
	    					if(line.charAt(i) == '。' || line.charAt(i) == '．' || line.charAt(i) == '.' || line.charAt(i) == ';')
	    						lineStc += "**Pass**";
	    				}
	    			}
	    		}
	    		line = null;
	    	}
	    	else{//'<'が先頭
	    		pareR = line.indexOf('>');//'>'が初めて現れる箇所
	    		if(line.indexOf("!--") == 1)//<!--があった→flgSkip = true;
	    			flgSkip = true;
	    		if(pareR == -1){//'>'が無い
	    			if(flgSkip)
	    				line = null;
	    			else{
	    				//System.out.println("non '>'  " + line);
	    				//oldLine = line;
	    				line = null;
	    			}
	    		}
	    		else{//'>'がある
	    			if(flgSkip == true && line.indexOf("-->") == pareR - 2){//<!--が以前存在し，-->が見つかった
	    				line = line.substring(pareR + 1, line.length());
	    				flgSkip = false;
	    			}
	    			else{//'<'で始まり'>'で終わるものが見つかった
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
	    pass+=makePassage(lineStc,fs);//最終処理	
	    return pass;
	}
	
	/** getPassメソッドの途中処理 */
	public String makePassage(String lineStc,boolean fs){
		//\nを入れる
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
		  		if((lineStc.substring(dbl + 8, lineStc.length())).length() > 1000)//passが1000文字超えたら登録しない
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
