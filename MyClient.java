import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;//画像処理に必要
import java.awt.geom.*;//画像処理に必要
import java.util.Collections;//重複しない乱数生成
import java.util.ArrayList;//重複しない乱数生成
import java.io.IOException;
import javax.imageio.ImageIO;
import java.applet.*;//wavファイルの再生に使用


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
	
	private JButton buttonArray[][], yourbuttonArray[][];//ボタン用の配列
	private JButton buttonset3, buttonset4;//マス選択ボタン
	private JButton buttondog, buttongiraffe, buttonnumber, buttoncat;//モード選択ボタン
	private JButton buttonplayGame;//ゲーム開始ボタン
	private JButton cheatbutton;//チートボタン
	private JButton buttonbgm_on;//bgmボタン
	private JButton buttonreset;
	
	JLabel theLabeloriginal;//見本画像
	JLabel countmyclick;//動かした回数を表示
	JLabel waitinglabel;//待機画面
	JLabel backgroundpic;//背景画面
	JLabel explainlabel;
	JLabel startlabel;//スタート画面

	int myTurn;//ターン
	int myclick = 0;//クリックした回数
	int version = 0;//3*3か4*4のモード
	int imgmode = 0;//画像選択のモード
	int bgm = 0;//BGM種類
	int resetnum = 0;//リセットしたか管理、0なら初回

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

	PrintWriter out;//出力用のライター

	public MyClient() {

		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}

		//IPアドレスの入力ダイアログを開く
		String IPName = JOptionPane.showInputDialog(null,"IPアドレスを入力してください","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);
		if(IPName.equals("")){
			IPName = "localhost";//名前がないときは，"localhost"とする
		}


		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyClient");//ウィンドウのタイトルを設定する
		setSize(600,620);//ウィンドウのサイズを設定する
		c = getContentPane();//フレームのペインを取得する
		c.setLayout(null);//自動レイアウトの設定を行わない
		c.setBackground(Color.WHITE);//ウィンドウの色の設定
		
		
		//boardIconの宣言
		boardIcon = new ImageIcon("boardIcon.jpg");
		yourboardIcon = new ImageIcon("yourboardIcon.jpg");
		
		//スタート画面のラベル
		IconStart = new ImageIcon("./mainpic/start.jpg");
		startlabel = new JLabel(IconStart);
		c.add(startlabel);
		startlabel.setBounds(0,0,600,450);
		
		//ゲーム開始のボタン
		IconPlay = new ImageIcon("./mainpic/PlayGame.jpg");
		buttonplayGame = new JButton(IconPlay);
		c.add(buttonplayGame);
		buttonplayGame.setBounds(120,400,360,150);
		buttonplayGame.addMouseListener(this);
		
		//モード選択画面のラベル
		Iconexplain = new ImageIcon("./mainpic/explain.jpg");
		
		//bgmの再生開始
		SoundWav(bgm);
		
		
		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket(IPName, 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}

		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
	}
	
	
	/*
	//=============stop()は一度しか使えないため、待機画面処理にこのスレッドは使えなかった(泣)============
	// 新たに待機処理用にスレッドを立てる
	public class Waiting extends Thread{
		public void run(){
			add(p,BorderLayout.CENTER);
			p.setLayout(null);//自動レイアウトの設定を行わない
			p.setSize(600,600);//パネルのサイズはウィンドウに合わせる
			p.setBackground(Color.WHITE);//ウィンドウの色の設定

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
					Thread.sleep(1000); //1000ミリ秒Sleepする
				}catch(InterruptedException ee){}
				if(i == 3){
					i = -1;
				}
			}
		}
	}
	//=============stop()は一度しか使えないため、待機画面処理にこのスレッドは使えなかった(泣)============
	*/
	
	
	
	//メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {
		Socket socket;
		String myName;

		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}

		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続の最初に名前を送る
				String myNumberStr = br.readLine();
				int myNumberInt = Integer.parseInt(myNumberStr);
				
				ImageIcon iconoriginal = new ImageIcon("boardIcon.jpg");//見本画像
				
				cheatbutton = new JButton("チート");//チートボタン
				
				ImageIcon waitingicon = new ImageIcon("./mainpic/waiting.gif");//待機画面
				waitinglabel = new JLabel(waitingicon);
				
				
				while(true) {
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						//System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						
						if(cmd.equals("PLAY")){
							System.out.println("PLAY受信");
							
							//BGMボタンの作成
							ImageIcon Iconbgm = new ImageIcon("./mainpic/bgmbutton.jpg");
							buttonbgm_on = new JButton(Iconbgm);
							
							//リセットボタンの作成
							ImageIcon Iconreset = new ImageIcon("./mainpic/resetbutton.jpg");
							buttonreset = new JButton(Iconreset);
							
							//クリックカウントのラベル作成
							countmyclick = new JLabel("0");
							countmyclick.setFont(new Font("Arial", Font.BOLD, 50));
							
							//見本画像のラベル作成
							theLabeloriginal = new JLabel(iconoriginal);
							
							//myTurnの決定
							if(myNumberInt % 2 == 0){
								myTurn = 0;
							}else{
								myTurn = 1;
							}
							System.out.println("myTurn = " + myTurn);
							
							buttonplayGame.setVisible(false);
							startlabel.setVisible(false);
							
							buttonmode(myTurn);//各モード選択ボタンを作るメソッド呼び出し
							
						}

						if(cmd.equals("MODE")){
							System.out.print("MODE受信 : ");
							String cmd2 = inputTokens[1];
							if(cmd2.equals("DOG")){
								imgmode = 1;//犬モード
								System.out.println("DOG選択");
							}else if(cmd2.equals("GIRAFFE")){
								imgmode = 2;//キリンモード
								System.out.println("GIRAFFE選択");
							}else if(cmd2.equals("NUMBER")){
								imgmode = 3;//数字モード
								System.out.println("NUMBER選択");
							}else if(cmd2.equals("CAT")){
								imgmode = 4;//猫モード
								System.out.println("CAT選択");
							}
							
							
							if(myTurn == 0){
								//各モード選択ボタンを非表示にする
								buttondog.setVisible(false);
								buttongiraffe.setVisible(false);
								buttonnumber.setVisible(false);
								buttoncat.setVisible(false);
								
								buttonset(myTurn);//ver3かver4を選択するボタンを作るメソッド呼び出し
							}
							
							//右に表示する見本画像のラベル
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
						
						//4*4の大きさのbuttonArrayとyourbuttonArrayを用意する
						if(cmd.equals("SETver4")){
							System.out.println("4*4モード選択");
							version = 4;//4*4モード
							
							setSize(1300,600);//ウィンドウのサイズを設定する
							if(myTurn == 0){
								buttonset3.setVisible(false);
								buttonset4.setVisible(false);
								explainlabel.setVisible(false);
							}
							
							//ボタンの生成
							buttonArray = new JButton[4][4];//ボタンの配列を５個作成する[0]から[4]まで使える
							yourbuttonArray = new JButton[4][4];//ボタンの配列を５個作成する[0]から[4]まで使える
							
							SETver4();//ボタンに画像貼り付けるメソッド呼び出し
							
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
						
						//3*3の大きさのbuttonArrayとyourbuttonArrayを用意する
						if(cmd.equals("SETver3")){
							System.out.println("3*3モード選択");
							version = 3;//3*3モード
							
							setSize(1100,500);//ウィンドウのサイズを設定する
							if(myTurn == 0){
								buttonset3.setVisible(false);
								buttonset4.setVisible(false);
								explainlabel.setVisible(false);
							}
							
							//ボタンの生成
							buttonArray = new JButton[3][3];//ボタンの配列を５個作成する[0]から[4]まで使える
							yourbuttonArray = new JButton[3][3];//ボタンの配列を５個作成する[0]から[4]まで使える
							
							SETver3();//ボタンに画像貼り付けるメソッド呼び出し
							
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

						//接続後画像をランダムに張り付ける
						if(cmd.equals("SETICONver4")){
							System.out.println("SETICON受信ver4");
							
							for(int j = 0;j<4;j++){
								for(int i=0; i<4;i++){
									String strlocation = inputTokens[1 + j*4+i];
									int location = Integer.parseInt(strlocation);
									String nameicon = "boardIcon.jpg";
									String ynameicon = "yourboardIcon.jpg";
									
									if(location == 15){//15にはboardiconを貼り付ける
										buttonArray[j][i].setIcon(boardIcon);
										yourbuttonArray[j][i].setIcon(yourboardIcon);
									}else{
										if(imgmode == 1){//犬モードのとき
											nameicon = "./dog/" + name[location];
											ynameicon = "./dog/" + yname[location];
										}else if(imgmode == 2){//キリンモードのとき
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
							
							waitinglabel.setVisible(false);//待機画面を消す
							
							//=============stop()は一度しか使えないため、待機画面処理にこのスレッドは使えなかった(泣)============
							/*
							//スレッドを止める
							t.stop();
							System.out.println("stop!!!");
							p.setVisible(false);
							*/
							//=============stop()は一度しか使えないため、待機画面処理にこのスレッドは使えなかった(泣)============
							
						}
						
						
						if(cmd.equals("SETICONver3")){
							System.out.println("SETICONver3受信");
							
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
										if(imgmode == 1){//犬モードのとき
											nameicon = "./dog3/" + name[location];
											ynameicon = "./dog3/" + yname[location];
										}else if(imgmode == 2){//キリンモードのとき
											nameicon = "./giraffe3/" + name[location];
											ynameicon = "./giraffe3/" + yname[location];
										}else if(imgmode == 3){//数字モードのときs
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
							
							waitinglabel.setVisible(false);//待機画面を消す
							
							//=============stop()は一度しか使えないため、待機画面処理にこのスレッドは使えなかった(泣)============
							/*
							//スレッドを止める
							t.stop();
							System.out.println("stop!!!");
							p.setVisible(false);
							*/
							//=============stop()は一度しか使えないため、待機画面処理にこのスレッドは使えなかった(泣)============
							
						}

						if(cmd.equals("CHOOSE")){
							System.out.println("CHOOSE受信");
							
							int x1 = 0;
							int y1 = 0;
							int boardx = 0;
							int boardy = 0;
							int memory1 = 0;
							int memory2 = 0;
							
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
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
							//System.out.println("（x,y） = （ "+ boardx + " , " + boardy + " ） に再確認");
							
							if(myTurn == 0){
								buttonArray[y1][x1].setIcon(boardIcon);
								buttonArray[boardy][boardx].setIcon(Icon1);
								
								//動かした回数のカウント
								myclick++;
								String myclickstr = Integer.toString(myclick);
								countmyclick.setText(myclickstr);
								
							}else{
								yourbuttonArray[y1][x1].setIcon(yourboardIcon);
								
								//自分と相手のアイコンを分ける
								String[] splitname = theIconName.split("/");//[0]は. [1]はモード名 [2]は画像名
								String your_theIconName = splitname[0] + "/" + splitname[1] + "/y" + splitname[2];
								//System.out.println("て　　い　　せ　　　い　　　ば　　　ん　　　your_theIconName  =======   " + your_theIconName);
								Icon1 = new ImageIcon(your_theIconName);
								yourbuttonArray[boardy][boardx].setIcon(Icon1);
							}
							
							judge(version, imgmode);
							myTurn = 1 - myTurn;
							//System.out.println("myTurn : " + myTurn);
							//System.out.println("======================");
						}
						
						
						if(cmd.equals("CHEAT")){
							System.out.println("CHEAT受信");
							cheatbutton();
						}
						
						
						if(cmd.equals("RESET")){
							System.out.println("RESET受信");
							resetgame();
						}
						
					}else{
						break;
					}

				}
				socket.close();
			} catch (IOException e) {
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}


	//チートボタン
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
	

	////ver3かver4を選択するボタンを作るメソッド
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


	//各モード選択ボタンを作るメソッド
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
		//自分のbuttonArrayを作る
		for(int j = 0;j<4;j++){
			for(int i=0; i<4;i++){
				buttonArray[j][i] = new JButton(boardIcon);
				c.add(buttonArray[j][i]);//ペインに貼り付ける
				buttonArray[j][i].setBounds(i*100+10,j*100+10,100,100);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
				buttonArray[j][i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
				buttonArray[j][i].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
				buttonArray[j][i].setActionCommand(Integer.toString(j*4+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
			}
		}
		
		//相手のbuttonArrayを作る
		for(int j=0;j<4;j++){
			for(int i=0; i<4;i++){
				yourbuttonArray[j][i] = new JButton(yourboardIcon);
				c.add(yourbuttonArray[j][i]);//ペインに貼り付ける
				yourbuttonArray[j][i].setBounds(i*100+510,j*100+10,100,100);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
				yourbuttonArray[j][i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
				yourbuttonArray[j][i].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
				yourbuttonArray[j][i].setActionCommand(Integer.toString(j*4+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
			}
		}
		buttonbgm_on.addMouseListener(this);//bgmボタンをクリックしたときに反応させる
	}

	private void SETver3(){
		//自分のbuttonArrayを作る
		for(int j = 0;j<3;j++){
			for(int i=0; i<3;i++){
				buttonArray[j][i] = new JButton(boardIcon);
				c.add(buttonArray[j][i]);//ペインに貼り付ける
				buttonArray[j][i].setBounds(i*100+10,j*100+10,100,100);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
				buttonArray[j][i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
				buttonArray[j][i].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
				buttonArray[j][i].setActionCommand(Integer.toString(j*3+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
			}
		}
		
		//相手のbuttonArrayを作る
		for(int j=0;j<3;j++){
			for(int i=0; i<3;i++){
				yourbuttonArray[j][i] = new JButton(yourboardIcon);
				c.add(yourbuttonArray[j][i]);//ペインに貼り付ける
				yourbuttonArray[j][i].setBounds(i*100+410,j*100+10,100,100);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
				yourbuttonArray[j][i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
				yourbuttonArray[j][i].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
				yourbuttonArray[j][i].setActionCommand(Integer.toString(j*3+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
			}
		}
		buttonbgm_on.addMouseListener(this);//bgmボタンをクリックしたときに反応させる
	}


	//終了判定
	private void judge(int version, int imgmode){
		int collectNum = 0;
		String iconnames = null;

		if(version == 4){
			//System.out.println("----------------------");
			for(int j=0;j<4;j++){
				for(int i=0; i<4;i++){
					Icon IconName = buttonArray[j][i].getIcon();

					String falsename = " "+IconName;//アイコンをストリングで取り出す処理
					String rename = falsename.substring(1);//1文字目のスペースを消した、本当のアイコンの名前
					//System.out.println(rename);//デバッグ用に
					
					if(imgmode == 1){//dogのとき
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
					System.out.println("パズル完成!");
					if(bgm == 0){
						bgm = 1;
						SoundWav(bgm);
						bgm = 0;
					}
					FinishWindow dlg = new FinishWindow(this);
					setVisible(true);
					buttonreset.addMouseListener(this);//bgmボタンをクリックしたときに反応させる
				}
				
			}
		}else if(version == 3){
			for(int j=0;j<3;j++){
				for(int i=0; i<3;i++){
					Icon IconName = buttonArray[j][i].getIcon();
					String falsename = " "+IconName;//アイコンをストリングで取り出す処理
					String rename = falsename.substring(1);//1文字目のスペースを消した、本当のアイコンの名前
					//System.out.println(rename);//デバッグ用に
					
					if(imgmode == 1){//dogのとき
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
					System.out.println("完成です!");
					if(bgm == 0){
						bgm = 1;
						SoundWav(bgm);
						bgm = 0;
					}
					FinishWindow dlg = new FinishWindow(this);
					setVisible(true);
					buttonreset.addMouseListener(this);//bgmボタンをクリックしたときに反応させる
				}
			}
		}
	}
	
	
	//BGMの管理
	public void SoundWav(int bgm){
		if(bgm == 0){
			clip.loop();//BGMの再生
		}else if(bgm == 1){
			clip1.play();//完成SEの再生
		}else if(bgm == 100){
			clip.stop();//BGMを止める
		}
	}

	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
		System.out.println("========================");
		int count = 0;
		
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
		Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る
		System.out.println("クリックしたのは  " + theIcon);
		
		if(theButton == buttonbgm_on){
			//System.out.println("BGMボタン押したよ！");
			if(bgm == 0){//BGMがONのときOFFにする
				bgm = 100;//100のときBGMをOFFにする
				SoundWav(bgm);
			}else{//BGMがOFFのときONにする
				bgm = 0;//0のときはBGMはON
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

				msg = setNum_ver4();//初期配置を決定しメッセージ送信
				out.println(msg);
				out.flush();

				c.add(cheatbutton);
				cheatbutton.setBounds(1400,700,100,100);
				cheatbutton.addMouseListener(this);
			}else if(theButton == buttonset3){
				String msg = "SETver3";
				out.println(msg);
				out.flush();

				msg = setNum_ver3();//初期配置を決定しメッセージ送信
				out.println(msg);
				out.flush();

				c.add(cheatbutton);
				cheatbutton.setBounds(1400,700,100,100);
				cheatbutton.addMouseListener(this);
				
			}else{//アイコンをクリックしたときの処理
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
						if(canputicon[1] == 1){//bordiconがあるとき
							String msg = "CHOOSE"+" "+theArrayIndex+" "+theIcon+" "+canputicon[0];//サーバに情報を送る
							out.println(msg);//送信データをバッファに書き出す
							out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
							repaint();//オブジェクトの再描画を行う
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
						int notlocatiopn[] = location(chechy, checkx, version);//置けない場所の座標を配列に格納するメソッド呼び出し
						int canputicon[] = putIcon(chechy, checkx, notlocatiopn, version);//置けない場所を引数に、置ける場所を配列に格納するメソッド呼び出し
						if(canputicon[1] == 1){//bordiconがあるとき
							String msg = "CHOOSE"+" "+theArrayIndex+" "+theIcon+" "+canputicon[0];//サーバに情報を送る
							out.println(msg);//送信データをバッファに書き出す
							out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
							repaint();//オブジェクトの再描画を行う
						}
					}
				}
			}
		}
		System.out.println("========================");
	}
	
	
	//boardIconの置けない場所、場外を計算するメソッド
	private int[]location(int y1, int x1, int version){
		int count = 0;
		int cannotlocation[] = new int[4];

		for(int j = -1; j < 2; j++){
			for(int i = -1; i< 2; i++){
				if((i*j == 0) && ((i != 0) || (j != 0))){
					if((i == 0) && (j == 0)){
						//System.out.println("自分自身");
					}else {
						if((version == 4) && ((y1 + j < 0) || (y1 + j > 3) || (x1 + i < 0) || (x1 + i > 3))
							||((version ==3) && ((y1 + j < 0) || (y1 + j > 2) || (x1 + i < 0) || (x1 + i > 2))))
						{//場外
							cannotlocation[count] = j;
							cannotlocation[count+1] = i;
							//System.out.println("場外は（ "+j+" , "+i+" ）");
							count = 2;
						}

					}
				}
			}
		}
		return cannotlocation;
	}
	
	//置けない場所を引数に、置ける場所を配列に格納するして返すメソッド
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
						//System.out.println("置けない");
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


	//初期配置の計算を行うメソッド、4*4の場合
	public String setNum_ver4(){
		int Num1[][] = new int[4][4];//元の配列
		int Num2[][] = new int[4][4];//確認用のNum1の配列
		int count = 0;//count = 16 であれば並び替えできている
		int countsort = 0;//ソートするのにかかった回数
		int countMinmove = 0;//空白を右下に持ってくる最短距離
		boolean flag = false;

		while(flag != true) {
			//0~15までの16個の数字を配列Numに格納
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
						//System.out.println("最小距離は" + countMinmove);
					}
				}
			}
			
			//配列の中身を並び替える
			while(countsort != 16){
				for(int i = 0; i < 4; i++){
					for(int j = 0; j < 4; j++){
						//countの場所を探す（countが次並べ替えるべき数字になっている）
						if(Num2[i][j] == countsort){
							int x = countsort % 4;
							int y = countsort / 4;
							//実際に何回置換したかをcountで数える
							if((i == y) && (j == x)){
							}else{
								count++;
							}
							//countがあった場所を入れ変える
							Num2[i][j] = Num2[y][x];
							Num2[y][x] = countsort;
							//countを次に進める
							countsort++;
							//System.out.println("countsort = " + countsort);
						}
					}
				}
			}
			//System.out.println("count = " + count);

			//以下デバック
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
		//System.out.println("=====初期配置=====");
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				//System.out.println(Num1[i][j]);
				msg = msg + " " + Num1[i][j];
			}
		}
		//System.out.println(msg);
		return msg;
	}


	//初期配置の計算を行うメソッド、3*3の場合
	public String setNum_ver3(){
		int Num1[][] = new int[3][3];//元の配列
		int Num2[][] = new int[3][3];//確認用のNum1の配列
		int count = 0;//count = 9 であれば並び替えできている
		int countsort = 0;//ソートするのにかかった回数
		int countMinmove = 0;//空白を右下に持ってくる最短距離
		boolean flag = false;

		while(flag != true) {
			//0~8までの9個の数字を配列Numに格納
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
						//System.out.println("最小距離は" + countMinmove);
					}
				}
			}
			//配列の中身を並び替える
			while(countsort != 9){
				for(int i = 0; i < 3; i++){
					for(int j = 0; j < 3; j++){
						//countの場所を探す（countが次並べ替えるべき数字になっている）
						if(Num2[i][j] == countsort){
							int x = countsort % 3;
							int y = countsort / 3;
							//実際に何回置換したかをcountで数える
							if((i == y) && (j == x)){
							}else{
								count++;
							}
							//countがあった場所を入れ変える
							Num2[i][j] = Num2[y][x];
							Num2[y][x] = countsort;
							//countを次に進める
							countsort++;
							//System.out.println("countsort = " + countsort);
						}
					}
				}
			}
			//System.out.println("count = " + count);

			//以下デバック
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
		//System.out.println("=====初期配置=====");
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				//System.out.println(Num1[i][j]);
				msg = msg + " " + Num1[i][j];
			}
		}
		return msg;
	}
	
	//ゲームのリセットをするメソッド
	public void resetgame(){
		for(int j = 0;j<version;j++){
			for(int i=0; i<version;i++){
				buttonArray[j][i].setVisible(false);
				yourbuttonArray[j][i].setVisible(false);
			}
		}
		
		myclick = 0;//クリックした回数
		version = 0;//3*3か4*4のモード
		imgmode = 0;//画像選択のモード
		resetnum = 1;
		
		theLabeloriginal.setVisible(false);
		buttonbgm_on.setVisible(false);
		buttonreset.setVisible(false);
		countmyclick.setVisible(false);
		setSize(600,620);//ウィンドウのサイズを設定する
		
		if(myTurn == 0){
			String msg = "PLAY";//PLAY送信で、モード選択画面と待機画面に
			out.println(msg);
			out.flush();
		}
	}


	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}

	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
		//System.out.println("マウスが入った");
	}

	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
		//System.out.println("マウス脱出");
	}

	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
		//System.out.println("マウスを押した");
	}

	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
		//System.out.println("マウスを放した");
	}

	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラッグしているときの処理
	}

	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したときの処理
	}
	
}

//パズル完成のウィンドウを表示するメソッド
class FinishWindow extends JDialog implements ActionListener{
    FinishWindow(JFrame owner) {
        super(owner);//呼び出しもととの親子関係の設定．これをコメントアウトすると別々のダイアログになる

		Container c = this.getContentPane();	//フレームのペインを取得する
        c.setLayout(null);		//自動レイアウトの設定を行わない

        JButton theButton = new JButton();//画像を貼り付けるラベル
        ImageIcon theImage = new ImageIcon("./mainpic/finish.jpg");//なにか画像ファイルをダウンロードしておく
        theButton.setIcon(theImage);//ラベルを設定
        theButton.setBounds(0,0,440,440);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
        theButton.addActionListener(this);//ボタンをクリックしたときにactionPerformedで受け取るため
        c.add(theButton);//ダイアログに貼り付ける（貼り付けないと表示されない

        setTitle("FINISH!");//タイトルの設定
        setSize(440, 440);//大きさの設定
        setResizable(false);//拡大縮小禁止//trueにすると拡大縮小できるようになる
        setUndecorated(true); //タイトルを表示しない
        setModal(true);//上を閉じるまで下を触れなくする（falseにすると触れる）

        //ダイアログの大きさや表示場所を変更できる
        //親のダイアログの中心に表示したい場合は，親のウィンドウの中心座標を求めて，子のダイアログの大きさの半分ずらす
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialogを廃棄する
    }
}
