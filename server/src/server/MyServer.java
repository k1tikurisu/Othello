package server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class MyServer {
	private static final int maxConnection = 100;
	private static Socket[] incoming;
	private static boolean[] flag;
	private static InputStreamReader[] isr;
	private static BufferedReader[] in;
	private static PrintWriter[] out;
	private static ClientThread[] myClientThread;
	private static int member;

	//全員にメッセージを送る
	public static void SendAll(String str, String myName) {
		//送られた来たメッセージを接続している全員に配る
		for (int i = 1; i <= member; i++) {
			if (flag[i] == true) {
				out[i].println(str);
				out[i].flush();//バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
				System.out.println("Send messages to client No." + i);
			}
		}	
	}
	
	//フラグの設定を行う
	public static void SetFlag(int n, boolean value) {
		flag[n] = value;
	}
	
	public static void main(String[] args) {
		incoming = new Socket[maxConnection];
		flag = new boolean[maxConnection];
		isr = new InputStreamReader[maxConnection];
		in = new BufferedReader[maxConnection];
		out = new PrintWriter[maxConnection];
		myClientThread = new ClientThread[maxConnection];
		
		int n = 1;
		member = 0;

		try {
			System.out.println("The server has launched!");
			ServerSocket server = new ServerSocket(10000);
			
			while (true) {
				incoming[n] = server.accept();
				flag[n] = true;
				System.out.println("Accept client No." + n);

				//必要な入出力ストリームを作成する
				isr[n] = new InputStreamReader(incoming[n].getInputStream());
				in[n] = new BufferedReader(isr[n]);
				out[n] = new PrintWriter(incoming[n].getOutputStream(), true);
				
				//必要なパラメータを渡しスレッドを作成
				myClientThread[n] = new ClientThread(n, incoming[n], isr[n], in[n], out[n]);
				myClientThread[n].start();
				member = n;
				n++;
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}
}