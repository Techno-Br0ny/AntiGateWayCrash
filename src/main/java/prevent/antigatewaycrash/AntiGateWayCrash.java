package prevent.antigatewaycrash;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiGateWayCrash extends JavaPlugin {

    public class EntityPortalListener implements Listener {

        private final EntityType[] blockedMobs = {
                EntityType.HORSE,
                EntityType.BOAT,
                EntityType.DONKEY,
                EntityType.MINECART_CHEST,
                EntityType.MINECART,
                EntityType.MINECART_COMMAND,
                EntityType.MINECART_FURNACE,
                EntityType.MINECART_HOPPER,
                EntityType.MINECART_MOB_SPAWNER,
                EntityType.MINECART_TNT
                // Add more mob types here as needed
        };

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
         * Checks if the given entity is a blocked mob.
         *
         * @param entity The entity to check.
         * @return true if the entity is a blocked mob, false otherwise.
         */
        private boolean isBlockedMob(Entity entity) {
            for (EntityType mobType : blockedMobs) {
                if (entity.getType() == mobType) {
                    return true;
                }
            }
            return false;
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
                int chunkX = admin.getLocation().getChunk().getX();
                int chunkZ = admin.getLocation().getChunk().getZ();
                sendEntityCountToAdmin(admin, chunkX, chunkZ);
                return true;
            }
        }
        return false;
    }

    public void sendEntityCountToAdmin(Player admin, int chunkX, int chunkZ) {
        Chunk chunk = Bukkit.getWorld("world").getChunkAt(chunkX, chunkZ);
        Entity[] entities = chunk.getEntities();
        int entityCount = entities.length;
        String message = "There are " + entityCount + " entities in chunk (" + chunkX + ", " + chunkZ + ")";
        admin.sendMessage(message);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
