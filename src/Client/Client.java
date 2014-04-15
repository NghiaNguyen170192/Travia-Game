package Client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.*;
import javax.swing.text.Document;

public class Client extends JFrame {// 

    static AudioClip ac;
    String[] array;
    //
    private JButton yes = new JButton("Yes");
    private JButton no = new JButton("No");
    private JButton send = new JButton("Send");
    private JButton ansA = new JButton("Answer A");
    private JButton ansB = new JButton("Answer B");
    private JButton ansC = new JButton("Answer C");
    private JButton ansD = new JButton("Answer D");
    private static JButton instruction = new JButton("Instruction");
    private static JButton start = new JButton("Start");
    private static JButton exit = new JButton("Exit");
    private JButton[] button = new JButton[20];
    //
    private JPanel panel = new JPanel();
    private JPanel westPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    //
    private JTextArea textArea = new JTextArea();
    private JTextField textField = new JTextField();
    private JScrollPane scrollPane = new JScrollPane(textArea);
    //
    private static String str;
    private String serverIp = "localhost";
    private String result = null;
    private String name, phone, email, address;
    //
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    //
    private Timer time;
    //
    public static int numQuest;
    //
    private static JFrame mainFrame = new JFrame();
    private static Object scoreObject = null;

    public Client() {

        // Log in
        boolean okie = true;
        JTextField nameField = new JTextField(10);
        JTextField phoneField = new JTextField(10);
        JTextField emailField = new JTextField(10);
        JTextField addressField = new JTextField(10);
        Object[] msg = {"Enter your name \n", nameField, "\nEnter your phone number", phoneField, "\nEnter your email address", emailField, "\nEnter your address", addressField};
        do {
            JOptionPane op = new JOptionPane(msg);
            JDialog dia = op.createDialog("Enter your personal information");
            dia.setVisible(true);
            if (op.getValue() != null) {
                okie = validateClientInformation(nameField, phoneField, emailField, addressField);
            } else {
                System.exit(0);
            }

        } while (okie != true);
        name = nameField.getText();
        phone = phoneField.getText();
        email = emailField.getText();
        address = addressField.getText();

        // Valid user is allowed to open the client
        if (okie) {
            openClient(name, phone, email, address);
        }
    }
// Showing the time meter

    class TimeHandler implements ActionListener {

        int count = 20;

        @Override
        public void actionPerformed(ActionEvent e) {
            count--;
            if (count >= 0) {
                if (count == 10) {
                    for (int i = 0; i < 10; i++) {
                        button[i].setBackground(Color.RED);
                        button[i].setBorderPainted(false);
                    }
                    button[count].setBackground(null);
                    //The sound
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    button[count].setBackground(null);
                    Toolkit.getDefaultToolkit().beep();
                }

            } else {
                new Handler().actionPerformed(e);

            }
        }
    }
// Sending answer after 20 seconds

    class Handler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == textField) {
                    toServer.writeUTF(str);
                    toServer.flush();
                } else if (e.getSource() == time) {
                    toServer.writeUTF(" ");
                    toServer.flush();
                }
            } catch (IOException ex) {
            }
        }
    }
// Set answer to textfield

    private class TakeData extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource().equals(ansA)) {
                str = array[0];
                textField.setText("Your answer: " + str);
                ansA.setBackground(Color.RED);
                yes.setBackground(null);
                no.setBackground(null);
            } else {
                ansA.setBackground(null);
            }

            if (e.getSource().equals(ansB)) {
                str = array[1];
                textField.setText("Your answer: " + str);
                ansB.setBackground(Color.RED);
                yes.setBackground(null);
                no.setBackground(null);
            } else {
                ansB.setBackground(null);
            }

            if (e.getSource().equals(ansC)) {
                str = array[2];
                textField.setText("Your answer: " + str);
                ansC.setBackground(Color.RED);
                yes.setBackground(null);
                no.setBackground(null);

            } else {
                ansC.setBackground(null);
            }

            if (e.getSource().equals(ansD)) {
                str = array[3];
                textField.setText("Your answer: " + str);
                ansD.setBackground(Color.RED);
                yes.setBackground(null);
                no.setBackground(null);
            } else {
                ansD.setBackground(null);
            }
        }
    }
// Sending answers

    private class Send extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                if (e.getSource().equals(send)) {
                    if (textField.getText().contains("Your answer: ")) {
                        toServer.writeUTF(str);
                    } else {
                        toServer.writeUTF(textField.getText());
                    }
                    toServer.flush();
                    textField.setText("");
                }
            } catch (IOException ex) {
            }

        }
    }
// Set Color for buttons when choosen

    private class Confirm extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource().equals(yes)) {
                textField.setText("Yes");
                yes.setBackground(Color.red);
                no.setBackground(null);
                ansA.setBackground(null);
                ansB.setBackground(null);
                ansC.setBackground(null);
                ansD.setBackground(null);
            }
            if (e.getSource().equals(no)) {
                textField.setText("No");
                no.setBackground(Color.red);
                yes.setBackground(null);
                ansA.setBackground(null);
                ansB.setBackground(null);
                ansC.setBackground(null);
                ansD.setBackground(null);
            }
        }
    }

    //Checking Information
    public static boolean validateClientInformation(JTextField nameField, JTextField phoneField, JTextField emailField, JTextField addressField) {
        boolean okie = false;
        if (nameField.getText().equals("") || phoneField.getText().equals("")
                || emailField.getText().equals("") || addressField.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please fill all your information", "Information Error", JOptionPane.ERROR_MESSAGE);

        } else {
            String error = "";
            //Check name
            if (!nameField.getText().matches("[a-zA-Z ]+")) {
                error += "Your name is not valid.Please enter name again \n";
                okie = false;
            }
            //Check phone
            if (!phoneField.getText().matches("[0-9]+")) {
                error += "Your phone is not valid.Please enter phone again \n";
                okie = false;
            }
            //Check email
            if (!emailField.getText().matches("[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+")) {
                error += "Your email is not valid.Please enter email again \n";
                okie = false;
            }
            //Check address
            if (!addressField.getText().matches("[a-zA-Z0-9 ,./]+")) {
                error += "Your address is not valid.Please enter address again \n";
                okie = false;
            }
            if (!error.equals("")) {
                okie = false;
                JOptionPane.showMessageDialog(null, error, "Information Error", JOptionPane.ERROR_MESSAGE);
                //Set wrong input to blank
                if (error.contains("name")) {
                    nameField.setText("");
                }
                if (error.contains("phone")) {
                    phoneField.setText("");
                }
                if (error.contains("email")) {
                    emailField.setText("");
                }
                if (error.contains("address")) {
                    addressField.setText("");
                }

            } else {
                okie = true;
            }
        }
        return okie;
    }
// Opening a client

    public void openClient(String name, String phone, String email, String address) {
        textField.setEditable(false);
        textArea.setEditable(false);
        add(scrollPane);
        southPanel.setLayout(new GridLayout(4, 2, 10, 10));
        southPanel.add(textField);

        southPanel.add(send);

        southPanel.add(yes);
        southPanel.add(no);

        southPanel.add(ansA);
        southPanel.add(ansB);
        southPanel.add(ansC);
        southPanel.add(ansD);

        ansA.addMouseListener(new TakeData());
        ansB.addMouseListener(new TakeData());
        ansC.addMouseListener(new TakeData());
        ansD.addMouseListener(new TakeData());
        send.addMouseListener(new Send());
        yes.addMouseListener(new Confirm());
        no.addMouseListener(new Confirm());

        add(southPanel, BorderLayout.SOUTH);

        westPanel.setLayout(new GridLayout(20, 1));
        for (int i = 20; i > 0; i--) {
            button[i - 1] = new JButton();
            westPanel.add(button[i - 1]);
        }
        add(westPanel, BorderLayout.WEST);

        setTitle("Client");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(860, 500);
        setResizable(false);
        // Start to process data from Server
        try {
            Socket socket = new Socket(serverIp, 9999);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());

            int counter = 0;
            numQuest = fromServer.readInt();
            // Send infomation of Client to server
            String info = name + ":" + phone + ":" + email + ":" + address;
            toServer.writeUTF(info);

            while (counter <= numQuest) {

                Document d = textArea.getDocument();
                String question = fromServer.readUTF() + "\n";
                String dataFromServer = fromServer.readUTF();

                if (!question.contains("?") || !dataFromServer.contains(":")) {
                    new ScoreBoard(dataFromServer);
                } else {

                    array = dataFromServer.split("\\:");
                    Collections.shuffle(Arrays.asList(array));
                    //Set answer to button                 
                    ansA.setText(array[0]);
                    ansB.setText(array[1]);
                    ansC.setText(array[2]);
                    ansD.setText(array[3]);

                    textArea.append(question);
                    yes.setVisible(false);
                    no.setVisible(false);

                    yes.setBackground(null);
                    no.setBackground(null);

                    ansA.setVisible(true);
                    ansB.setVisible(true);
                    ansC.setVisible(true);
                    ansD.setVisible(true);

                    //Auto Scroll down

                    textArea.select(d.getLength(), d.getLength());

                    //Timer display
                    TimeHandler timeHandler = new TimeHandler();
                    time = new Timer(1000, timeHandler);
                    for (int i = 0; i < 20; i++) {
                        button[i].setBackground(Color.GREEN);
                        button[i].setEnabled(false);
                        button[i].setBorderPainted(false);
                    }

                    time.start();

                    //  Answer the question


                    //  Read the result and confirm
                    result = fromServer.readUTF();
                    textArea.append(result);

                    time.stop();
                    yes.setVisible(true);
                    no.setVisible(true);
                    ansA.setVisible(false);
                    ansB.setVisible(false);
                    ansC.setVisible(false);
                    ansD.setVisible(false);

                    ansA.setBackground(null);
                    ansB.setBackground(null);
                    ansC.setBackground(null);
                    ansD.setBackground(null);


                    //Auto scroll down
                    textArea.select(d.getLength(), d.getLength());
                }
                //  Send Confirm
                counter++;

                if (counter == numQuest) {
                    String s = fromServer.readUTF();
                    new ScoreBoard(s);
                }
            }
        } catch (IOException ex) {
// Show final result in textfield
            String[] a = result.split("\n\n");
            for (int i = 0; i < a.length; i++) {
                if (a[i].contains("Your score")) {
                    textField.setForeground(Color.red);
                    textField.setText(a[i]);
                    try {
                        String text = fromServer.readUTF();
                        System.out.println(text);
                    } catch (IOException ex1) {
                    }

                    break;
                }
            }
            // reset the number of question
            numQuest = 0;
            // Playing music
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    URL url = this.getClass().getResource("swintro.mid");
                    ac = Applet.newAudioClip(url);
                    ac.play();
                }
            });
            t.start();
            System.out.println("Client is disconnected");
        }
    }
// Handle buttons events

    private static class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("Instruction")) {
                JFrame f = new JFrame();
                f.setVisible(true);
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.pack();
                JTextArea instr = new JTextArea(5, 10);
                instr.append("Welcome to Trivia Game\n"
                        + "There is a number of questions that you have to answer \n"
                        + "For each question, you have 20 seconds to answer, the answer"
                        + " which is sent after 20 seconds will be considered as an incorrect answer \n");

                f.add(instr);
                f.pack();
                f.setTitle("Trivia Instruction");
            }
            if (e.getActionCommand().equalsIgnoreCase("Start")) {
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        new Client();
                    }
                });
                t.start();
                mainFrame.dispose();
            }
            if (e.getActionCommand().equalsIgnoreCase("Exit")) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        mainFrame.add(new ImagePanel());
        mainFrame.setVisible(true);
        mainFrame.setSize(800, 500);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("Trivia Game v1.0");

        instruction.addActionListener(new ButtonHandler());
        start.addActionListener(new ButtonHandler());
        exit.addActionListener(new ButtonHandler());
        JPanel pnel = new JPanel();

        pnel.add(instruction);
        pnel.add(start);
        pnel.add(exit);

        mainFrame.add(pnel, BorderLayout.SOUTH);
    }
}
