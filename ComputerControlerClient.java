import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import java.awt.MouseInfo;
import java.awt.Point;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
public class ComputerControlerClient {
    static int serverPort = 0;
    static String serverIP = "";
    static Point mousePostion = new Point();
    Robot robot;
    public static void main() throws Exception {
        new ComputerControlerClient();
    }

    public ComputerControlerClient() throws Exception {
        robot = new Robot();
        Scanner scan = new Scanner(System.in);

        System.out.println("Client Running");
        System.out.println("What ip:port?");
        String serverIP = scan.nextLine();
        int serverPort = Integer.parseInt(serverIP.substring(serverIP.indexOf(":")+1));
        serverIP = serverIP.substring(0,serverIP.indexOf(":"));

        try {    
            Socket socket = new Socket(serverIP,serverPort);

            Thread listenThread = new Thread(new ListenThread(socket));
            Thread mouseThread = new Thread(new MouseInputThread());

            System.out.print("\f");
            System.out.println("Client");

            listenThread.start();
            mouseThread.start();
            while (true) {}
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void updatePoint(String point) {
        int x = Integer.parseInt(point.substring(2,6));
        int y = Integer.parseInt(point.substring(7));
        mousePostion = new Point(x,y);
    }

    public static void typeMessage(String text) throws Exception {
        Robot robot = new Robot();
        robot.setAutoDelay(750);
        //0 means enter
        for (char x: text.toCharArray()) {//a == 97-122 and A == 65-90
            int letter = 0;
            boolean uppercase = false;
            if (x == ' ') { 
                robot.keyPress(KeyEvent.VK_SPACE);
                robot.keyRelease(KeyEvent.VK_SPACE);
            }
            else if (x == '(') { 
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_9);
                robot.keyRelease(KeyEvent.VK_9);
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
            else if (x == ')') { 
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_0);
                robot.keyRelease(KeyEvent.VK_0);
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
            else if (x == ';') { 
                robot.keyPress(KeyEvent.VK_SEMICOLON);
                robot.keyRelease(KeyEvent.VK_SEMICOLON);
            }
            else if (x == '{') { 
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(91);
                robot.keyRelease(91);
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
            else if (x == '}') { 
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(93);
                robot.keyRelease(93);
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
            else if (x == '.') { 
                robot.keyPress(46);
                robot.keyRelease(46);
            }
            else if (x == '0') { 
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
            }
            else {
                letter = x;
                if (letter >= 65 && letter <= 90) { //uppercase
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    uppercase = true;
                }
                else {
                    letter = letter - 32;
                }

                robot.keyPress(letter);
                robot.keyRelease(letter);
                if (uppercase) {
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                }
            }
        }
    }

    //WIP
    public static void click(boolean d, String button) throws Exception {
        Robot robot  = new Robot();
        int leftButton = InputEvent.getMaskForButton(1);//left
        int rightButton = InputEvent.getMaskForButton(2);//right

        robot.setAutoDelay(20);

        if (button.equals("r")) {

        }
        

    }

    public class MouseInputThread implements Runnable {
        public void run() {
            while (true) {
                robot.mouseMove((int)mousePostion.getX(),(int)mousePostion.getY());
            }
        }
    }

    public class ListenThread implements Runnable {
        private Socket socket;
        public ListenThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                while (socket.isClosed() == false) {
                    try {
                        String message = br.readLine();
                        if (message.substring(0,1).equals("M")){//M ####/####
                            updatePoint(message);
                        }
                        else if (message.substring(0,4).equals("type")){//type messagethatwillbetypedhere
                            typeMessage(message.substring(5));
                        }
                        else if (message.substring(0,5).equals("click")){
                            //click l or click r
                            click(false,message.substring(6));
                        }
                        else if (message.substring(0,5).equals("dclick")){
                            click(true,message.substring(7));
                        }
                        else {
                            System.out.println(message);
                        }
                    }
                    catch (Exception err) {
                        err.printStackTrace();
                        System.exit(0);
                    }
                }
            }
            catch(Exception err) {
                err.printStackTrace();
                System.exit(0);
            }
        }

    }
}