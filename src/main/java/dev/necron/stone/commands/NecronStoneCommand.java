package dev.necron.stone.commands;

import com.hakan.core.command.HCommandAdapter;
import com.hakan.core.command.executors.base.BaseCommand;
import com.hakan.core.command.executors.sub.SubCommand;
import com.hakan.core.utils.yaml.HYaml;
import dev.necron.stone.NecronStoneHandler;
import dev.necron.stone.NecronStonePlugin;
import dev.necron.stone.configuration.NecronStoneConfiguration;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.hologram.NecronStoneHologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

@BaseCommand(
        name = "necronstone",
        description = "NecronStone command",
        usage = "/necronstone"
)
public class NecronStoneCommand implements HCommandAdapter {

    @SubCommand(
            args = {"create"},
            permission = "necronstone.create",
            permissionMessage = "§cYou don't have permission to use this command"
    )
    public void createCommand(Player player, String[] args) {
        Block targetBlock = player.getTargetBlock((Set<Material>) null, 5);
        if (targetBlock == null) {
            player.sendMessage("§cYou must look at a block!");
            return;
        } else if (targetBlock.getType() == Material.AIR) {
            player.sendMessage("§cYou must look at a block!");
            return;
        }

        NecronStoneHandler.create(targetBlock.getLocation());
        player.sendMessage("§aNecronStone created!");
    }

    @SubCommand(
            args = {"list"},
            permission = "necronstone.list",
            permissionMessage = "§cYou don't have permission to use this command"
    )
    public void listCommand(Player player, String[] args) {

    }

    @SubCommand(
            args = {"reload"},
            permission = "necronstone.reload",
            permissionMessage = "§cYou don't have permission to use this command"
    )
    public void reloadCommand(Player player, String[] args) {
        player.sendMessage("§aNecronStone reloading...");

        NecronStoneConfiguration.initialize(NecronStonePlugin.getInstance());
        NecronStoneConfigContainer.reload();
        NecronStoneHandler.getValues().forEach(stone -> {
            stone.changeHologram(NecronStoneHologram.register(stone));

            HYaml dataFile = stone.getDataFile();
            dataFile.reload();
            stone.setRewards(dataFile.getStringList("rewards"));
            stone.setMaxHealth(dataFile.getInt("maxHealth"));
            stone.setHealth(dataFile.getInt("health"));
            stone.changeLocation((Location) dataFile.get("location"));
            stone.getHologram().update();
        });

        player.sendMessage("§aNecronStone reloaded!");
    }
}