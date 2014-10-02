package me.calebbfmv.nations;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Tim [calebbfmv] on 10/2/2014.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        NEconomy.getInstance().getManager().load(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        NEconomy.getInstance().getManager().save(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        String command = event.getMessage().replace("/", "");
        if(command.contains(" ")){
            command = command.split(" ")[0];
        }
        if(command.equalsIgnoreCase("balance") || command.equalsIgnoreCase("pay") || command.equalsIgnoreCase("money")){
            Bukkit.dispatchCommand(event.getPlayer(), "ce" + command);
            event.setCancelled(true);
        }
    }
}
