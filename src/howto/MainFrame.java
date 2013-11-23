package howto;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.GroupLayout.Alignment;

public class MainFrame extends JFrame implements ActionListener {
	/** MainFrameオブジェクトにおけるフィールド群 */
	private JLabel Massage;
	private JLabel DefaultKeyWard;
	private JTextField KeyWard;
	private JLabel RankOption;
	private JLabel lbRank;
	private JTextField tfRank;
	private JButton Search;
	private Analyze analyze;
	private final int DefaultRnak = 30;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame();
	}
	
	public MainFrame(){
		super("方法検索システム");  //タイトル
		createObject();  //各オブジェクトの生成
		Layout(); //各コンポーネントのレイアウトを行う
		pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false); //サイズの固定
        setVisible(true);
        //ウィンドウ右上の×ボタンを押した時の動作の登録
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	confirmExiting();
            }
        });
	}
	
	/** オブジェクトの生成を行うメソッド */
	private final void createObject(){
		Massage = new JLabel("★調べたい方法を入力してください");
		DefaultKeyWard = new JLabel("方法");
		KeyWard = new JTextField();
		Search = new JButton("検索");
		RankOption = new JLabel("上位何件まで表示しますか？");
		lbRank = new JLabel("位");
		tfRank = new JTextField();
		tfRank.setText(Integer.toString(DefaultRnak));
		Search.addActionListener(this);
	}
	
	/** 各コンポーネントのレイアウトを行うメソッド */
	private final void Layout(){
	    JPanel panel = new JPanel(); //レイアウト用のパネル
		GroupLayout sublayout = new GroupLayout(panel);
		panel.setLayout(sublayout);
		sublayout.setAutoCreateGaps(true);
		sublayout.setAutoCreateContainerGaps(true);
		//垂直方向
		GroupLayout.SequentialGroup subhGroup = sublayout.createSequentialGroup();
		subhGroup.addGroup(sublayout.createParallelGroup()
				.addComponent(Massage)
				.addComponent(KeyWard, 100, 150, 200)
				.addComponent(RankOption)
				.addComponent(tfRank, 50, 60, 70)
				.addComponent(Search)
		);
		subhGroup.addGroup(sublayout.createParallelGroup()
				.addComponent(DefaultKeyWard)
				//.addComponent(lbRank)
		);
		//並行方向
		GroupLayout.SequentialGroup subvGroup = sublayout.createSequentialGroup();		
		subvGroup.addGroup(sublayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(Massage)
		);
		subvGroup.addGroup(sublayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(KeyWard)
				.addComponent(DefaultKeyWard)
		);
		subvGroup.addGroup(sublayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(RankOption)
		);
		subvGroup.addGroup(sublayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tfRank)
				//.addComponent(lbRank)
		);
		subvGroup.addGroup(sublayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(Search)
		);
		
		sublayout.setHorizontalGroup(subhGroup);
		sublayout.setVerticalGroup(subvGroup);
		getContentPane().add(panel);
	}
	
	/** ウィンドウを閉じるメソッド */
    private void confirmExiting() {
        int retValue = JOptionPane.showConfirmDialog(this, "終了しますか？",
                "ウィンドウの終了", JOptionPane.OK_CANCEL_OPTION);
        if (retValue == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if( arg0.getSource() == Search ){
			String key = KeyWard.getText();
			if(!key.equals("")){
				if(analyze!=null)analyze.disposeAnswerFrame();
				analyze = new Analyze(key+"　方法",Search,getRank());
				Search.setEnabled(false);
			}
		}
	}
	
	/** 指定された表示件数を返すメソッド */
	public int getRank(){
		String str = tfRank.getText();
		if(str.equals(""))return DefaultRnak;
		try{
			int rank = Integer.parseInt(str);
			return rank;
		}catch(NumberFormatException ex){
			return DefaultRnak;
		}
	}

}
