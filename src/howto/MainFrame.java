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
	/** MainFrame�I�u�W�F�N�g�ɂ�����t�B�[���h�Q */
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
		super("���@�����V�X�e��");  //�^�C�g��
		createObject();  //�e�I�u�W�F�N�g�̐���
		Layout(); //�e�R���|�[�l���g�̃��C�A�E�g���s��
		pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false); //�T�C�Y�̌Œ�
        setVisible(true);
        //�E�B���h�E�E��́~�{�^�������������̓���̓o�^
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	confirmExiting();
            }
        });
	}
	
	/** �I�u�W�F�N�g�̐������s�����\�b�h */
	private final void createObject(){
		Massage = new JLabel("�����ׂ������@����͂��Ă�������");
		DefaultKeyWard = new JLabel("���@");
		KeyWard = new JTextField();
		Search = new JButton("����");
		RankOption = new JLabel("��ʉ����܂ŕ\�����܂����H");
		lbRank = new JLabel("��");
		tfRank = new JTextField();
		tfRank.setText(Integer.toString(DefaultRnak));
		Search.addActionListener(this);
	}
	
	/** �e�R���|�[�l���g�̃��C�A�E�g���s�����\�b�h */
	private final void Layout(){
	    JPanel panel = new JPanel(); //���C�A�E�g�p�̃p�l��
		GroupLayout sublayout = new GroupLayout(panel);
		panel.setLayout(sublayout);
		sublayout.setAutoCreateGaps(true);
		sublayout.setAutoCreateContainerGaps(true);
		//��������
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
		//���s����
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
	
	/** �E�B���h�E����郁�\�b�h */
    private void confirmExiting() {
        int retValue = JOptionPane.showConfirmDialog(this, "�I�����܂����H",
                "�E�B���h�E�̏I��", JOptionPane.OK_CANCEL_OPTION);
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
				analyze = new Analyze(key+"�@���@",Search,getRank());
				Search.setEnabled(false);
			}
		}
	}
	
	/** �w�肳�ꂽ�\��������Ԃ����\�b�h */
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
