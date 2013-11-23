package howto;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Analyze implements Runnable {
	private Thread thread;
	private String KeyWard;  /** 検索対象のキーワード */
	private Preparation before; /** 回答抽出の前処理を行うオブジェクト */
	private Answer answer; /** 回答抽出を行うオブジェクト */
	private static String FoldrName = "C:\\Users\\Onodera\\Desktop\\プログラム等\\2012オープンキャンパス\\result"; /** 各ファイルの保存先ディレクトリ */
	private AnswerFrame frame;  /** 答えを表示する画面 */
	private JButton Search;  /** フレームの実行ボタン */
	private final int Rank; /** 答えの表示件数 */
	private WaitFrame frame_W;  /** 検索中に表示するウィンドウ */
	
	public Analyze(String str,JButton button,int rank){
		KeyWard = str;
		Search = button;
		Rank = rank;
		frame_W = new WaitFrame();
			
		/* 別スレッドの起動 */
		thread = new Thread(this);
		thread.start();
	}
	
	/** 第1引数のファイルパスに第2引数の文字列を書き込むメソッド */
	public static void fileSave(String file,String content){
		try{
			String pass = FoldrName+File.separatorChar+file;
			FileWriter fw = new FileWriter(pass);//ファイル開く
			fw.write(content, 0, content.length());//保存
			fw.close();
		}
		catch(IOException e){
			System.out.println("*** Error in FileSave Class, Save ***\n" + e.getMessage());
		}
	}

	@Override
	public void run() {
		frame_W.setVisible(true);
		before = new Preparation();
		System.out.println(KeyWard);
		String pass = before.getPassFile_String(KeyWard);
		//fileSave("pass.txt",pass);
		answer = new Answer(pass,Rank);
		frame_W.setVisible(false);
		frame = new AnswerFrame(KeyWard,answer.getAnswers());
		Search.setEnabled(true);
	}
	
	/** AnswerFrameウィンドウを消すメソッド */
	public void disposeAnswerFrame(){
		frame.setVisible(false);
	}
	
}

/** 答えの検索中に表示される画面オブジェクト */
class WaitFrame extends JFrame{
	 JLabel textarea = new JLabel("現在検索中。少々お待ちください");
	
	public WaitFrame(){
		super("");  //タイトル
		textarea = new JLabel("現在検索中です。少々お待ちください。");
		textarea.setFont(new Font("",Font.BOLD,28));
		getContentPane().add(textarea, BorderLayout.CENTER);
		pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false); //サイズの固定
	}
	
}
