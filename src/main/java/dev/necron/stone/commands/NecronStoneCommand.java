package dev.necron.stone.commands;

import com.hakan.core.command.HCommandAdapter;
import com.hakan.core.command.executors.base.BaseCommand;
import com.hakan.core.command.executors.sub.SubCommand;
import dev.necron.stone.NecronStoneHandler;
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
        Block targetBlock = player.getTargetBlock((Set<Material>) null, 4);
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
}