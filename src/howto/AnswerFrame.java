package howto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

public class AnswerFrame extends JFrame {
	/** AnswerFrameオブジェクトにおけるフィールド群 */
	private JLabel Massage;
	private JLabel[] Answers;
	private JSeparator separator;  //セパレーター
	
	public AnswerFrame(String key,String[] strs){
		super("「"+key+"」"+"に対する答え");  //タイトル
		createObject(strs);  //各オブジェクトの生成
		Layout(); //各コンポーネントのレイアウトを行う
		setSize(800,600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        //ウィンドウ右上の×ボタンを押した時の動作の登録
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	setVisible(false);
            }
        });
	}
	
	/** フィールドのオブジェクトの生成を行うメソッド */
	private final void createObject(String[] strs){
		Massage = new JLabel("上位"+strs.length+"件の答えを表示");
		Massage.setFont(new Font("",Font.BOLD,30));
		Massage.setForeground(Color.red);
		Answers = new JLabel[2*strs.length];
		for(int i=0;i<Answers.length;i+=2){
			int font;
			if(i<20){
				switch(i){
				case 0: font = 24;
						break;
				case 2: font = 22;
						break;
				case 4: font = 20;
						break;
				default: font =18;
						 break;
				}
			}
			else{
				font = 16;
			}
			Answers[i] = new JLabel((i/2+1)+"位："+strs[i/2]);
			Answers[i+1] = new JLabel("  ");
			Answers[i].setFont(new Font("",Font.PLAIN,font));
			Answers[i+1].setFont(new Font("",Font.PLAIN,16));
		}
		separator = new JSeparator();
	}
	
	/** 各コンポーネントのレイアウトを行うメソッド */
	private final void Layout(){
		JPanel panel = new JPanel(); //レイアウト用のパネル
        BoxLayout boxlayout =
            new BoxLayout(panel,BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
		panel.add(Massage);
		panel.add(separator);
		for(JLabel label : Answers){
			panel.add(label);
		}
		
		/* スクロールパネルの追加 */
	    JScrollPane scrollpane = new JScrollPane(panel);
	    scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    getContentPane().add(scrollpane, BorderLayout.CENTER);
		//getContentPane().add(panel);
	}

}
