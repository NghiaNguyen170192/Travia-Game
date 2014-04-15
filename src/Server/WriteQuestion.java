/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.*;

/**
 *
 * @author Le Chinh Nhan & Nguyen Quoc Trong Nghia
 */
public class WriteQuestion extends JFrame {

    private JPanel panel = new JPanel();
    //Label
    private JLabel questLabel = new JLabel("Please enter question: ");
    private JLabel multiLabelA = new JLabel("Answer A: ");
    private JLabel multiLabelB = new JLabel("Answer B: ");
    private JLabel multiLabelC = new JLabel("Answer C: ");
    private JLabel multiLabelD = new JLabel("Answer D: ");
    private JLabel ansLabel = new JLabel("The correct answer: ");
    //Multi textfield
    private JTextField questField = new JTextField(20);
    private JTextField multiFieldA = new JTextField(20);
    private JTextField multiFieldB = new JTextField(20);
    private JTextField multiFieldC = new JTextField(20);
    private JTextField multiFieldD = new JTextField(20);
    private JTextField ansField = new JTextField(20);
    //Button
    private JButton write = new JButton("Write");
    private JButton exit = new JButton("Exit");

//    public static void main(String[] args) {
//        new WriteQuestion();
//    }
    public WriteQuestion GetWriteQuestion() {
        return this;
    }

    public WriteQuestion() {

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
                break;
            }
        } while (signin != true);
        if (signin) {
            WriteQuestion();
        }
    }

    public void writeData() {
        try {
            Scanner readFromFile = new Scanner(new File("src\\Server\\QnA2.txt"));

            String text = "";
            while (readFromFile.hasNextLine()) {
                text += readFromFile.nextLine() + "\n";
            }

            String temp = questField.getText() + "?" + "\n"
                    + multiFieldA.getText() + ":" + multiFieldB.getText() + ":"
                    + multiFieldC.getText() + ":" + multiFieldD.getText() + "\n"
                    + ansField.getText();
            text += temp;
            readFromFile.close();
            PrintWriter writer = new PrintWriter(new File("src\\Server\\QnA2.txt"));
            writer.print(text);
            System.out.println("Write complete");
            JOptionPane.showMessageDialog(null, "Adding completed", "Adding Successfully", WIDTH);
            questField.setText("");
            multiFieldA.setText("");
            multiFieldB.setText("");
            multiFieldC.setText("");
            multiFieldD.setText("");
            ansField.setText("");
            writer.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Cannot read");
        }
    }

    private class Exit extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource().equals(exit)) {
                GetWriteQuestion().dispose();

            }
        }
    }

    private class Write extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            String error = "";
            if (e.getSource().equals(write)) {
                if (questField.getText().equals("") || multiFieldA.getText().equals("") || multiFieldB.getText().equals("")
                        || multiFieldC.getText().equals("") || multiFieldD.getText().equals("") || ansField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Data must not be null", "Adding Error", JOptionPane.ERROR_MESSAGE);
                }

                //Checking correct answer must be matched with 1 answer
                if (!ansField.getText().equals(multiFieldA.getText()) && !ansField.getText().equals(multiFieldB.getText())
                        && !ansField.getText().equals(multiFieldC.getText()) && !ansField.getText().equals(multiFieldD.getText())) {
                    error += "The correct answer must be matched with one answer";
                }
                //Checking 4 answer must not be the same with the correct answer
                if (ansField.getText().equals(multiFieldA.getText()) && ansField.getText().equals(multiFieldB.getText())
                        && ansField.getText().equals(multiFieldC.getText()) && ansField.getText().equals(multiFieldD.getText())) {
                    error += "The correct answer must not  be matched with four answers";
                }
                //Checking 4 answer must be different
                if (multiFieldA.getText().equals(multiFieldB.getText()) || multiFieldA.getText().equals(multiFieldC.getText()) || multiFieldA.getText().equals(multiFieldD.getText())) {
                    error += "Answer must be different";
                }
                if (multiFieldB.getText().equals(multiFieldC.getText()) || multiFieldB.getText().equals(multiFieldD.getText())) {
                    error += "Answer must be different";
                }
                if (multiFieldC.getText().equals(multiFieldD.getText())) {
                    error += "Answer must be different";
                }
                //Showing Error
                if (!error.equals("")) {
                    if (error.contains("one")) {
                        ansField.setText("");
                    }
                    if (error.contains("four")) {
                        ansField.setText("");
                        multiFieldA.setText("");
                        multiFieldB.setText("");
                        multiFieldC.setText("");
                        multiFieldD.setText("");
                    }
                    if (error.contains("different")) {
                        multiFieldA.setText("");
                        multiFieldB.setText("");
                        multiFieldC.setText("");
                        multiFieldD.setText("");
                    }
                    JOptionPane.showMessageDialog(null, error, "Adding Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    writeData();
                }
            }

        }
    }

    public void WriteQuestion() {

        panel.setLayout(new GridLayout(7, 2, 10, 10));

        //Question
        panel.add(questLabel);
        panel.add(questField);

        //Multiple choice
        panel.add(multiLabelA);
        panel.add(multiFieldA);
        panel.add(multiLabelB);
        panel.add(multiFieldB);
        panel.add(multiLabelC);
        panel.add(multiFieldC);
        panel.add(multiLabelD);
        panel.add(multiFieldD);

        //Correct Answer
        panel.add(ansLabel);
        panel.add(ansField);

        //Button
        panel.add(write);
        panel.add(exit);

        write.addMouseListener(new Write());
        exit.addMouseListener(new Exit());

        add(panel);
        setTitle("Adding Question");
        pack();
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
