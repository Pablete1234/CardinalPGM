package in.twizmwaz.cardinal.util;

import net.minecraft.server.PacketPlayOutMount;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftFallingBlock;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttackEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MovableBlock implements Listener {

    private Location location;
    private FallingBlock fallingSand;
    private ArmorStand fallingBlock;
    private Shulker shulker;
    private ArmorStand solidBlock;

    private boolean solid;
    private List<Integer> ids;

    public MovableBlock(Block block) {
        solid = block.getType().isSolid();

        ids = solid ? new ArrayList<>(4) : new ArrayList<>(2);

        FallingBlock fallingSand = block.getWorld().spawn(block.getLocation(), FallingBlock.class);
        fallingSand.setGravity(false);
        fallingSand.setDropItem(false);
        //fallingSand.setGlowing(true);
        //block.setType(Material.AIR);
        ((CraftFallingBlock) fallingSand).getHandle().ticksLived = 1;
        ids.add(fallingSand.getEntityId());
        this.fallingSand = fallingSand;

        ArmorStand fallingBlock = block.getWorld().spawn(block.getLocation(), ArmorStand.class);
        fallingBlock.setMarker(true);
        fallingBlock.setVisible(false);
        fallingBlock.setBasePlate(false);
        fallingBlock.setAI(false);
        fallingBlock.setGravity(false);
        fallingBlock.setPassenger(fallingSand);
        ids.add(fallingBlock.getEntityId());
        this.fallingBlock = fallingBlock;

        if (solid) {
            Shulker shulker = block.getWorld().spawn(block.getLocation(), Shulker.class);
            shulker.setAI(false);
            shulker.setSilent(true);
            shulker.setInvulnerable(true);
            shulker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            ids.add(shulker.getEntityId());
            this.shulker = shulker;

            ArmorStand solidBlock = block.getWorld().spawn(block.getLocation(), ArmorStand.class);
            solidBlock.setMarker(true);
            solidBlock.setVisible(false);
            solidBlock.setBasePlate(false);
            solidBlock.setAI(false);
            solidBlock.setGravity(false);
            solidBlock.setPassenger(shulker);
            ids.add(solidBlock.getEntityId());
            this.solidBlock = solidBlock;
        }

        this.location = block.getLocation().subtract(0.5, 0, 0.5);
        move(new Vector(0, 0, 0));
    }

    public void move(Vector vec) {
        location.add(vec);
        if (solid) teleport(solidBlock, location);
        teleport(fallingBlock, location);
    }

    private void teleport(Entity entity, Location loc) {
        ((CraftEntity) entity).getHandle().setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    private void update(Player player) {
        if (solid) PacketUtils.sendPacket(player, new PacketPlayOutMount(solidBlock.getEntityId(), shulker.getEntityId()));
        PacketUtils.sendPacket(player, new PacketPlayOutMount(fallingBlock.getEntityId(), fallingSand.getEntityId()));
    }

    public void handlePlayerMove(PlayerMoveEvent event) {
        if (!event.getTo().getBlock().equals(event.getFrom().getBlock())
                && event.getTo().getBlock().getLocation().distanceSquared(location) <= 4096 && event.getFrom().getBlock().getLocation().distanceSquared(location) > 4096) {
            update(event.getPlayer());
        }
    }

    public void handlePlayerAttack(PlayerAttackEntityEvent event) {
        if (ids.contains(event.getLeftClicked().getEntityId()))
            event.setCancelled(true);
    }

    public void handleFallingSand() {
        ((CraftFallingBlock) MovableBlock.this.fallingSand).getHandle().ticksLived = 1;
    }

}
