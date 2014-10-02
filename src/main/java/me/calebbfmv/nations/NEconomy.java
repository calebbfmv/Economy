package me.calebbfmv.nations;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tim [calebbfmv] on 10/2/2014.
 */
public class NEconomy extends JavaPlugin {

    private static NEconomy instance;
    private SQLManager manager;

    @Override
    public void onEnable(){
        instance = this;
        saveDefaultConfig();
        new ECommands();
        getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, new Economy(), this, ServicePriority.Highest);
        connect();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        for(Player player : Bukkit.getOnlinePlayers()){
            getManager().load(player);
        }
    }

    @Override
    public void onDisable(){
        for(Player player : Bukkit.getOnlinePlayers()){
            getManager().save(player);
        }
    }

    private void connect(){
        String host = getConfig().getString("host", "localhost");
        String user = getConfig().getString("user", "root");
        String password = getConfig().getString("password", "pippintook");
        int port = 3306;
        new QueryThread();
        manager = new SQLManager(host, user, password, port);
    }

    public static NEconomy getInstance(){
        return instance;
    }

    public SQLManager getManager(){
        return manager;
    }
}
