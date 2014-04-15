/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author Le Chinh Nhan & Nguyen Quoc Trong Nghia
 */
public class MultiClientServer extends JFrame {
    //Panel

    private JPanel panel = new JPanel();
    //Array
    private static ArrayList<String> quest = new ArrayList<>();
    private static ArrayList<String> ans = new ArrayList<>();
    private static ArrayList<String> multi = new ArrayList<>();
    private static Set<String> runningClient = new HashSet<>();
    protected static ArrayList<String> score = new ArrayList();
    //TextArea
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane(textArea);
    private static MultiClientServer server;
    private static JFrame mainFrame = new JFrame();
    //Button
    private static JButton createServer;
    private static JButton showP;
    private static JButton addQuestion;
    private static JButton deleteQuestion;
    private static JButton clear;

    public MultiClientServer() {
        if (logIn()) {
            textArea.setEditable(false);
            textArea.setForeground(Color.red);
            add(scrollPane);

            setVisible(true);
            setTitle("Trivia Server");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setSize(500, 500);
            setResizable(false);

            openServer();
        }
    }

    public boolean logIn() {
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
                signin = false;
                break;
            }
        } while (signin != true);
        return signin;
    }

    public void openServer() {
        readFromFile();
        addQuestion.setEnabled(false);
        deleteQuestion.setEnabled(false);
        showP.setEnabled(false);
        clear.setEnabled(false);
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            textArea.append("Server2 is open at " + new Date() + "\n");
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
                toClient.writeInt(getQuest().size());

                String clientInfo = fromClient.readUTF() + ":";

                while (counter < quest.size()) {
                    int questNum = r.nextInt(quest.size());
                    while (checkQ.contains(questNum)) {
                        questNum = r.nextInt(quest.size());
                    }
                    checkQ.add(questNum);

                    //Write question to Client
                    toClient.writeUTF("Question " + (counter + 1) + "\t" + quest.get(questNum));
                    toClient.writeUTF(multi.get(questNum));
                    //Write Multiple Choice to Client

                    toClient.flush();
                    // Get answer from Client

                    String ans0 = fromClient.readUTF();
                    textArea.append("Answer " + (counter + 1) + " from Client" + "\t" + ans0 + "\n\n");

                    // Increase the counter 
                    counter++;
                    //  Validate user answer, send the result and confirm                   


                    // Wrong answer
                    if (!ans0.equalsIgnoreCase(ans.get(questNum))) {
                        // Last question
                        if (counter == quest.size()) {
                            toClient.writeUTF("\tYou are wrong! The correct answer is " + ans.get(questNum) + "\n\n"
                                    + "\tYour score is " + score + "\n\n"
                                    + "\tFinish \n\n");
                            toClient.flush();
                            checkQ.clear();
                            if (runningClient.contains(ip)) {
                                runningClient.remove(ip);
                            }
                            writeScore(clientInfo, score);
                            clientInfo = "";
                            String text = "";
                            try {
                                Scanner readFromFile = new Scanner(new File("src\\Server\\Score.txt"));

                                while (readFromFile.hasNextLine()) {
                                    text += readFromFile.nextLine() + "\n";
                                }
                                System.out.println(text);
                                toClient.writeUTF(text);
                                toClient.flush();
                                readFromFile.close();
                            } catch (FileNotFoundException e) {
                                System.out.println("Cannot read the Score.txt file");
                            }

                            socket.close();
                            break;
                        } else {
                            // Not the last question
                            toClient.writeUTF("\tYou are wrong! The correct answer is " + ans.get(questNum) + "\n\n"
                                    + "\tYour score is " + score + "\n\n"
                                    + "\tDo you want to continue(y/n) \n\n");
                            toClient.flush();
                        }


                    } // Correct Answer
                    else {
                        // Last question
                        if (counter == quest.size()) {
                            toClient.writeUTF("\tCongratulation ! "
                                    + "Your score is " + (++score) + "\n\n"
                                    + "\tFinish \n\n");
                            toClient.flush();

                            checkQ.clear();
                            if (runningClient.contains(ip)) {
                                runningClient.remove(ip);
                            }
                            writeScore(clientInfo, score);
                            clientInfo = "";
                            String text = "";
                            try {
                                Scanner readFromFile = new Scanner(new File("src\\Server\\Score.txt"));

                                while (readFromFile.hasNextLine()) {
                                    text += readFromFile.nextLine() + "\n";
                                }
                                System.out.println(text);
                                toClient.writeUTF(text);
                                toClient.flush();
                                readFromFile.close();
                            } catch (FileNotFoundException e) {
                                System.out.println("Cannot read the Score.txt file");
                            }

                            socket.close();
                            break;
                        } else {
                            // Not the last question
                            toClient.writeUTF("\tCongratulation ! "
                                    + "Your score is " + (++score) + "\n\n"
                                    + "\tDo you want to continue(y/n) \n\n");
                            toClient.flush();
                        }

                    }

                    //  Get confirm from Client                                
                    String conf = fromClient.readUTF();
                    textArea.append("Confirm from Client \t" + conf + "\n\n");
                    // If user does not want to play, close the socket
                    if (conf.equalsIgnoreCase("n") || conf.equalsIgnoreCase("no")) {
                        checkQ.clear();
                        toClient.writeUTF("\tYour score is " + score + "\n\n" + "\tFinish \n\n");
                        toClient.flush();
                        textArea.append("Your score is " + score + "\n\n" + "Finish " + "\n\n");
                        System.out.println("Remove ip " + ip);
                        writeScore(clientInfo, score);
                        clientInfo = "";
                        String text = "";
                        try {
                            Scanner readFromFile = new Scanner(new File("src\\Server\\Score.txt"));

                            while (readFromFile.hasNextLine()) {
                                text += readFromFile.nextLine() + "\n";
                            }
                            System.out.println(text);
                            toClient.writeUTF(text);
                            toClient.flush();
                            readFromFile.close();
                        } catch (FileNotFoundException e) {
                            System.out.println("Cannot read the Score.txt file");
                        }


                        if (runningClient.contains(ip)) {
                            runningClient.remove(ip);
                        }

                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                if (runningClient.contains(ip)) {
                    runningClient.remove(ip);
                }

                System.out.println("Remove ip " + ip);
            }
        }
    }

    public static ArrayList<String> getQuest() {
        return quest;
    }

    public static ArrayList<String> getMulti() {
        return multi;
    }
// Read the questions 

    public void readFromFile() {
        try {
            Scanner readFromFile = new Scanner(new File("src\\Server\\QnA2.txt"));
            while (readFromFile.hasNextLine()) {
                String text = readFromFile.nextLine();
                if (text.contains("?")) {
                    quest.add(text);
                } else if (text.contains(":")) {
                    multi.add(text);
                } else {
                    ans.add(text);
                }
            }
            readFromFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot read file");
        }
    }
// write score to text file

    public synchronized void writeScore(String info, int score) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("src\\Server\\Score.txt", true));
            pw.printf("%s%d\n", info, score);
        } catch (IOException ex) {
            System.out.println("Cannot write the file");
        } finally {
            pw.close();
        }
    }
// Read score from text file

    public synchronized static String readFile() {

        int scoreMax = 0;
        try {
            Scanner readFromFile = new Scanner(new File("src\\Server\\QnA2.txt"));
            while (readFromFile.hasNextLine()) {
                String text = readFromFile.nextLine();
                if (text.contains("?")) {
                    scoreMax++;
                }
            }
            readFromFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot read file");
        }

        Scanner reader = null;
        String info = "";

        try {
            while (scoreMax >= 0) {
                reader = new Scanner(new File("src\\Server\\Score.txt"));
                while (reader.hasNextLine()) {
                    String temp = reader.nextLine();
                    String[] arrayInfo = temp.split(":");
                    if (Integer.parseInt(arrayInfo[4]) == scoreMax) {
                        score.add(temp);
                    }
                }
                scoreMax--;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find the file");
        } finally {
            reader.close();
        }
        return info;
    }
// Handle button events

    private static class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("Create Server")) {
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        new MultiClientServer();
                    }
                });
                t.start();

            }
            if (e.getActionCommand().equalsIgnoreCase("Add new question")) {
                new WriteQuestion();
            }
            if (e.getActionCommand().equalsIgnoreCase("Delete Question")) {
                new DeleteQuestion();
            }
            if (e.getActionCommand().equalsIgnoreCase("Show Player")) {
                new ScoreBoard();

            }
            if (e.getActionCommand().equalsIgnoreCase("Clear record")) {
                try {
                    PrintWriter pw = new PrintWriter(new File("src\\Server\\Score.txt"));
                    pw.print("");
                    pw.close();
                    score.clear();
                    JOptionPane.showMessageDialog(null, "All the records have been cleared", "Clear Completed", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found exception");
                }
            }
        }
    }

    public static void main(String[] args) {

        JPanel panel = new JPanel();
        createServer = new JButton("Create Server");
        showP = new JButton("Show Player");
        addQuestion = new JButton("Add new question");
        deleteQuestion = new JButton("Delete Question");
        clear = new JButton("Clear record");

        //Action Listener
        createServer.addActionListener(new ButtonHandler());
        addQuestion.addActionListener(new ButtonHandler());
        deleteQuestion.addActionListener(new ButtonHandler());
        showP.addActionListener(new ButtonHandler());
        clear.addActionListener(new ButtonHandler());

        panel.add(createServer);
        panel.add(addQuestion);
        panel.add(deleteQuestion);
        panel.add(showP);
        panel.add(clear);

        mainFrame.setTitle("Trivia Server v1.0");
        mainFrame.add(panel, BorderLayout.SOUTH);
        mainFrame.add(new ImagePanel());
        mainFrame.setVisible(true);
        mainFrame.setSize(800, 500);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
}
