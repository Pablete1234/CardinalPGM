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

    Payload(RegionModule payload, PointRegion start) {
        this.moveable = new MovableStructure(payload);
        Block block = GameHandler.getGameHandler().getMatchWorld().getBlockAt(start.getLocation());
        if (block.getType().equals(Material.RAILS)) {
            this.location = start.getLocation();
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Payload won't work, as the first block is not a rail!");
        }
    }

    public void unload() {
        moveable.destroy();
        HandlerList.unregisterAll(this);
    }

    public void run() {
        Block block = GameHandler.getGameHandler().getMatchWorld().getBlockAt(0, 1, 0);

        if (block.getType() == Material.STONE) {
            moveable.move(new Vector(0, 0, 0.1));
        } else if (block.getType() == Material.DIRT) {
            moveable.move(new Vector(0, 0, -0.1));
        }
    }

}
