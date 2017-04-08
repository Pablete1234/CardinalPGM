package in.twizmwaz.cardinal.module.modules.payload;

import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.module.TaskedModule;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.type.PointRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class Payload implements TaskedModule {

    private Moveable moveable;
    private Location location;
    private long nextUpdate;
    private int rate;
    private double distance;

    Payload(RegionModule payload, PointRegion start, int rate, double distance) {
        this.moveable = new MovableStructure(payload);
        Block block = GameHandler.getGameHandler().getMatchWorld().getBlockAt(start.getLocation());
        if (block.getType().equals(Material.RAILS)) {
            this.location = start.getLocation();
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Payload won't work, as the first block is not a rail!");
        }
        this.rate = rate;
        this.distance = distance;
    }

    public void unload() {
        moveable.destroy();
        HandlerList.unregisterAll(this);
    }

    public void run() {
        if (nextUpdate <= System.currentTimeMillis()) {
            nextUpdate = System.currentTimeMillis() + rate;

            Block block = GameHandler.getGameHandler().getMatchWorld().getBlockAt(0, 1, 0);

            if (block.getType() == Material.STONE) {
                moveable.move(new Vector(0, 0, distance));
            } else if (block.getType() == Material.DIRT) {
                moveable.move(new Vector(0, 0, distance * -1));
            }
        }
    }

}
