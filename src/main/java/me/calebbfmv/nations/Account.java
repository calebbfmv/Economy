package me.calebbfmv.nations;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tim [calebbfmv] on 10/2/2014.
 */
public class Account {

    private String name;
    private UUID uuid;
    private double balance;

    private static HashMap<UUID, Account> accounts = new HashMap<>();

    public Account(Player player){
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.balance = 10.0;
        accounts.put(uuid, this);
    }

    public Account(Player player, double balance){
        this(player);
        setBalance(balance);
    }

    public static Account get(Player player){
        return accounts.get(player.getUniqueId());
    }

    public static Account get(String name){
        Player player1 = Bukkit.getPlayerExact(name);
        if(player1 == null){
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            UUID o_uuid = player.getUniqueId();
            return accounts.get(o_uuid);
        }
        return get(player1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        NEconomy.getInstance().getManager().save(Bukkit.getPlayer(getUuid()));
    }

    public void remove(){
        accounts.remove(getUuid());
    }
}
