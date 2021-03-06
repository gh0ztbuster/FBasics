package org.originmc.fbasics.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.originmc.fbasics.FBasics;
import org.originmc.fbasics.CommandEditor;
import org.originmc.fbasics.FBPlayer;

public class SessionListener implements Listener {

    public SessionListener(FBasics plugin) {
        // Register Session events to the server
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void loadPlayerData(PlayerJoinEvent event) {
        // Do nothing if player data is loaded
        Player player = event.getPlayer();
        if (FBPlayer.get(player.getUniqueId()) != null) return;

        // Create a new profile for this player
        new FBPlayer(player.getUniqueId().toString() + "," + player.getName() + ",0");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void savePlayerData(PlayerQuitEvent event) {
        // Do nothing if player has no data
        FBPlayer fbplayer = FBPlayer.get(event.getPlayer().getUniqueId());
        if (fbplayer == null) return;

        // Iterate through all player command cooldowns
        for (CommandEditor commandEditor : fbplayer.getCooldowns().keySet()) {
            // Remove all expired command cooldowns
            int difference = (int) (System.currentTimeMillis() - fbplayer.getCooldown(commandEditor)) / 1000;
            if (difference > commandEditor.getCooldown()) {
                fbplayer.removeCooldown(commandEditor);
            }
        }

        // Remove player data if player has no command cooldowns nor crates
        if (fbplayer.getCooldowns().isEmpty() && fbplayer.getCrates() == 0) {
            fbplayer.remove();
        }
    }

}