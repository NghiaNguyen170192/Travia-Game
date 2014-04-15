/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import Testing.Server;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
import javax.swing.text.Document;

/**
 *
 * @author CHINHNHAN
 */
public class Client extends JFrame {

    private JPanel westPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    private JTextField textField = new JTextField(20);
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane(textArea);
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private JLabel timeLabel = new JLabel("Time left : 20");
    private JButton[] button = new JButton[20];
    private Timer time;

    public Client() {
        boolean okie = true;
//        JTextField nameField = new JTextField(10);
//        JTextField phoneField = new JTextField(10);
//        JTextField emailField = new JTextField(10);
//        JTextField addressField = new JTextField(10);
//        Object[] msg = {"Enter your name \n", nameField, "\nEnter your phone number", phoneField, "\nEnter your email address", emailField, "\nEnter your address", addressField};
//        do {
//            JOptionPane op = new JOptionPane(msg);
//            JDialog dia = op.createDialog("Enter your personal information");
//            dia.setVisible(true);
//            if (op.getValue() != null) {
//                okie = validateClientInformation(nameField, phoneField, emailField, addressField);
//            } else {
//                System.exit(0);
//            }
//
//        } while (okie != true);
        if (okie) {
            JOptionPane.showMessageDialog(null, "Here are some information about the game\n"
                    + "The game has " + Server.getQuest().size() + " questions\n"
                    + ".......");
            checkClient();
        }
    }

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
                    timeLabel.setText("Time left: " + count);
                    button[count].setBackground(null);
                    //The sound
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    timeLabel.setText("Time left: " + count);
                    button[count].setBackground(null);
                    Toolkit.getDefaultToolkit().beep();
                }

            } else {
                new Handler().actionPerformed(e);

            }
        }
    }

    class Handler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == textField) {
                    toServer.writeUTF(textField.getText());
                    toServer.flush();
                    textField.setText("");
                } else if (e.getSource() == time) {
                    toServer.writeUTF(" ");
                    toServer.flush();
                    textField.setText("");
                }
            } catch (IOException ex) {
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

    public void checkClient() {

        add(scrollPane);
        southPanel.add(new JLabel("Answer : "));
        southPanel.add(textField);
        add(southPanel, BorderLayout.SOUTH);

        westPanel.setLayout(new GridLayout(20, 1));
        for (int i = 20; i > 0; i--) {
            button[i - 1] = new JButton();
//            button[i - 1].setBackground(Color.GREEN);
            westPanel.add(button[i - 1]);
        }
        add(westPanel, BorderLayout.WEST);

        setTitle("Client");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 500);
        setResizable(false);
        try {
            // Nghia
//            Socket socket = new Socket("172.16.130.100", 9999);
            Socket socket = new Socket("localhost", 9999);
            System.out.println("connect to server");
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());


            int counter = 0;
            textField.addActionListener(new Handler());

            // send information to server if needed
//            toServer.writeUTF("Hey Im connected\n");

            while (true) {
                //  Read question from Server
                textArea.append(fromServer.readUTF());

                //Auto Scroll down
                Document d = textArea.getDocument();
                textArea.select(d.getLength(), d.getLength());

                //Timer display
                TimeHandler timeHandler = new TimeHandler();
                time = new Timer(1000, timeHandler);
                for (int i = 0; i < 20; i++) {
                    button[i].setBackground(Color.GREEN);
                }

                time.start();

                //  Answer the question


                //  Read the result and confirm
                textArea.append(fromServer.readUTF());
                //Auto scroll down
                textArea.select(d.getLength(), d.getLength());
                time.stop();

                //  Send Confirm
                counter++;
                if (counter == Server.getQuest().size() || textField.getText().equalsIgnoreCase("") || textField.getText().equalsIgnoreCase("no")) {
                    System.out.println("enter the if");
                    textArea.append(fromServer.readUTF());
                    System.out.println(fromServer.readUTF());
                    textArea.select(d.getLength(), d.getLength());
                    socket.close();
                    break;
                }
            }

        } catch (IOException ex) {
            if (time != null) {
                time.stop();
            }
            System.out.println("Client is disconected");
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
