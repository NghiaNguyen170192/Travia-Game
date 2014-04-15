/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Le Chinh Nhan & Nguyen Quoc Trong Nghia
 */
public class ScoreBoard {

    public ScoreBoard(String scoreData) {
        // Storing client informations and scores
        ArrayList<String> score = new ArrayList();
        // Sorting the players base on their scores
        ArrayList<String> sortedScore = new ArrayList();
        
        String[] arrayRow = scoreData.split("\n");
        // Split the data into records
        for (int i = 0; i < arrayRow.length; i++) {
            score.add(arrayRow[i]);
        }
        
        // Sorting records and add them into arraylist
        int scoreMax = Client.numQuest;
        while (scoreMax >= 0) {
            for (int i = 0; i < score.size(); i++) {
                String data = score.get(i);
                String[] array = data.split(":");
                if (Integer.parseInt(array[4]) == scoreMax) {
                    sortedScore.add(data);
                }
            }
            scoreMax--;
        }

        // Showing record table
        Object[] colums = {"Rank", "Name", "Phone number", "Email", "Address", "Score"};
        Object[][] data = new Object[sortedScore.size()][colums.length];
        for (int i = 0; i < sortedScore.size(); i++) {
            String record = sortedScore.get(i);
            data[i][0] = i + 1;
            data[i][1] = (record.split(":"))[0];
            data[i][2] = (record.split(":"))[1];
            data[i][3] = (record.split(":"))[2];
            data[i][4] = (record.split(":"))[3];
            data[i][5] = (record.split(":"))[4];
        }
        JFrame f = new JFrame();
        JTable table = new JTable(data, colums);
        table.setFillsViewportHeight(true);
        f.setTitle("Score board");
        f.add(new JScrollPane(table));
        f.setSize(800, 500);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
}
