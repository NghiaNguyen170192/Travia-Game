/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

/**
 *
 * @author admin
 */
public class DeleteQuestion extends JFrame {

    private JPanel panel = new JPanel();
    private JPanel panel2 = new JPanel();
    private  ArrayList<String> quest = new ArrayList<>();
    private  ArrayList<String> ans = new ArrayList<>();
    private  ArrayList<String> multi = new ArrayList<>();
    private String[] arrayQues;
    private JLabel label = new JLabel("Choose a question: ");
    private JButton delete = new JButton("Delete");
    private JButton exit = new JButton("Exit");
    private String selectedQuestion = "";
    private JComboBox box = new JComboBox();

    public DeleteQuestion getDeleteQuestion() {
        return this;
    }

    public DeleteQuestion() {

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
            DeleteFrame();
        }
    }

    public synchronized void ReadFile() {
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
            arrayQues = new String[quest.size()];

            for (int i = 0; i < quest.size(); i++) {
                arrayQues[i] = quest.get(i);
            }

            readFromFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot read file");
        }
    }

    public synchronized void DeleteFrame() {
        ReadFile();
        setLayout(new BorderLayout(10, 10));
        box = new JComboBox(arrayQues);
        box.setEditable(false);

        panel.add(label);
        panel.add(box);
        panel2.setLayout(new GridLayout(1, 2, 5, 5));
        panel2.add(delete);
        panel2.add(exit);

        delete.addActionListener(new DeleteQuestion.TakeData2());
        exit.addMouseListener(new DeleteQuestion.TakeData());

        add(panel);
        add(panel2, BorderLayout.SOUTH);
        setSize(630, 130);
        setVisible(true);
        setTitle("Delete");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private class TakeData extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getSource().equals(exit)) {
                getDeleteQuestion().dispose();
            }

        }
    }

    private class TakeData2 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(delete)) {

                selectedQuestion = (String) box.getSelectedItem();
                try {
                    PrintWriter writer = new PrintWriter(new File("src\\Server\\QnA2.txt"));
                    
                    for (int i = 0; i < quest.size(); i++) {
                        if (quest.get(i).equalsIgnoreCase(selectedQuestion)) {
                            System.out.println("Removing question: " + quest.get(i));
                            quest.remove(i);
                            ans.remove(i);
                            multi.remove(i);
                            break;
                        }
                    }

                    box.removeItem(selectedQuestion);

                    for (int j = 0; j < quest.size(); j++) {
                        writer.print(quest.get(j) + "\n");
                        writer.print(multi.get(j) + "\n");
                        writer.print(ans.get(j) + "\n");
                        arrayQues[j] = quest.get(j);
                    }

                    writer.close();
                } catch (FileNotFoundException ex) {
                    System.out.println("Cannot Open QnA2.txt");
                }
                System.out.println("Delete completed");
            }
        }
    }

//    public static void main(String[] args) {
//        new DeleteQuestion();
//    }
}
