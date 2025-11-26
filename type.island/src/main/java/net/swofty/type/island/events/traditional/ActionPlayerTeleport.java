package net.swofty.type.island.events.traditional;

import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.SharedInstance;
import net.swofty.type.generic.event.EventNodes;
import net.swofty.type.generic.event.HypixelEvent;
import net.swofty.type.generic.event.HypixelEventClass;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

public class ActionPlayerTeleport implements HypixelEventClass {

    @HypixelEvent(node = EventNodes.PLAYER, requireDataLoaded = false)
    public void run(PlayerSpawnEvent event) {
        SkyBlockPlayer player = (SkyBlockPlayer) event.getPlayer();

        if (!player.hasAuthenticated) return;

        // Always ensure the island instance is loaded and the player is in it
        SharedInstance instance = player.getSkyBlockIsland().getSharedInstance().join();
        if (player.getInstance() != instance) {
            player.setInstance(instance, player.getRespawnPoint());
            player.teleport(player.getRespawnPoint());
        }

        // Ensure player is marked ready for events after island loads
        player.setReadyForEvents();
    }
}
