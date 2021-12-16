package server;

import java.net.ServerSocket;
import java.net.Socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

class ClientProcThread extends Thread {
	private int number;//自分の番号
	private Socket incoming;
	private InputStreamReader myIsr;
	private BufferedReader myIn;
	private PrintWriter myOut;
	private String myName;//接続者の名前

	public ClientProcThread(int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
		number = n;
		incoming = i;
		myIsr = isr;
		myIn = in;
		myOut = out;
	}

	public void run() {
		try {
			myOut.println(number);

			myName = myIn.readLine();
		
			//ソケットへの入力を監視する
			while (true) {
				String str = myIn.readLine();
				System.out.println("Received from client No."+ number + "(" + myName + "), Messages: " + str);

				//このソケット(バッファ)に入力があるかをチェック
				if (str != null) {
					if (str.toUpperCase().equals("BYE")) {
						myOut.println("Good bye!");
						break;
					}
					MyServer.SendAll(str, myName);
				}
			}
		} catch (Exception e) {
			//ここにプログラムが到達するときは，接続が切れたとき
			System.out.println("Disconnect from client No." + number + "(" + myName + ")");
			MyServer.SetFlag(number, false);//接続が切れたのでフラグを下げる
		}
	}
}

public class MyServer {
	private static final int maxConnection = 100;
	private static Socket[] incoming;
	private static boolean[] flag;
	private static InputStreamReader[] isr;
	private static BufferedReader[] in;
	private static PrintWriter[] out;
	private static ClientProcThread[] myClientProcThread;
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
		myClientProcThread = new ClientProcThread[maxConnection];
		
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
				myClientProcThread[n] = new ClientProcThread(n, incoming[n], isr[n], in[n], out[n]);
				myClientProcThread[n].start();
				member = n;
				n++;
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}
}