package me.calebbfmv.nations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Tim [calebbfmv] on 10/2/2014.
 */
public class ECommands implements CommandExecutor {

    public ECommands(){
        NEconomy plugin = NEconomy.getInstance();
        plugin.getCommand("cebalance").setExecutor(this);
        plugin.getCommand("cepay").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getLabel().equalsIgnoreCase("cepay")){
            if(args.length != 2){
                sendMessage(sender, "Incorrect arguments: " + Arrays.toString(args).replaceAll("\\[]]", ""));
                sendMessage(sender, "Correct usage: " + ChatColor.GREEN + "/pay <player> <amount>" );
                return true;
            }
            if(!(sender instanceof Player)){
                String tar = args[0];
                String amt = args[1];
                double amount;
                try {
                    amount = Double.parseDouble(amt);
                } catch (NumberFormatException e){
                    sendMessage(sender, amt + " is not a valid number! EX: (1, 1.0)");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(tar);
                if(target == null){
                    sendMessage(sender, tar + " is not online!");
                    return true;
                }
                Account tAccount = Account.get(target);
                tAccount.setBalance(tAccount.getBalance() + amount);
                sendMessage(target, "Received " + ChatColor.GREEN + amt + " coin" + (amount == 1 ? "": "s") + ChatColor.GOLD);
                return true;
            }
            String tar = args[0];
            String amt = args[1];
            double amount;
            try {
                amount = Double.parseDouble(amt);
            } catch (NumberFormatException e){
                sendMessage(sender, amt + " is not a valid number! EX: (1, 1.0)");
                return true;
            }
            Player target = Bukkit.getPlayerExact(tar);
            if(target == null){
                sendMessage(sender, tar + " is not online!");
                return true;
            }
            Player player = (Player) sender;
            Account pAccount = Account.get(player);
            Account tAccount = Account.get(target);
            double balance = pAccount.getBalance();
            if(balance < amount){
                sendMessage(sender, "You don't have enough money!");
                return true;
            }
            pAccount.setBalance(balance - amount);
            tAccount.setBalance(tAccount.getBalance() + amount);
            sendMessage(player, "Sent " + ChatColor.GREEN + amt + " coin" + (amount == 1 ? "": "s") + ChatColor.GOLD + " to " + ChatColor.LIGHT_PURPLE + tar);
            sendMessage(target, "Received " + ChatColor.GREEN + amt + " coin" + (amount == 1 ? "": "s") + ChatColor.GOLD + " from " + ChatColor.LIGHT_PURPLE + player.getName());
            return true;
        }
        if(cmd.getLabel().equalsIgnoreCase("cebalance") || cmd.getLabel().equalsIgnoreCase("cemoney")){
            if(args.length == 0){
                Player player = (Player) sender;
                Account account = Account.get(player);
                String msg = "You have " + ChatColor.GREEN + account.getBalance() + " coin" + (account.getBalance() == 1 ? "" : "s");
                sendMessage(player, msg);
                return true;
            }
            if(args.length == 1){
                String tar = args[0];
                Player target = Bukkit.getPlayerExact(tar);
                if(target == null){
                    sendMessage(sender, tar + " is not online!");
                    return true;
                }
                Account account = Account.get(target);
                String msg = tar + " has " + ChatColor.GREEN + account.getBalance() + " coin" + (account.getBalance() == 1 ? "" : "s");
                sendMessage(sender, msg);
            }
            return true;
        }
        return false;
    }

    private void sendMessage(CommandSender sender, String message){
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "cEcononmy" + ChatColor.GRAY + "] " + ChatColor.GOLD + message);
    }
}
