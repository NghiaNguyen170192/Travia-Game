/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import Client.Client;
import java.awt.Color;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author CHINHNHAN
 */
public class Server extends JFrame {

    private JPanel panel = new JPanel();
    private JTextArea textArea = new JTextArea(30,30);
    private JScrollPane scrollPane = new JScrollPane(textArea);
    private static ArrayList<String> quest = new ArrayList<>();
    private static ArrayList<String> ans = new ArrayList<>();
    private static Server server;
    private static Set<String> runningClient = new HashSet<>();
    private JTable scoreTable = null;
    private Object [][] data = new Object[100][10];
    private ArrayList [][] data1 = new ArrayList[100][10];

    public Server() {
        boolean signin = false;
        JTextField un = new JTextField(10);
        JPasswordField pw = new JPasswordField(10);
        Object[] msg = {"Enter admin user name ", un, "\nEnter admin password", pw};
        do {
            JOptionPane op = new JOptionPane(msg, JOptionPane.QUESTION_MESSAGE);
            JDialog dia = op.createDialog("Enter username and password");
            dia.setVisible(true);

            if (op.getValue() != null) {
                if (un.getText().equalsIgnoreCase("admin") && pw.getText().equalsIgnoreCase("admin")) {
                    signin = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong username or password");
                    un.setText("");
                    pw.setText("");
                    signin = false;
                }
            } else {
                System.out.println("null");
                signin = false;
                System.exit(0);
            }
        } while (signin != true);
        if (signin) {
            checkServer();
        }
    }

    class Task implements Runnable {

        private Socket socket;
        private String ip;

        public Task(Socket s, String ip) {
            this.socket = s;
            this.ip = ip;
        }

        @Override
        public void run() {
            Random r = new Random();
            int counter = 0;
            int score = 0;
            Set checkQ = new HashSet();
            try {
                DataInputStream fromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());

                InetAddress inetAdd = InetAddress.getLocalHost();
                textArea.append("Client from " + inetAdd.getHostAddress() + "\n");

                // Write client to text field if needed
//                textArea.append(fromClient.readUTF());

                while (counter < quest.size()) {
                    int questNum = r.nextInt(quest.size());
                    while (checkQ.contains(questNum)) {
                        questNum = r.nextInt(quest.size());
                    }
                    checkQ.add(questNum);

                    //Write question to Client
                    toClient.writeUTF("Question " + (questNum + 1) + "\t" + quest.get(questNum) + "\n\n");
                    toClient.flush();

                    // Get answer from Client

                    String ans0 = fromClient.readUTF();
                    textArea.append("Answer " + (questNum + 1) + " from Client" + "\t" + ans0 + "\n\n");

                    // Increase the counter 
                    counter++;
                    //  Send the result and confirm

                    if (!ans0.equalsIgnoreCase(ans.get(questNum))) {
                        if (counter == quest.size()) {
                            toClient.writeUTF("\tThe correct answer is " + ans.get(questNum) + "\n\n"
                                    + "\tYour score is " + score + "\n\n"
                                    + "\tFinish \n\n");
                            toClient.flush();
                        } else {
                            toClient.writeUTF("\tThe correct answer is " + ans.get(questNum) + "\n\n"
                                    + "\tYour score is " + score + "\n\n"
                                    + "\tDo you want to continue(y/n) \n\n");
                            toClient.flush();
                        }


                    } else {
                        if (counter == quest.size()) {
                            toClient.writeUTF("\tCongratulation ! "
                                    + "Your score is " + (++score) + "\n\n"
                                    + "\tFinish \n\n");
                            toClient.flush();
                        } else {
                            toClient.writeUTF("\tCongratulation ! "
                                    + "Your score is " + (++score) + "\n\n"
                                    + "\tDo you want to continue(y/n) \n\n");
                            toClient.flush();
                        }

                    }

                    //  Get confirm from Client

                    // Check the number of question

                    String conf = fromClient.readUTF();
                    textArea.append("Confirm from Client \t" + conf + "\n\n");
                    if (counter == quest.size() || conf.equalsIgnoreCase("n")) {
                        System.out.println("Remove " + ip + " from Running Client Set");
                        if (runningClient.contains(ip)) {
                            runningClient.remove(ip);
                        }
                        checkQ.clear();
                        toClient.writeUTF("\tYour score is " + score + "\n\n" + "\tFinish \n\n");
                        textArea.append("Your score is " + score + "\n\n" + "Finish " + "\n\n");
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                if (runningClient.contains(ip)) {
                    runningClient.remove(ip);
                }
                System.out.println("Remove " + ip + " from Running Client Set in catch");
            }
        }
    }

    public static ArrayList<String> getQuest() {
        return quest;
    }

    public void checkServer() {
        try {
            Scanner readFromFile = new Scanner(new File("src\\Server\\QnA.txt"));
            int countLine = 0;
            while (readFromFile.hasNextLine()) {
                if (countLine % 2 == 0) {
                    quest.add(readFromFile.nextLine());
                } else {
                    ans.add(readFromFile.nextLine());
                }
                countLine++;
            }
            readFromFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find the file");
        }
//        for (int i = 0; i < quest.size(); i++) {
//            System.out.println(quest.get(i));
//            System.out.println(ans.get(i));
//            System.out.println(multi.get(i));
//        }
//
        
        textArea.setEditable(false);
        textArea.setForeground(Color.red);

        panel.add(scrollPane);
        panel.add(textArea);

        add(panel);
        setVisible(true);
        setTitle("Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(500, 500);
        pack();
        setResizable(false);

        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            textArea.append("Server is open at " + new Date() + "\n");

            while (true) {
                Socket socket = serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();


                if (runningClient.contains(ip)) {
                    socket.close();
                    System.out.println("Cannot connect");
                } else {
                    runningClient.add(ip);
                    Task task = new Task(socket, ip);
                    Thread t = new Thread(task);
                    t.start();
                }
            }
        } catch (IOException ex) {
            System.out.println("Reach catch in server");
        }
    }
    public static void main(String[] args) {
        new Server();
    }
}
