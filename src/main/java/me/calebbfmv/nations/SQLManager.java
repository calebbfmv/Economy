package me.calebbfmv.nations;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

/**
 * Created by Tim [calebbfmv] on 10/2/2014.
 */
public class SQLManager {

    private Connection con;
    public static int query_count = 0;
    private String user, pass, url;

    private String accountsName = "fe_accounts";
    private String accountsColumnUser = "name";
    private String accountsColumnMoney = "money";

    public SQLManager(String host, String user, String pass, String db, int port){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String statement = "CREATE DATABASE IF NOT EXISTS " + db;
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, user, pass);
            Statement st = conn.createStatement();
            st.executeUpdate(statement);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db;
        this.user = user;
        this.pass = pass;
        con = getConnection();
        QueryThread.addQuery("CREATE TABLE IF NOT EXISTS " + accountsName + " (" + accountsColumnUser + " varchar(64) NOT NULL PRIMARY KEY, "  + accountsColumnMoney + " double NOT NULL)");

    }

    public Connection getConnection() {
        try {
            if (query_count >= 1000) {
                if(con != null){
                    con.close();
                }
                con = DriverManager.getConnection(url, user, pass);
                query_count = 0;
            }
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(url, user, pass);
            }
        } catch(Exception e) {
            e.printStackTrace();
            try{
                con = DriverManager.getConnection(url, user, pass);
            } catch(Exception err){
                err.printStackTrace();
            }
        }
        query_count++;
        return con;
    }

    public ResultSet getResultSet(String query) {
        PreparedStatement pst;
        try {
            pst = getConnection().prepareStatement(query);
            pst.execute();
            ResultSet rs = pst.getResultSet();
            return rs;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public void insert(String insert) {
        QueryThread.addQuery(insert);
    }

    public void create(Player player){
        String insert = "INSERT INTO `" + accountsName + "` VALUES('" + player.getName() + "', 10)";
        new Account(player);
        insert(insert);
    }

    public void save(Player player){
        Account account = Account.get(player);
        if(account == null){
            return;
        }
        String update = "UPDATE `" + accountsName + "` SET `" + accountsColumnMoney + "`= " + account.getBalance() + " WHERE `" + accountsColumnUser + "`='" + player.getName() + "'";
        insert(update);
    }

    public void getBalance(final Player player, final Callable<Double> call){
        Account account = Account.get(player);
        if(account != null){
            call.call(account.getBalance());
            return;
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                String select = "SELECT `money` FROM `" + accountsName + "` WHERE `" + accountsColumnUser + "`='" + player.getName() + "'";
                ResultSet res = getResultSet(select);
                try {
                    if (res.next()) {
                        call.call(res.getDouble(accountsColumnMoney));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(NEconomy.getInstance());
    }

    public void hasAccount(final Player player, final Callable<Boolean> call) {
        new BukkitRunnable(){
            @Override
            public void run() {
                String select = "SELECT * FROM `" + accountsName + "` WHERE `" + accountsColumnUser + "`='" + player.getName() + "'";
                ResultSet res = getResultSet(select);
                try {
                    call.call(res.next());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(NEconomy.getInstance());
    }

    public void load(final Player player) {
        Callable<Boolean> c = new Callable<Boolean>() {
            public void call(Boolean res) {
                if (!res) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            create(player);
                        }
                    }.runTaskLaterAsynchronously(NEconomy.getInstance(), 5L);
                }
            }
        };
        Callable<Double> balCall = new Callable<Double>() {
            public void call(Double bal) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new Account(player, bal);
                    }
                }.runTaskLaterAsynchronously(NEconomy.getInstance(), 5L);
            }
        };
        getBalance(player, balCall);
        hasAccount(player, c);
    }

    public abstract class Callable<T> {
        public abstract void call(T t);
    }
}