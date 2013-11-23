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
	/** AnswerFrame�I�u�W�F�N�g�ɂ�����t�B�[���h�Q */
	private JLabel Massage;
	private JLabel[] Answers;
	private JSeparator separator;  //�Z�p���[�^�[
	
	public AnswerFrame(String key,String[] strs){
		super("�u"+key+"�v"+"�ɑ΂��铚��");  //�^�C�g��
		createObject(strs);  //�e�I�u�W�F�N�g�̐���
		Layout(); //�e�R���|�[�l���g�̃��C�A�E�g���s��
		setSize(800,600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        //�E�B���h�E�E��́~�{�^�������������̓���̓o�^
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	setVisible(false);
            }
        });
	}
	
	/** �t�B�[���h�̃I�u�W�F�N�g�̐������s�����\�b�h */
	private final void createObject(String[] strs){
		Massage = new JLabel("���"+strs.length+"���̓�����\��");
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
			Answers[i] = new JLabel((i/2+1)+"�ʁF"+strs[i/2]);
			Answers[i+1] = new JLabel("  ");
			Answers[i].setFont(new Font("",Font.PLAIN,font));
			Answers[i+1].setFont(new Font("",Font.PLAIN,16));
		}
		separator = new JSeparator();
	}
	
	/** �e�R���|�[�l���g�̃��C�A�E�g���s�����\�b�h */
	private final void Layout(){
		JPanel panel = new JPanel(); //���C�A�E�g�p�̃p�l��
        BoxLayout boxlayout =
            new BoxLayout(panel,BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
		panel.add(Massage);
		panel.add(separator);
		for(JLabel label : Answers){
			panel.add(label);
		}
		
		/* �X�N���[���p�l���̒ǉ� */
	    JScrollPane scrollpane = new JScrollPane(panel);
	    scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    getContentPane().add(scrollpane, BorderLayout.CENTER);
		//getContentPane().add(panel);
	}

}
