import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;

import java.awt.MouseInfo;
import java.awt.Point;
public class ComputerControlerServer{
    public static void main() throws Exception{
        new ComputerControlerServer();
    }

    public ComputerControlerServer() {
        try {
            int serverPort = 42069;
            ServerSocket serversocket = new ServerSocket(serverPort);
            System.out.println("Running! Connect At: "+InetAddress.getLocalHost().getHostAddress()+":"+serverPort);

            try {//accept
                Socket client = serversocket.accept();
                Thread talkThread = new Thread(new TalkThread(client));
                Thread mouseThread = new Thread(new MouseThread(client));
                talkThread.start();
                mouseThread.start();
                System.out.print("\f");
                System.out.println("Server");
            }
            catch(Exception err) {
                err.printStackTrace();
            }

            while (true) {}
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    } 

    public class TalkThread implements Runnable {
        private Socket socket;
        public TalkThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                Scanner scan = new Scanner(System.in);
                while (true) {       
                    String message = scan.nextLine();

                    OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
                    PrintWriter pw = new PrintWriter(osw,true);
                    pw.println(message);
                }
            }
            catch(Exception err) {
                err.printStackTrace();
                System.exit(0);
            }
        }
    }

    public class MouseThread implements Runnable {
        private Socket socket;
        public MouseThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            Point point = MouseInfo.getPointerInfo().getLocation();
            int x = (int)point.getX();
            int y = (int)point.getY();
            String strX = "";
            String strY = "";
            String message = "";
            try {
                Scanner scan = new Scanner(System.in);
                while (true) { 
                    delay(100);
                    strX = "";
                    strY = "";
                    message = "";
                    //System.out.print("\f");
                    point = MouseInfo.getPointerInfo().getLocation();
                    x = (int)point.getX();
                    y = (int)point.getY();
                    //System.out.println("("+x+"/"+y+")");
                    if (x <= 9) {
                        strX += "0";
                    }
                    if (x <= 99) {
                        strX += "0";
                    }
                    if (x <= 999) {
                        strX += "0";
                    }         
                    strX += Integer.toString(x);
                    if (y <= 9) {
                        strY += "0";
                    }
                    if (y <= 99) {
                        strY += "0";
                    }
                    if (y <= 999) {
                        strY += "0";
                    } 
                    strY += Integer.toString(y);
                    
                    message = "M " + strX + "/" + strY;
                    //System.out.println(message);
                                        
                    OutputStreamWriter osw_mouse = new OutputStreamWriter(socket.getOutputStream());
                    PrintWriter pw_mouse = new PrintWriter(osw_mouse,true);
                    pw_mouse.println(message);
                }
            }
            catch(Exception err) {
                err.printStackTrace();
                System.exit(0);
            }
        }
    }
    public static void delay(int x) {
        try{
            Thread.sleep(x);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}