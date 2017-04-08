package in.twizmwaz.cardinal.module.modules.payload;

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.util.MovableBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttackEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class MovableStructure implements Moveable, Listener {

    private List<MovableBlock> moveableBlocks;
    private Vector min;
    private Vector max;

    private int task;

    MovableStructure(RegionModule payload) {
        min = payload.getMin();
        max = payload.getMax();
        this.moveableBlocks = Lists.newArrayList();

        for (Block block : payload.getBlocks()) {
            if (block.getType() == Material.AIR) continue;
            moveableBlocks.add(new MovableBlock(block));
        }
        for (Block block : payload.getBlocks()) {
            block.setType(Material.AIR);
        }
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Cardinal.getInstance(),
                () -> moveableBlocks.forEach(MovableBlock::handleFallingSand), 500L, 500L);
        Bukkit.getPluginManager().registerEvents(this, Cardinal.getInstance());
    }

    public void destroy() {
        Bukkit.getScheduler().cancelTask(task);
        HandlerList.unregisterAll(this);
    }

    public void move(Vector vec) {
        for (MovableBlock moveableBlock : moveableBlocks) {
            moveableBlock.move(vec);
        }
        /*for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().toVector().isInAABB(min, max)) {
                player.teleportRelative(vec, 0, 0);
            }
        }*/
        min.add(vec);
        max.add(vec);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        for (MovableBlock moveableBlock : moveableBlocks) {
            moveableBlock.handlePlayerMove(event);
        }
    }

    @EventHandler
    public void onPlayerAttack(PlayerAttackEntityEvent event) {
        for (MovableBlock moveableBlock : moveableBlocks) {
            moveableBlock.handlePlayerAttack(event);
        }
    }

}
