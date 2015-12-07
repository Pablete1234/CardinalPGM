package in.twizmwaz.cardinal.module.modules.longTntRender;

import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.module.TaskedModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongTntRender implements TaskedModule {
    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    private Map<Entity,Location> oldLocation = new HashMap<>();

    @Override
    public void run() {
        List<Entity> entities = GameHandler.getGameHandler().getMatchWorld().getEntities();
        for (Entity tnt : entities){
            if (tnt instanceof TNTPrimed){
                if (!oldLocation.containsKey(tnt)){
                    oldLocation.put(tnt, tnt.getLocation().add(0, 1, 0));
                }
                Location old = oldLocation.get(tnt);
                Location actual = tnt.getLocation();
                if (old != actual){
                    for (Player player : Bukkit.getOnlinePlayers()){
                        player.sendBlockChange(old, old.getBlock().getType(), old.getBlock().getData());
                        if (tnt.getLocation().distance(player.getLocation()) >= 63.0f){
                            player.sendBlockChange(tnt.getLocation(), Material.TNT, (byte) 0);
                        }
                    }
                    oldLocation.put(tnt,actual);
                }
            }
        }
    }

    @EventHandler
    public void onTntExplode (EntityExplodeEvent event){
        Entity tnt = event.getEntity();
        if (tnt instanceof TNTPrimed){
            Block block = event.getEntity().getLocation().getBlock();
            Location old = oldLocation.get(tnt);
            for (Player player : Bukkit.getOnlinePlayers()){
                player.sendBlockChange(old, old.getBlock().getType(), old.getBlock().getData());
                player.sendBlockChange(tnt.getLocation(), block.getType(), block.getData());
            }
            oldLocation.remove(tnt);
        }
    }

}
