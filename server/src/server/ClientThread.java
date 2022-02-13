package server;

import java.net.Socket;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

class ClientThread extends Thread {
	private int number;//自分の番号
	private Socket incoming;
	private InputStreamReader myIsr;
	private BufferedReader myIn;
	private PrintWriter myOut;
	private String myName;//接続者の名前

	public ClientThread(int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
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