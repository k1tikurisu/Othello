package client;

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class MyClient extends JFrame {
	private JButton buttonArray[][];
	private ImageIcon black, white, board;
  private Container c;
  PrintWriter out;

	public MyClient() {
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null, "名前を入力してください", "名前の入力", JOptionPane.QUESTION_MESSAGE);
		// 名前の入力がないとき
    if (myName.equals("")) {
			myName = "No name";
    }
    
    // IPアドレスを入力するダイアログを開く
		String myIpAddress = JOptionPane.showInputDialog(null, "IPアドレスを入力してください", "IPアドレスの入力", JOptionPane.QUESTION_MESSAGE);
    if (myIpAddress.equals("")) {
			myIpAddress = "localhost";
    }

		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Othello Game");
		setSize(1000, 750);
		c = getContentPane();

		white = new ImageIcon("./resources/white.png");
		black = new ImageIcon("./resources/black.png");
		board = new ImageIcon("./resources/board.png");
		
		// 自動レイアウトの設定を行わない
    c.setLayout(null);

		// 盤面を作成する
		buttonArray = new JButton[8][8];
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				buttonArray[i][j] = new JButton(board);
				// ボーダーを消す
				buttonArray[i][j].setBorderPainted(false);
				c.add(buttonArray[i][j]);
				buttonArray[i][j].setBounds(i*50+300, j*50+150, 50, 50);
			}
		}
		
		// 初期配置
		buttonArray[4][3].setIcon(white);
		buttonArray[4][4].setIcon(black);
		buttonArray[3][4].setIcon(white);
		buttonArray[3][3].setIcon(black);
		
		//サーバに接続する
		Socket socket = null;
		try {
			socket = new Socket("localhost", 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);
		mrt.start();
	}
		
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
				out.println(myName);
				socket.close();
			} catch (IOException e) {
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
}