package in.twizmwaz.cardinal;

import in.twizmwaz.cardinal.cycle.Cycle;
import in.twizmwaz.cardinal.event.CycleCompleteEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.ModuleFactory;
import in.twizmwaz.cardinal.rotation.Rotation;
import in.twizmwaz.cardinal.rotation.exception.RotationLoadException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class GameHandler implements Listener {

    private static GameHandler handler;
    private final ModuleFactory moduleFactory;
    private Rotation rotation;
    private WeakReference<World> matchWorld;
    private Match match;
    private Cycle cycle;
    private File matchFile;
    private boolean globalMute;

    public GameHandler() throws RotationLoadException {
        handler = this;
        this.moduleFactory = new ModuleFactory();
        rotation = new Rotation();
        cycle = new Cycle(rotation.getNext(), UUID.randomUUID(), this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Cardinal.getInstance(), new Runnable() {
            @Override
            public void run() {
                cycleAndMakeMatch();
            }
        });

    }

    public static GameHandler getGameHandler() {
        return handler;
    }

    public void cycleAndMakeMatch() {
        if (rotation.getNext().equals(cycle.getMap())) {
            rotation.move();
        }
        World oldMatchWorld = matchWorld == null ? null : matchWorld.get();
        Long time = System.currentTimeMillis();
        Long timeTotal = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage("Start cycle");
        cycle.run();
        Bukkit.getConsoleSender().sendMessage("Ended cycle in:" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        if (match != null) match.unregisterModules();
        Bukkit.getConsoleSender().sendMessage("Unregistered old modules:" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        this.match = new Match(cycle.getUuid(), cycle.getMap());
        Bukkit.getConsoleSender().sendMessage("New match in:" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        this.match.registerModules();
        Cardinal.getInstance().getLogger().info(this.match.getModules().size() + " modules loaded in:" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        Bukkit.getServer().getPluginManager().callEvent(new CycleCompleteEvent(match));
        Bukkit.getConsoleSender().sendMessage("Handle Cycle event:" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        cycle = new Cycle(rotation.getNext(), UUID.randomUUID(), this);
        Bukkit.getConsoleSender().sendMessage("new Cycle:" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        Bukkit.unloadWorld(oldMatchWorld, true);
        Bukkit.getConsoleSender().sendMessage("Unload World:" + (System.currentTimeMillis() - time));
        Bukkit.getConsoleSender().sendMessage("Total World Loading Time:" + (System.currentTimeMillis() - timeTotal));
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        event.getWorld().setKeepSpawnInMemory(false);
    }

    public Rotation getRotation() {
        return rotation;
    }

    public World getMatchWorld() {
        return matchWorld.get();
    }

    public void setMatchWorld(World world) {
        matchWorld = new WeakReference<>(world);
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Cycle getCycle() {
        return cycle;
    }

    public JavaPlugin getPlugin() {
        return Cardinal.getInstance();
    }

    public ModuleFactory getModuleFactory() {
        return moduleFactory;
    }

    public File getMatchFile() {
        return matchFile;
    }

    public void setMatchFile(File file) {
        matchFile = file;
    }

    public boolean getGlobalMute() {
        return globalMute;
    }

    public void setGlobalMute(boolean globalMute) {
        this.globalMute = globalMute;
    }

    public boolean toggleGlobalMute() {
        globalMute = !globalMute;
        return globalMute;
    }
}
