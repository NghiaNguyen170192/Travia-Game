/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 *
 * @author Le Chinh Nhan & Nguyen Quoc Trong Nghia
 */
public class ScoreBoard {

    public ScoreBoard() {
        JFrame f = new JFrame();
        JTextArea tf = new JTextArea();
        if (!MultiClientServer.score.isEmpty()) {
            MultiClientServer.score.clear();
        }
        MultiClientServer.readFile();

        Object[] colums = {"Rank", "Name", "Phone number", "Email", "Address", "Score"};
        Object[][] data = new Object[MultiClientServer.score.size()][colums.length];
        for (int i = 0; i < MultiClientServer.score.size(); i++) {
            String record = MultiClientServer.score.get(i);
            data[i][0] = i + 1;
            data[i][1] = (record.split(":"))[0];
            data[i][2] = (record.split(":"))[1];
            data[i][3] = (record.split(":"))[2];
            data[i][4] = (record.split(":"))[3];
            data[i][5] = (record.split(":"))[4];
        }

        JTable table = new JTable(data, colums);
        table.setFillsViewportHeight(true);
        f.setTitle("Score board");
        f.add(new JScrollPane(table));
        f.setSize(800, 500);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
