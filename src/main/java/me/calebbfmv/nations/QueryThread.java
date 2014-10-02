package me.calebbfmv.nations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tim [calebbfmv] on 10/2/2014.
 */
public class QueryThread extends Thread {

    public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<>();

    public QueryThread(){
        start();
        setName("Economy-SQL");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
            }
            for (String query : sql_query) {
                PreparedStatement pst = null;
                try {
                    pst = NEconomy.getPlugin(NEconomy.class).getManager().getConnection().prepareStatement(query);
                    pst.executeUpdate();
                } catch (Exception ex) {
                    System.out.println("Error with Query: " + query + "\n=========================================================\n (" + ex + ")\n=========================================================\n");
                } finally {
                    try {
                        if (pst != null) {
                            pst.close();
                        }
                    } catch (SQLException ex) {
                        System.out.println("Suppressed Error");
                    }
                }
                sql_query.remove(query);
            }
        }
    }

    public static void addQuery(String query){
        sql_query.add(query);
    }
}
