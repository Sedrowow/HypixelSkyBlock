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

        if (!event.isFirstSpawn()) return;
        if (!player.hasAuthenticated) return;

        SharedInstance instance = player.getSkyBlockIsland().getSharedInstance().join();
        player.setInstance(instance, player.getRespawnPoint());
        player.teleport(player.getRespawnPoint());
        
        // Safety check: Ensure player is marked ready for events after island loads
        // This should already be set in SkyBlockIsland.getSharedInstance(), but ensures no race conditions
        player.setReadyForEvents();
    }
}
