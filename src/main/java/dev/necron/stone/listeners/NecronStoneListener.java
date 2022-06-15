package dev.necron.stone.listeners;

import com.hakan.core.HCore;
import com.hakan.core.particle.HParticle;
import dev.necron.stone.NecronStoneHandler;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class NecronStoneListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        NecronStoneHandler.findByLocation(event.getBlock().getLocation()).ifPresent(stone -> {
            event.setCancelled(true);

            Player player = event.getPlayer();
            if (!stone.isAlive())
                return;

            if (stone.damage(player, 1)) {
                Bukkit.broadcastMessage(NecronStoneConfigContainer.MESSAGE_INFO_DESTROY_STONE_BROADCAST.asString()
                        .replace("%player%", player.getName())
                        .replace("%location%", LocationUtil.serialize(stone.getLocation()).replace(":", ", ")));

                List<Player> show = new ArrayList<>(event.getBlock().getLocation().getWorld().getPlayers());
                HParticle particle1 = new HParticle("FLAME", 100, 0.1, new Vector(0.1, 0.1, 0.1));
                HParticle particle2 = new HParticle("CLOUD", 100, 0.1, new Vector(0.1, 0.1, 0.1));
                HCore.playParticle(show, event.getBlock().getLocation().add(0.5, 0.5, 0.5), particle1);
                HCore.playParticle(show, event.getBlock().getLocation().add(0.5, 0.5, 0.5), particle2);

                for (String rewardCommand : stone.getRewards()) {
                    String command = rewardCommand.replace("%player%", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        });
    }
}