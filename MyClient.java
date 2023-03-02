import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;//�摜�����ɕK�v
import java.awt.geom.*;//�摜�����ɕK�v
import java.util.Collections;//�d�����Ȃ���������
import java.util.ArrayList;//�d�����Ȃ���������
import java.io.IOException;
import javax.imageio.ImageIO;
import java.applet.*;//wav�t�@�C���̍Đ��Ɏg�p


public class MyClient extends JFrame implements MouseListener,MouseMotionListener{

	private Container c;
	
	private ImageIcon boardIcon, yourboardIcon, Icon1, Icon2, Icon3, seticon1, seticon2, Icons, yIcons;
	private ImageIcon IconPlay, IconStart;
	private ImageIcon IconDog, IconGiraffe, IconNumber, IconCat;
	private ImageIcon IconVer3, IconVer4;
	private ImageIcon Iconexplain;
	private ImageIcon IconBackground;
	private ImageIcon Iconbgm;
	private ImageIcon Iconreset;
	
	private JButton buttonArray[][], yourbuttonArray[][];//�{�^���p�̔z��
	private JButton buttonset3, buttonset4;//�}�X�I���{�^��
	private JButton buttondog, buttongiraffe, buttonnumber, buttoncat;//���[�h�I���{�^��
	private JButton buttonplayGame;//�Q�[���J�n�{�^��
	private JButton cheatbutton;//�`�[�g�{�^��
	private JButton buttonbgm_on;//bgm�{�^��
	private JButton buttonreset;
	
	JLabel theLabeloriginal;//���{�摜
	JLabel countmyclick;//���������񐔂�\��
	JLabel waitinglabel;//�ҋ@���
	JLabel backgroundpic;//�w�i���
	JLabel explainlabel;
	JLabel startlabel;//�X�^�[�g���

	int myTurn;//�^�[��
	int myclick = 0;//�N���b�N������
	int version = 0;//3*3��4*4�̃��[�h
	int imgmode = 0;//�摜�I���̃��[�h
	int bgm = 0;//BGM���
	int resetnum = 0;//���Z�b�g�������Ǘ��A0�Ȃ珉��

	private AudioClip clip = Applet.newAudioClip(getClass().getResource("./bgm/background.wav"));
	private AudioClip clip1 = Applet.newAudioClip(getClass().getResource("./bgm/win.wav"));
	
	


	String name[] = new String[]{
		"no1.jpg","no2.jpg","no3.jpg","no4.jpg","no5.jpg",
		"no6.jpg","no7.jpg","no8.jpg","no9.jpg","no10.jpg",
		"no11.jpg","no12.jpg","no13.jpg","no14.jpg","no15.jpg","boardIcon"
	};
	
	String yname[] = new String[]{
		"yno1.jpg","yno2.jpg","yno3.jpg","yno4.jpg","yno5.jpg",
		"yno6.jpg","yno7.jpg","yno8.jpg","yno9.jpg","yno10.jpg",
		"yno11.jpg","yno12.jpg","yno13.jpg","yno14.jpg","yno15.jpg","yourboardIcon"
	};

	ImageIcon[] icons;
	JLabel theLabel1;
	JPanel p = new JPanel();

	PrintWriter out;//�o�͗p�̃��C�^�[

	public MyClient() {

		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}

		//IP�A�h���X�̓��̓_�C�A���O���J��
		String IPName = JOptionPane.showInputDialog(null,"IP�A�h���X����͂��Ă�������","IP�A�h���X�̓���",JOptionPane.QUESTION_MESSAGE);
		if(IPName.equals("")){
			IPName = "localhost";//���O���Ȃ��Ƃ��́C"localhost"�Ƃ���
		}


		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyClient");//�E�B���h�E�̃^�C�g����ݒ肷��
		setSize(600,620);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();//�t���[���̃y�C�����擾����
		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		c.setBackground(Color.WHITE);//�E�B���h�E�̐F�̐ݒ�
		
		
		//boardIcon�̐錾
		boardIcon = new ImageIcon("boardIcon.jpg");
		yourboardIcon = new ImageIcon("yourboardIcon.jpg");
		
		//�X�^�[�g��ʂ̃��x��
		IconStart = new ImageIcon("./mainpic/start.jpg");
		startlabel = new JLabel(IconStart);
		c.add(startlabel);
		startlabel.setBounds(0,0,600,450);
		
		//�Q�[���J�n�̃{�^��
		IconPlay = new ImageIcon("./mainpic/PlayGame.jpg");
		buttonplayGame = new JButton(IconPlay);
		c.add(buttonplayGame);
		buttonplayGame.setBounds(120,400,360,150);
		buttonplayGame.addMouseListener(this);
		
		//���[�h�I����ʂ̃��x��
		Iconexplain = new ImageIcon("./mainpic/explain.jpg");
		
		//bgm�̍Đ��J�n
		SoundWav(bgm);
		
		
		//�T�[�o�ɐڑ�����
		Socket socket = null;
		try {
			//"localhost"�́C���������ւ̐ڑ��Dlocalhost��ڑ����IP Address�i"133.42.155.201"�`���j�ɐݒ肷��Ƒ���PC�̃T�[�o�ƒʐM�ł���
			//10000�̓|�[�g�ԍ��DIP Address�Őڑ�����PC�����߂āC�|�[�g�ԍ��ł���PC�㓮�삷��v���O��������肷��
			socket = new Socket(IPName, 10000);
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g�� IP �A�h���X������ł��܂���: " + e);
		} catch (IOException e) {
			 System.err.println("�G���[���������܂���: " + e);
		}

		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//��M�p�̃X���b�h���쐬����
		mrt.start();//�X���b�h�𓮂����iRun�������j
	}
	
	
	/*
	//=============stop()�͈�x�����g���Ȃ����߁A�ҋ@��ʏ����ɂ��̃X���b�h�͎g���Ȃ�����(��)============
	// �V���ɑҋ@�����p�ɃX���b�h�𗧂Ă�
	public class Waiting extends Thread{
		public void run(){
			add(p,BorderLayout.CENTER);
			p.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
			p.setSize(600,600);//�p�l���̃T�C�Y�̓E�B���h�E�ɍ��킹��
			p.setBackground(Color.WHITE);//�E�B���h�E�̐F�̐ݒ�

			icons = new ImageIcon[4];
			for(int i=0; i<4 ; i++){
				icons[i] = new ImageIcon("./waiting/number_"+i+".png");
				System.out.println(icons[i]);
			}

			theLabel1 = new JLabel(icons[0]);
			p.add(theLabel1);
			theLabel1.setBounds(175,175,icons[0].getIconWidth(),icons[0].getIconHeight());
			//theLabel1.addMouseListener(this);
			theLabel1.setForeground(Color.WHITE);

			for(int i=0;;i++){
				theLabel1.setIcon(icons[i]);
				p.repaint();
				p.paintImmediately(p.getBounds());
				System.out.println(i);
				try{
					Thread.sleep(1000); //1000�~���bSleep����
				}catch(InterruptedException ee){}
				if(i == 3){
					i = -1;
				}
			}
		}
	}
	//=============stop()�͈�x�����g���Ȃ����߁A�ҋ@��ʏ����ɂ��̃X���b�h�͎g���Ȃ�����(��)============
	*/
	
	
	
	//���b�Z�[�W��M�̂��߂̃X���b�h
	public class MesgRecvThread extends Thread {
		Socket socket;
		String myName;

		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}

		//�ʐM�󋵂��Ď����C��M�f�[�^�ɂ���ē��삷��
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//�ڑ��̍ŏ��ɖ��O�𑗂�
				String myNumberStr = br.readLine();
				int myNumberInt = Integer.parseInt(myNumberStr);
				
				ImageIcon iconoriginal = new ImageIcon("boardIcon.jpg");//���{�摜
				
				cheatbutton = new JButton("�`�[�g");//�`�[�g�{�^��
				
				ImageIcon waitingicon = new ImageIcon("./mainpic/waiting.gif");//�ҋ@���
				waitinglabel = new JLabel(waitingicon);
				
				
				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						//System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
						
						if(cmd.equals("PLAY")){
							System.out.println("PLAY��M");
							
							//BGM�{�^���̍쐬
							ImageIcon Iconbgm = new ImageIcon("./mainpic/bgmbutton.jpg");
							buttonbgm_on = new JButton(Iconbgm);
							
							//���Z�b�g�{�^���̍쐬
							ImageIcon Iconreset = new ImageIcon("./mainpic/resetbutton.jpg");
							buttonreset = new JButton(Iconreset);
							
							//�N���b�N�J�E���g�̃��x���쐬
							countmyclick = new JLabel("0");
							countmyclick.setFont(new Font("Arial", Font.BOLD, 50));
							
							//���{�摜�̃��x���쐬
							theLabeloriginal = new JLabel(iconoriginal);
							
							//myTurn�̌���
							if(myNumberInt % 2 == 0){
								myTurn = 0;
							}else{
								myTurn = 1;
							}
							System.out.println("myTurn = " + myTurn);
							
							buttonplayGame.setVisible(false);
							startlabel.setVisible(false);
							
							buttonmode(myTurn);//�e���[�h�I���{�^������郁�\�b�h�Ăяo��
							
						}

						if(cmd.equals("MODE")){
							System.out.print("MODE��M : ");
							String cmd2 = inputTokens[1];
							if(cmd2.equals("DOG")){
								imgmode = 1;//�����[�h
								System.out.println("DOG�I��");
							}else if(cmd2.equals("GIRAFFE")){
								imgmode = 2;//�L�������[�h
								System.out.println("GIRAFFE�I��");
							}else if(cmd2.equals("NUMBER")){
								imgmode = 3;//�������[�h
								System.out.println("NUMBER�I��");
							}else if(cmd2.equals("CAT")){
								imgmode = 4;//�L���[�h
								System.out.println("CAT�I��");
							}
							
							
							if(myTurn == 0){
								//�e���[�h�I���{�^�����\���ɂ���
								buttondog.setVisible(false);
								buttongiraffe.setVisible(false);
								buttonnumber.setVisible(false);
								buttoncat.setVisible(false);
								
								buttonset(myTurn);//ver3��ver4��I������{�^������郁�\�b�h�Ăяo��
							}
							
							//�E�ɕ\�����錩�{�摜�̃��x��
							if(imgmode == 1){
								iconoriginal = new ImageIcon("./mainpic/dog.jpg");
							}else if(imgmode == 2){
								iconoriginal = new ImageIcon("./mainpic/giraffe.jpg");
							}else if(imgmode == 3){
								iconoriginal = new ImageIcon("./mainpic/number.jpg");
							}else if(imgmode == 4){
								iconoriginal = new ImageIcon("./mainpic/cat.jpg");
							}
							theLabeloriginal = new JLabel(iconoriginal);
						}
						
						//4*4�̑傫����buttonArray��yourbuttonArray��p�ӂ���
						if(cmd.equals("SETver4")){
							System.out.println("4*4���[�h�I��");
							version = 4;//4*4���[�h
							
							setSize(1300,600);//�E�B���h�E�̃T�C�Y��ݒ肷��
							if(myTurn == 0){
								buttonset3.setVisible(false);
								buttonset4.setVisible(false);
								explainlabel.setVisible(false);
							}
							
							//�{�^���̐���
							buttonArray = new JButton[4][4];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
							yourbuttonArray = new JButton[4][4];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
							
							SETver4();//�{�^���ɉ摜�\��t���郁�\�b�h�Ăяo��
							
							c.add(countmyclick);
							countmyclick.setBounds(70,420,100,50);
							countmyclick.setForeground(Color.BLACK);
							
							c.add(theLabeloriginal);
							theLabeloriginal.setBounds(960,10,250,250);
							theLabeloriginal.setForeground(Color.WHITE);
							
							c.add(buttonbgm_on);
							buttonbgm_on.setBounds(960,310,100,50);
							
							c.add(buttonreset);
							buttonreset.setBounds(1090,310,100,50);
							
						}
						
						//3*3�̑傫����buttonArray��yourbuttonArray��p�ӂ���
						if(cmd.equals("SETver3")){
							System.out.println("3*3���[�h�I��");
							version = 3;//3*3���[�h
							
							setSize(1100,500);//�E�B���h�E�̃T�C�Y��ݒ肷��
							if(myTurn == 0){
								buttonset3.setVisible(false);
								buttonset4.setVisible(false);
								explainlabel.setVisible(false);
							}
							
							//�{�^���̐���
							buttonArray = new JButton[3][3];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
							yourbuttonArray = new JButton[3][3];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
							
							SETver3();//�{�^���ɉ摜�\��t���郁�\�b�h�Ăяo��
							
							c.add(countmyclick);
							countmyclick.setBounds(70,320,100,50);
							countmyclick.setForeground(Color.BLACK);
							
							c.add(theLabeloriginal);
							theLabeloriginal.setBounds(760,10,250,250);
							theLabeloriginal.setForeground(Color.WHITE);
							
							c.add(buttonbgm_on);
							buttonbgm_on.setBounds(760,310,100,50);
							
							c.add(buttonreset);
							buttonreset.setBounds(890,310,100,50);
							
						}

						//�ڑ���摜�������_���ɒ���t����
						if(cmd.equals("SETICONver4")){
							System.out.println("SETICON��Mver4");
							
							for(int j = 0;j<4;j++){
								for(int i=0; i<4;i++){
									String strlocation = inputTokens[1 + j*4+i];
									int location = Integer.parseInt(strlocation);
									String nameicon = "boardIcon.jpg";
									String ynameicon = "yourboardIcon.jpg";
									
									if(location == 15){//15�ɂ�boardicon��\��t����
										buttonArray[j][i].setIcon(boardIcon);
										yourbuttonArray[j][i].setIcon(yourboardIcon);
									}else{
										if(imgmode == 1){//�����[�h�̂Ƃ�
											nameicon = "./dog/" + name[location];
											ynameicon = "./dog/" + yname[location];
										}else if(imgmode == 2){//�L�������[�h�̂Ƃ�
											nameicon = "./giraffe/" + name[location];
											ynameicon = "./giraffe/" + yname[location];
										}else if(imgmode == 3){
											nameicon = "./number/" + name[location];
											ynameicon = "./number/" + yname[location];
										}else if(imgmode == 4){
											nameicon = "./cat/" + name[location];
											ynameicon = "./cat/" + yname[location];
										}
										Icon2 = new ImageIcon(nameicon);
										buttonArray[j][i].setIcon(Icon2);
										Icon3 = new ImageIcon(ynameicon);
										yourbuttonArray[j][i].setIcon(Icon3);
									}
								}
							}
							
							waitinglabel.setVisible(false);//�ҋ@��ʂ�����
							
							//=============stop()�͈�x�����g���Ȃ����߁A�ҋ@��ʏ����ɂ��̃X���b�h�͎g���Ȃ�����(��)============
							/*
							//�X���b�h���~�߂�
							t.stop();
							System.out.println("stop!!!");
							p.setVisible(false);
							*/
							//=============stop()�͈�x�����g���Ȃ����߁A�ҋ@��ʏ����ɂ��̃X���b�h�͎g���Ȃ�����(��)============
							
						}
						
						
						if(cmd.equals("SETICONver3")){
							System.out.println("SETICONver3��M");
							
							for(int j = 0;j<3;j++){
								for(int i=0; i<3;i++){
									String strlocation = inputTokens[1 + j*3+i];
									int location = Integer.parseInt(strlocation);
									String nameicon = "boardIcon.jpg";
									String ynameicon = "yourboardIcon.jpg";
									
									if(location == 8){
										buttonArray[j][i].setIcon(boardIcon);
										yourbuttonArray[j][i].setIcon(yourboardIcon);
									}else{
										if(imgmode == 1){//�����[�h�̂Ƃ�
											nameicon = "./dog3/" + name[location];
											ynameicon = "./dog3/" + yname[location];
										}else if(imgmode == 2){//�L�������[�h�̂Ƃ�
											nameicon = "./giraffe3/" + name[location];
											ynameicon = "./giraffe3/" + yname[location];
										}else if(imgmode == 3){//�������[�h�̂Ƃ�s
											nameicon = "./number3/" + name[location];
											ynameicon = "./number3/" + yname[location];
										}else if(imgmode == 4){
											nameicon = "./cat3/" + name[location];
											ynameicon = "./cat3/" + yname[location];
										}
										Icon2 = new ImageIcon(nameicon);
										buttonArray[j][i].setIcon(Icon2);
										Icon3 = new ImageIcon(ynameicon);
										yourbuttonArray[j][i].setIcon(Icon3);
									}
								}
							}
							
							waitinglabel.setVisible(false);//�ҋ@��ʂ�����
							
							//=============stop()�͈�x�����g���Ȃ����߁A�ҋ@��ʏ����ɂ��̃X���b�h�͎g���Ȃ�����(��)============
							/*
							//�X���b�h���~�߂�
							t.stop();
							System.out.println("stop!!!");
							p.setVisible(false);
							*/
							//=============stop()�͈�x�����g���Ȃ����߁A�ҋ@��ʏ����ɂ��̃X���b�h�͎g���Ȃ�����(��)============
							
						}

						if(cmd.equals("CHOOSE")){
							System.out.println("CHOOSE��M");
							
							int x1 = 0;
							int y1 = 0;
							int boardx = 0;
							int boardy = 0;
							int memory1 = 0;
							int memory2 = 0;
							
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							//System.out.println("theBName = " + theBName);
							String theIconName = inputTokens[2];
							String boardIlocation = inputTokens[3];
							memory1 = Integer.parseInt(theBName);
							memory2 = Integer.parseInt(boardIlocation);
							
							if(version == 4){
								x1 = memory1 % 4;
								y1 = memory1 / 4;
								boardx = memory2 % 4;
								boardy = memory2 / 4;
							}else if(version == 3){
								x1 = memory1 % 3;
								y1 = memory1 / 3;
								boardx = memory2 % 3;
								boardy = memory2 / 3;
							}
							
							Icon1 = new ImageIcon(theIconName);
							//System.out.println("�ix,y�j = �i "+ boardx + " , " + boardy + " �j �ɍĊm�F");
							
							if(myTurn == 0){
								buttonArray[y1][x1].setIcon(boardIcon);
								buttonArray[boardy][boardx].setIcon(Icon1);
								
								//���������񐔂̃J�E���g
								myclick++;
								String myclickstr = Integer.toString(myclick);
								countmyclick.setText(myclickstr);
								
							}else{
								yourbuttonArray[y1][x1].setIcon(yourboardIcon);
								
								//�����Ƒ���̃A�C�R���𕪂���
								String[] splitname = theIconName.split("/");//[0]��. [1]�̓��[�h�� [2]�͉摜��
								String your_theIconName = splitname[0] + "/" + splitname[1] + "/y" + splitname[2];
								//System.out.println("�ā@�@���@�@���@�@�@���@�@�@�΁@�@�@��@�@�@your_theIconName  =======   " + your_theIconName);
								Icon1 = new ImageIcon(your_theIconName);
								yourbuttonArray[boardy][boardx].setIcon(Icon1);
							}
							
							judge(version, imgmode);
							myTurn = 1 - myTurn;
							//System.out.println("myTurn : " + myTurn);
							//System.out.println("======================");
						}
						
						
						if(cmd.equals("CHEAT")){
							System.out.println("CHEAT��M");
							cheatbutton();
						}
						
						
						if(cmd.equals("RESET")){
							System.out.println("RESET��M");
							resetgame();
						}
						
					}else{
						break;
					}

				}
				socket.close();
			} catch (IOException e) {
				System.err.println("�G���[���������܂���: " + e);
			}
		}
	}


	//�`�[�g�{�^��
	private void cheatbutton(){
		String names = null;
		String ynames = null;

		//4*dog
		if((version == 4) && (imgmode == 1)){
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if (i != 3){
						names = "./dog/" + name[4*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./dog/" + yname[4*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 3){
						if(j == 0){
							names = "./dog/no13.jpg";
							Icons = new ImageIcon(names);
							ynames = "./dog/yno13.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							names = "./dog/no14.jpg";
							Icons = new ImageIcon(names);
							ynames = "./dog/yno14.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 2){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 3){
							names = "./dog/no15.jpg";
							Icons = new ImageIcon(names);
							ynames = "./dog/yno15.jpg";
							yIcons = new ImageIcon(ynames);
						}
					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}

		//4*giraffe
		if((version == 4) && (imgmode == 2)){
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if (i != 3){
						names = "./giraffe/" + name[4*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./giraffe/" + yname[4*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 3){
						if(j == 0){
							names = "./giraffe/no13.jpg";
							Icons = new ImageIcon(names);
							ynames = "./giraffe/yno13.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							names = "./giraffe/no14.jpg";
							Icons = new ImageIcon(names);
							ynames = "./giraffe/yno14.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 2){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 3){
							names = "./giraffe/no15.jpg";
							Icons = new ImageIcon(names);
							ynames = "./giraffe/yno15.jpg";
							yIcons = new ImageIcon(ynames);
						}
					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}

		//4*number
		if((version == 4) && (imgmode == 3)){
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if (i != 3){
						names = "./number/" + name[4*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./number/" + yname[4*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 3){
						if(j == 0){
							names = "./number/no13.jpg";
							Icons = new ImageIcon(names);
							ynames = "./number/yno13.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							names = "./number/no14.jpg";
							Icons = new ImageIcon(names);
							ynames = "./number/yno14.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 2){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 3){
							names = "./number/no15.jpg";
							Icons = new ImageIcon(names);
							ynames = "./number/yno15.jpg";
							yIcons = new ImageIcon(ynames);
						}


					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}
		
		//4*cat
		if((version == 4) && (imgmode == 4)){
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if (i != 3){
						names = "./cat/" + name[4*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./cat/" + yname[4*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 3){
						if(j == 0){
							names = "./cat/no13.jpg";
							Icons = new ImageIcon(names);
							ynames = "./cat/yno13.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							names = "./cat/no14.jpg";
							Icons = new ImageIcon(names);
							ynames = "./cat/yno14.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 2){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 3){
							names = "./cat/no15.jpg";
							Icons = new ImageIcon(names);
							ynames = "./cat/yno15.jpg";
							yIcons = new ImageIcon(ynames);
						}
					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}

		//3*dog
		if((version == 3) && (imgmode == 1)){
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					if (i != 2){
						names = "./dog3/" + name[3*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./dog3/" + yname[3*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 2){
						if(j == 0){
							names = "./dog3/no7.jpg";
							Icons = new ImageIcon(names);
							ynames = "./dog3/yno7.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 2){
							names = "./dog3/no8.jpg";
							Icons = new ImageIcon(names);
							ynames = "./dog3/yno8.jpg";
							yIcons = new ImageIcon(ynames);
						}
					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}


		//3*giraffe
		if((version == 3) && (imgmode == 2)){
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					if (i != 2){
						names = "./giraffe3/" + name[3*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./giraffe3/" + yname[3*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 2){
						if(j == 0){
							names = "./giraffe3/no7.jpg";
							Icons = new ImageIcon(names);
							ynames = "./giraffe3/yno7.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 2){
							names = "./giraffe3/no8.jpg";
							Icons = new ImageIcon(names);
							ynames = "./giraffe3/yno8.jpg";
							yIcons = new ImageIcon(ynames);
						}
					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}


		//3*number
		if((version == 3) && (imgmode == 3)){
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					if (i != 2){
						names = "./number3/" + name[3*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./number3/" + yname[3*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 2){
						if(j == 0){
							names = "./number3/no7.jpg";
							Icons = new ImageIcon(names);
							ynames = "./number3/yno7.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 2){
							names = "./number3/no8.jpg";
							Icons = new ImageIcon(names);
							ynames = "./number3/yno8.jpg";
							yIcons = new ImageIcon(ynames);
						}
					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}

		//3*cat
		if((version == 3) && (imgmode == 4)){
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					if (i != 2){
						names = "./cat3/" + name[3*i+j] ;
						Icons = new ImageIcon(names);
						ynames = "./cat3/" + yname[3*i+j] ;
						yIcons = new ImageIcon(ynames);
					}else if(i == 2){
						if(j == 0){
							names = "./cat3/no7.jpg";
							Icons = new ImageIcon(names);
							ynames = "./cat3/yno7.jpg";
							yIcons = new ImageIcon(ynames);
						}else if(j == 1){
							Icons = new ImageIcon("boardIcon.jpg");
							yIcons = new ImageIcon("yourboardIcon.jpg");
						}else if(j == 2){
							names = "./cat3/no8.jpg";
							Icons = new ImageIcon(names);
							ynames = "./cat3/yno8.jpg";
							yIcons = new ImageIcon(ynames);
						}
					}
					buttonArray[i][j].setIcon(Icons);
					yourbuttonArray[i][j].setIcon(yIcons);
				}
			}
		}

	}
	

	////ver3��ver4��I������{�^������郁�\�b�h
	private void buttonset(int myTurn){
		IconVer3 = new ImageIcon("./mainpic/setversion3.jpg");
		IconVer4 = new ImageIcon("./mainpic/setversion4.jpg");
		
		buttonset3 = new JButton(IconVer3);
		c.add(buttonset3);
		buttonset3.setBounds(30,20,250,250);
		buttonset3.addMouseListener(this);
		
		buttonset4 = new JButton(IconVer4);
		c.add(buttonset4);
		buttonset4.setBounds(300,20,250,250);
		buttonset4.addMouseListener(this);
		
		explainlabel = new JLabel(Iconexplain);
		c.add(explainlabel);
		explainlabel.setBounds(0,260,600,400);
		
	}


	//�e���[�h�I���{�^������郁�\�b�h
	private void buttonmode(int myTurn){
		if(myTurn == 0){
			IconDog = new ImageIcon("./mainpic/dog.jpg");
			IconGiraffe = new ImageIcon("./mainpic/giraffe.jpg");
			IconNumber = new ImageIcon("./mainpic/number.jpg");
			IconCat = new ImageIcon("./mainpic/cat.jpg");
			
			buttondog = new JButton(IconDog);
			c.add(buttondog);
			buttondog.setBounds(30,20,250,250);
			buttondog.addMouseListener(this);

			buttongiraffe = new JButton(IconGiraffe);
			c.add(buttongiraffe);
			buttongiraffe.setBounds(300,20,250,250);
			buttongiraffe.addMouseListener(this);

			buttonnumber = new JButton(IconNumber);
			c.add(buttonnumber);
			buttonnumber.setBounds(30,290,250,250);
			buttonnumber.addMouseListener(this);
			
			buttoncat = new JButton(IconCat);
			c.add(buttoncat);
			buttoncat.setBounds(300,290,250,250);
			buttoncat.addMouseListener(this);
			
		}else if(myTurn == 1){
			if(resetnum == 0){
				c.add(waitinglabel);
				waitinglabel.setBounds(0,0,580,580);
				waitinglabel.setForeground(Color.WHITE);
			}else if(resetnum == 1){
				waitinglabel.setVisible(true);
			}
		}
		
	}


	private void SETver4(){
		//������buttonArray�����
		for(int j = 0;j<4;j++){
			for(int i=0; i<4;i++){
				buttonArray[j][i] = new JButton(boardIcon);
				c.add(buttonArray[j][i]);//�y�C���ɓ\��t����
				buttonArray[j][i].setBounds(i*100+10,j*100+10,100,100);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
				buttonArray[j][i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
				buttonArray[j][i].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
				buttonArray[j][i].setActionCommand(Integer.toString(j*4+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
			}
		}
		
		//�����buttonArray�����
		for(int j=0;j<4;j++){
			for(int i=0; i<4;i++){
				yourbuttonArray[j][i] = new JButton(yourboardIcon);
				c.add(yourbuttonArray[j][i]);//�y�C���ɓ\��t����
				yourbuttonArray[j][i].setBounds(i*100+510,j*100+10,100,100);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
				yourbuttonArray[j][i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
				yourbuttonArray[j][i].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
				yourbuttonArray[j][i].setActionCommand(Integer.toString(j*4+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
			}
		}
		buttonbgm_on.addMouseListener(this);//bgm�{�^�����N���b�N�����Ƃ��ɔ���������
	}

	private void SETver3(){
		//������buttonArray�����
		for(int j = 0;j<3;j++){
			for(int i=0; i<3;i++){
				buttonArray[j][i] = new JButton(boardIcon);
				c.add(buttonArray[j][i]);//�y�C���ɓ\��t����
				buttonArray[j][i].setBounds(i*100+10,j*100+10,100,100);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
				buttonArray[j][i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
				buttonArray[j][i].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
				buttonArray[j][i].setActionCommand(Integer.toString(j*3+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
			}
		}
		
		//�����buttonArray�����
		for(int j=0;j<3;j++){
			for(int i=0; i<3;i++){
				yourbuttonArray[j][i] = new JButton(yourboardIcon);
				c.add(yourbuttonArray[j][i]);//�y�C���ɓ\��t����
				yourbuttonArray[j][i].setBounds(i*100+410,j*100+10,100,100);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
				yourbuttonArray[j][i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
				yourbuttonArray[j][i].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
				yourbuttonArray[j][i].setActionCommand(Integer.toString(j*3+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
			}
		}
		buttonbgm_on.addMouseListener(this);//bgm�{�^�����N���b�N�����Ƃ��ɔ���������
	}


	//�I������
	private void judge(int version, int imgmode){
		int collectNum = 0;
		String iconnames = null;

		if(version == 4){
			//System.out.println("----------------------");
			for(int j=0;j<4;j++){
				for(int i=0; i<4;i++){
					Icon IconName = buttonArray[j][i].getIcon();

					String falsename = " "+IconName;//�A�C�R�����X�g�����O�Ŏ��o������
					String rename = falsename.substring(1);//1�����ڂ̃X�y�[�X���������A�{���̃A�C�R���̖��O
					//System.out.println(rename);//�f�o�b�O�p��
					
					if(imgmode == 1){//dog�̂Ƃ�
						iconnames = rename.substring(6);
						//System.out.println(iconnames);
					}else if(imgmode == 2){
						iconnames = rename.substring(10);
						//System.out.println(iconnames);
					}else if(imgmode == 3){
						iconnames = rename.substring(9);
						//System.out.println(iconnames);
					}else if(imgmode == 4){
						iconnames = rename.substring(6);
						//System.out.println(iconnames);
					}
					
					String ansewername = name[4*j+i];
					//System.out.println(ansewername);
					
					if(iconnames.equals(ansewername)){
						collectNum++;
						//System.out.println(collectNum);
					}
				}
			}

			if(collectNum == 15){
				if(myTurn == 0){
					System.out.println("�p�Y������!");
					if(bgm == 0){
						bgm = 1;
						SoundWav(bgm);
						bgm = 0;
					}
					FinishWindow dlg = new FinishWindow(this);
					setVisible(true);
					buttonreset.addMouseListener(this);//bgm�{�^�����N���b�N�����Ƃ��ɔ���������
				}
				
			}
		}else if(version == 3){
			for(int j=0;j<3;j++){
				for(int i=0; i<3;i++){
					Icon IconName = buttonArray[j][i].getIcon();
					String falsename = " "+IconName;//�A�C�R�����X�g�����O�Ŏ��o������
					String rename = falsename.substring(1);//1�����ڂ̃X�y�[�X���������A�{���̃A�C�R���̖��O
					//System.out.println(rename);//�f�o�b�O�p��
					
					if(imgmode == 1){//dog�̂Ƃ�
						iconnames = rename.substring(7);
						//System.out.println(iconnames);
					}else if(imgmode == 2){
						iconnames = rename.substring(11);
						//System.out.println(iconnames);
					}else if(imgmode == 3){
						iconnames = rename.substring(10);
						//System.out.println(iconnames);
					}else if(imgmode == 4){
						iconnames = rename.substring(7);
						//System.out.println(iconnames);
					}
					
					String ansewername = name[3*j+i];
					//System.out.println(ansewername);
					
					if(iconnames.equals(ansewername)){
						collectNum++;
						//System.out.println(collectNum);
					}
				}
			}
			
			if(collectNum == 8){
				
				if(myTurn == 0){
					System.out.println("�����ł�!");
					if(bgm == 0){
						bgm = 1;
						SoundWav(bgm);
						bgm = 0;
					}
					FinishWindow dlg = new FinishWindow(this);
					setVisible(true);
					buttonreset.addMouseListener(this);//bgm�{�^�����N���b�N�����Ƃ��ɔ���������
				}
			}
		}
	}
	
	
	//BGM�̊Ǘ�
	public void SoundWav(int bgm){
		if(bgm == 0){
			clip.loop();//BGM�̍Đ�
		}else if(bgm == 1){
			clip1.play();//����SE�̍Đ�
		}else if(bgm == 100){
			clip.stop();//BGM���~�߂�
		}
	}

	public void mouseClicked(MouseEvent e) {//�{�^�����N���b�N�����Ƃ��̏���
		System.out.println("========================");
		int count = 0;
		
		JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
		String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
		Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������
		System.out.println("�N���b�N�����̂�  " + theIcon);
		
		if(theButton == buttonbgm_on){
			//System.out.println("BGM�{�^����������I");
			if(bgm == 0){//BGM��ON�̂Ƃ�OFF�ɂ���
				bgm = 100;//100�̂Ƃ�BGM��OFF�ɂ���
				SoundWav(bgm);
			}else{//BGM��OFF�̂Ƃ�ON�ɂ���
				bgm = 0;//0�̂Ƃ���BGM��ON
				SoundWav(bgm);
			}
		}
		
		if(theButton == buttonplayGame){
			String msg = "PLAY";
			out.println(msg);
			out.flush();
		}
		
		if(theButton == buttonreset){
			String msg = "RESET";
			out.println(msg);
			out.flush();
		}

		if(myTurn == 0){
			if(theButton == cheatbutton){
				String msg = "CHEAT";
				out.println(msg);
				out.flush();
			}
			else if(theButton == buttondog){
				String msg = "MODE"+" "+"DOG";
				out.println(msg);
				out.flush();
			}else if(theButton == buttongiraffe){
				String msg = "MODE"+" "+"GIRAFFE";
				out.println(msg);
				out.flush();
			}else if(theButton == buttonnumber){
				String msg = "MODE"+" "+"NUMBER";
				out.println(msg);
				out.flush();
			}else if(theButton == buttoncat){
				String msg = "MODE"+" "+"CAT";
				out.println(msg);
				out.flush();
			}else if(theButton == buttonset4){
				String msg = "SETver4";
				out.println(msg);
				out.flush();

				msg = setNum_ver4();//�����z�u�����肵���b�Z�[�W���M
				out.println(msg);
				out.flush();

				c.add(cheatbutton);
				cheatbutton.setBounds(1400,700,100,100);
				cheatbutton.addMouseListener(this);
			}else if(theButton == buttonset3){
				String msg = "SETver3";
				out.println(msg);
				out.flush();

				msg = setNum_ver3();//�����z�u�����肵���b�Z�[�W���M
				out.println(msg);
				out.flush();

				c.add(cheatbutton);
				cheatbutton.setBounds(1400,700,100,100);
				cheatbutton.addMouseListener(this);
				
			}else{//�A�C�R�����N���b�N�����Ƃ��̏���
				if(version == 4){
					for(int j = 0; j < 4; j++){
						for(int i = 0; i < 4; i++){
							Icon clickIconName = buttonArray[j][i].getIcon();
							if(clickIconName == theIcon){
								count++;
							}
						}
					}
					if((theIcon != boardIcon) && count > 0){
						int temp = Integer.parseInt(theArrayIndex);
						int checkx = temp % 4;
						int chechy = temp / 4;
						int notlocatiopn[] = location(chechy, checkx, version);
						int canputicon[] = putIcon(chechy, checkx, notlocatiopn, version);
						if(canputicon[1] == 1){//bordicon������Ƃ�
							String msg = "CHOOSE"+" "+theArrayIndex+" "+theIcon+" "+canputicon[0];//�T�[�o�ɏ��𑗂�
							out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
							out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
							repaint();//�I�u�W�F�N�g�̍ĕ`����s��
						}
					}
				}else if(version == 3){
					for(int j = 0; j < 3; j++){
						for(int i = 0; i < 3; i++){
							Icon clickIconName = buttonArray[j][i].getIcon();
							if(clickIconName == theIcon){
								count++;
							}
						}
					}
					if((theIcon != boardIcon) && count > 0){
						int temp = Integer.parseInt(theArrayIndex);
						int checkx = temp % 3;
						int chechy = temp / 3;
						int notlocatiopn[] = location(chechy, checkx, version);//�u���Ȃ��ꏊ�̍��W��z��Ɋi�[���郁�\�b�h�Ăяo��
						int canputicon[] = putIcon(chechy, checkx, notlocatiopn, version);//�u���Ȃ��ꏊ�������ɁA�u����ꏊ��z��Ɋi�[���郁�\�b�h�Ăяo��
						if(canputicon[1] == 1){//bordicon������Ƃ�
							String msg = "CHOOSE"+" "+theArrayIndex+" "+theIcon+" "+canputicon[0];//�T�[�o�ɏ��𑗂�
							out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
							out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
							repaint();//�I�u�W�F�N�g�̍ĕ`����s��
						}
					}
				}
			}
		}
		System.out.println("========================");
	}
	
	
	//boardIcon�̒u���Ȃ��ꏊ�A��O���v�Z���郁�\�b�h
	private int[]location(int y1, int x1, int version){
		int count = 0;
		int cannotlocation[] = new int[4];

		for(int j = -1; j < 2; j++){
			for(int i = -1; i< 2; i++){
				if((i*j == 0) && ((i != 0) || (j != 0))){
					if((i == 0) && (j == 0)){
						//System.out.println("�������g");
					}else {
						if((version == 4) && ((y1 + j < 0) || (y1 + j > 3) || (x1 + i < 0) || (x1 + i > 3))
							||((version ==3) && ((y1 + j < 0) || (y1 + j > 2) || (x1 + i < 0) || (x1 + i > 2))))
						{//��O
							cannotlocation[count] = j;
							cannotlocation[count+1] = i;
							//System.out.println("��O�́i "+j+" , "+i+" �j");
							count = 2;
						}

					}
				}
			}
		}
		return cannotlocation;
	}
	
	//�u���Ȃ��ꏊ�������ɁA�u����ꏊ��z��Ɋi�[���邵�ĕԂ����\�b�h
	private int[] putIcon(int y1, int x1, int[] notlocatiopn, int version){
		int canputicon[] = new int[2];
		int notsety1 = notlocatiopn[0];
		int notsetx1 = notlocatiopn[1];
		int notsety2 = notlocatiopn[2];
		int notsetx2 = notlocatiopn[3];
		canputicon[1] = 0;
		
		//System.out.println("!"+notsetx1+" ! "+notsety1+" ! "+notsetx2+" ! "+notsety2+" ! ");
		//int count = 0;
		
		for(int j = -1; j < 2; j++){
			for(int i = -1; i< 2; i++){
				if((i*j == 0) && ((i != 0) || (j != 0))){
					if(((j == notsety1) && (i == notsetx1)) || ((j == notsety2) && (i == notsetx2))){
						//System.out.println("�u���Ȃ�");
						//System.out.println("");
						
					}else{
						Icon IconName2 = buttonArray[y1 +j][x1 +i].getIcon();
						String falsecheckName2 = " "+IconName2;
						String checkName2 = falsecheckName2.substring(1);
						//System.out.println("checkName2 = " + checkName2);
						if(checkName2.equals("boardIcon.jpg")){
							int yy = y1 +j;
							int xx = x1 +i;
							
							if(version == 4){
								canputicon[0] = (y1 + j) * 4 + x1 +i;
								int dy = canputicon[0] % 4;
								int dx = canputicon[0] / 4;
							}else if(version ==3){
								canputicon[0] = (y1 + j) * 3 + x1 +i;
								int dy = canputicon[0] % 3;
								int dx = canputicon[0] / 3;
							}
							canputicon[1] = 1;
						}
					}
				}
			}
		}
		return canputicon;
	}


	//�����z�u�̌v�Z���s�����\�b�h�A4*4�̏ꍇ
	public String setNum_ver4(){
		int Num1[][] = new int[4][4];//���̔z��
		int Num2[][] = new int[4][4];//�m�F�p��Num1�̔z��
		int count = 0;//count = 16 �ł���Ε��ёւ��ł��Ă���
		int countsort = 0;//�\�[�g����̂ɂ���������
		int countMinmove = 0;//�󔒂��E���Ɏ����Ă���ŒZ����
		boolean flag = false;

		while(flag != true) {
			//0~15�܂ł�16�̐�����z��Num�Ɋi�[
			ArrayList<Integer> list = new ArrayList<Integer>();
			for(int i = 0; i < 16; i++) {
				list.add(i);
			}
			Collections.shuffle(list);
			for(int k = 0; k < 16; k++){
				Num1[k/4][k%4] = list.get(k);
				Num2[k/4][k%4] = list.get(k);
				//System.out.println(Num1[k/4][k%4]);
			}
			
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if(Num2[i][j] == 15){
						//System.out.println("y = " + i + ", x = " + j);
						countMinmove = 6 - i - j;
						//System.out.println("�ŏ�������" + countMinmove);
					}
				}
			}
			
			//�z��̒��g����ёւ���
			while(countsort != 16){
				for(int i = 0; i < 4; i++){
					for(int j = 0; j < 4; j++){
						//count�̏ꏊ��T���icount�������בւ���ׂ������ɂȂ��Ă���j
						if(Num2[i][j] == countsort){
							int x = countsort % 4;
							int y = countsort / 4;
							//���ۂɉ���u����������count�Ő�����
							if((i == y) && (j == x)){
							}else{
								count++;
							}
							//count���������ꏊ�����ς���
							Num2[i][j] = Num2[y][x];
							Num2[y][x] = countsort;
							//count�����ɐi�߂�
							countsort++;
							//System.out.println("countsort = " + countsort);
						}
					}
				}
			}
			//System.out.println("count = " + count);

			//�ȉ��f�o�b�N
			/*
			System.out.println("-----------------");
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					System.out.println(Num2[i][j]);
				}
			}
			System.out.println("-----------------");
			*/

			if((count % 2) == (countMinmove % 2)){
				flag = true;
				//System.out.println(flag);
			}else{
				//System.out.println(flag);
			}

		}

		String msg = "SETICONver4";
		//System.out.println("=====�����z�u=====");
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				//System.out.println(Num1[i][j]);
				msg = msg + " " + Num1[i][j];
			}
		}
		//System.out.println(msg);
		return msg;
	}


	//�����z�u�̌v�Z���s�����\�b�h�A3*3�̏ꍇ
	public String setNum_ver3(){
		int Num1[][] = new int[3][3];//���̔z��
		int Num2[][] = new int[3][3];//�m�F�p��Num1�̔z��
		int count = 0;//count = 9 �ł���Ε��ёւ��ł��Ă���
		int countsort = 0;//�\�[�g����̂ɂ���������
		int countMinmove = 0;//�󔒂��E���Ɏ����Ă���ŒZ����
		boolean flag = false;

		while(flag != true) {
			//0~8�܂ł�9�̐�����z��Num�Ɋi�[
			ArrayList<Integer> list = new ArrayList<Integer>();
			for(int i = 0; i < 9; i++) {
				list.add(i);
			}
			Collections.shuffle(list);
			for(int k = 0; k < 9; k++){
				Num1[k/3][k%3] = list.get(k);
				Num2[k/3][k%3] = list.get(k);
				//System.out.println(Num1[k/3][k%3]);
			}
			
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					if(Num2[i][j] == 8){
						//System.out.println("y = " + i + ", x = " + j);
						countMinmove = 4 - i - j;
						//System.out.println("�ŏ�������" + countMinmove);
					}
				}
			}
			//�z��̒��g����ёւ���
			while(countsort != 9){
				for(int i = 0; i < 3; i++){
					for(int j = 0; j < 3; j++){
						//count�̏ꏊ��T���icount�������בւ���ׂ������ɂȂ��Ă���j
						if(Num2[i][j] == countsort){
							int x = countsort % 3;
							int y = countsort / 3;
							//���ۂɉ���u����������count�Ő�����
							if((i == y) && (j == x)){
							}else{
								count++;
							}
							//count���������ꏊ�����ς���
							Num2[i][j] = Num2[y][x];
							Num2[y][x] = countsort;
							//count�����ɐi�߂�
							countsort++;
							//System.out.println("countsort = " + countsort);
						}
					}
				}
			}
			//System.out.println("count = " + count);

			//�ȉ��f�o�b�N
			/*
			System.out.println("-----------------");
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					System.out.println(Num2[i][j]);
				}
			}
			System.out.println("-----------------");
			*/

			if((count % 2) == (countMinmove % 2)){
				flag = true;
				//System.out.println(flag);
			}else{
				//System.out.println(flag);
			}

		}

		String msg = "SETICONver3";
		//System.out.println("=====�����z�u=====");
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				//System.out.println(Num1[i][j]);
				msg = msg + " " + Num1[i][j];
			}
		}
		return msg;
	}
	
	//�Q�[���̃��Z�b�g�����郁�\�b�h
	public void resetgame(){
		for(int j = 0;j<version;j++){
			for(int i=0; i<version;i++){
				buttonArray[j][i].setVisible(false);
				yourbuttonArray[j][i].setVisible(false);
			}
		}
		
		myclick = 0;//�N���b�N������
		version = 0;//3*3��4*4�̃��[�h
		imgmode = 0;//�摜�I���̃��[�h
		resetnum = 1;
		
		theLabeloriginal.setVisible(false);
		buttonbgm_on.setVisible(false);
		buttonreset.setVisible(false);
		countmyclick.setVisible(false);
		setSize(600,620);//�E�B���h�E�̃T�C�Y��ݒ肷��
		
		if(myTurn == 0){
			String msg = "PLAY";//PLAY���M�ŁA���[�h�I����ʂƑҋ@��ʂ�
			out.println(msg);
			out.flush();
		}
	}


	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}

	public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
		//System.out.println("�}�E�X��������");
	}

	public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
		//System.out.println("�}�E�X�E�o");
	}

	public void mousePressed(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏����i�N���b�N�Ƃ̈Ⴂ�ɒ��Ӂj
		//System.out.println("�}�E�X��������");
	}

	public void mouseReleased(MouseEvent e) {//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���
		//System.out.println("�}�E�X�������");
	}

	public void mouseDragged(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g�Ƃ��h���b�O���Ă���Ƃ��̏���
	}

	public void mouseMoved(MouseEvent e) {//�}�E�X���I�u�W�F�N�g��ňړ������Ƃ��̏���
	}
	
}

//�p�Y�������̃E�B���h�E��\�����郁�\�b�h
class FinishWindow extends JDialog implements ActionListener{
    FinishWindow(JFrame owner) {
        super(owner);//�Ăяo�����ƂƂ̐e�q�֌W�̐ݒ�D������R�����g�A�E�g����ƕʁX�̃_�C�A���O�ɂȂ�

		Container c = this.getContentPane();	//�t���[���̃y�C�����擾����
        c.setLayout(null);		//�������C�A�E�g�̐ݒ���s��Ȃ�

        JButton theButton = new JButton();//�摜��\��t���郉�x��
        ImageIcon theImage = new ImageIcon("./mainpic/finish.jpg");//�Ȃɂ��摜�t�@�C�����_�E�����[�h���Ă���
        theButton.setIcon(theImage);//���x����ݒ�
        theButton.setBounds(0,0,440,440);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
        theButton.addActionListener(this);//�{�^�����N���b�N�����Ƃ���actionPerformed�Ŏ󂯎�邽��
        c.add(theButton);//�_�C�A���O�ɓ\��t����i�\��t���Ȃ��ƕ\������Ȃ�

        setTitle("FINISH!");//�^�C�g���̐ݒ�
        setSize(440, 440);//�傫���̐ݒ�
        setResizable(false);//�g��k���֎~//true�ɂ���Ɗg��k���ł���悤�ɂȂ�
        setUndecorated(true); //�^�C�g����\�����Ȃ�
        setModal(true);//������܂ŉ���G��Ȃ�����ifalse�ɂ���ƐG���j

        //�_�C�A���O�̑傫����\���ꏊ��ύX�ł���
        //�e�̃_�C�A���O�̒��S�ɕ\���������ꍇ�́C�e�̃E�B���h�E�̒��S���W�����߂āC�q�̃_�C�A���O�̑傫���̔������炷
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialog��p������
    }
}
