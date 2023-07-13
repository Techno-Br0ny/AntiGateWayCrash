package prevent.antigatewaycrash;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.util.EnumSet;

public final class AntiGateWayCrash extends JavaPlugin {

    public class EntityPortalListener implements Listener {

        private final EnumSet<EntityType> blockedMobs = EnumSet.of(
                EntityType.HORSE,
                EntityType.DONKEY,
                EntityType.MINECART_CHEST,
                EntityType.MINECART,
                EntityType.MINECART_COMMAND,
                EntityType.MINECART_FURNACE,
                EntityType.MINECART_HOPPER,
                EntityType.MINECART_MOB_SPAWNER,
                EntityType.MINECART_TNT
                // Add more mob types here as needed
        );

        /**
         * Handles the entity portal event.
         *
         * @param event The entity portal event.
         */
        @EventHandler
        public void onEntityPortal(EntityPortalEvent event) {
            Entity entity = event.getEntity();
            Location toLocation = event.getTo();

            // Check if the entity is a blocked mob and the destination is a portal
            if (isBlockedMob(entity) && isPortal(toLocation)) {
                event.setCancelled(true); // Cancel the portal event
            }
        }

        /**
         * Checks if the given entity is a blocked mob
         *
         * @param entity The entity to check.
         * @return true if the entity is a blocked mob, false otherwise.
         */
        private boolean isBlockedMob(Entity entity) {
            return blockedMobs.contains(entity.getType());
        }

        /**
         * Checks if the given location represents a portal.
         *
         * @param location The location to check.
         * @return true if the location represents a portal, false otherwise.
         */
        private boolean isPortal(Location location) {
            // Check if the location represents an end portal or nether portal
            return location.getBlock().getType().toString().contains("END_PORTAL")
                    || location.getBlock().getType().toString().contains("NETHER_PORTAL");
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new EntityPortalListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("entitycount")) {
            if (sender instanceof Player) {
                Player admin = (Player) sender;

                // Check if the player has the admin permission
                if (admin.hasPermission("prevent.antigatewaycrash.admin")) {
                    int chunkX = admin.getLocation().getChunk().getX();
                    int chunkZ = admin.getLocation().getChunk().getZ();
                    sendEntityCountToAdmin(admin, chunkX, chunkZ);
                    return true;
                } else {
                    // Send a message to non-admin players
                    admin.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
            }
        }
        return false;
    }

    public void sendEntityCountToAdmin(Player admin, int chunkX, int chunkZ) {
        int entityCount = 0;
        for (int x = chunkX - 2; x <= chunkX + 2; x++) {
            for (int z = chunkZ - 2; z <= chunkZ + 2; z++) {
                Chunk chunk = admin.getWorld().getChunkAt(x, z);
                for (Entity entity : chunk.getEntities()) {
                    if (entity instanceof Boat || entity instanceof Mob) {
                        entityCount++;
                    }
                }
            }
        }
        String message = "There are " + ChatColor.GREEN + entityCount + ChatColor.WHITE + " entities in the chunk area around you";

        admin.sendMessage(message);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}