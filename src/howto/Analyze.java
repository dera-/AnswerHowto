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
	private String KeyWard;  /** �����Ώۂ̃L�[���[�h */
	private Preparation before; /** �񓚒��o�̑O�������s���I�u�W�F�N�g */
	private Answer answer; /** �񓚒��o���s���I�u�W�F�N�g */
	private static String FoldrName = "C:\\Users\\Onodera\\Desktop\\�v���O������\\2012�I�[�v���L�����p�X\\result"; /** �e�t�@�C���̕ۑ���f�B���N�g�� */
	private AnswerFrame frame;  /** ������\�������� */
	private JButton Search;  /** �t���[���̎��s�{�^�� */
	private final int Rank; /** �����̕\������ */
	private WaitFrame frame_W;  /** �������ɕ\������E�B���h�E */
	
	public Analyze(String str,JButton button,int rank){
		KeyWard = str;
		Search = button;
		Rank = rank;
		frame_W = new WaitFrame();
			
		/* �ʃX���b�h�̋N�� */
		thread = new Thread(this);
		thread.start();
	}
	
	/** ��1�����̃t�@�C���p�X�ɑ�2�����̕�������������ރ��\�b�h */
	public static void fileSave(String file,String content){
		try{
			String pass = FoldrName+File.separatorChar+file;
			FileWriter fw = new FileWriter(pass);//�t�@�C���J��
			fw.write(content, 0, content.length());//�ۑ�
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
	
	/** AnswerFrame�E�B���h�E���������\�b�h */
	public void disposeAnswerFrame(){
		frame.setVisible(false);
	}
	
}

/** �����̌������ɕ\��������ʃI�u�W�F�N�g */
class WaitFrame extends JFrame{
	 JLabel textarea = new JLabel("���݌������B���X���҂���������");
	
	public WaitFrame(){
		super("");  //�^�C�g��
		textarea = new JLabel("���݌������ł��B���X���҂����������B");
		textarea.setFont(new Font("",Font.BOLD,28));
		getContentPane().add(textarea, BorderLayout.CENTER);
		pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false); //�T�C�Y�̌Œ�
	}
	
}
