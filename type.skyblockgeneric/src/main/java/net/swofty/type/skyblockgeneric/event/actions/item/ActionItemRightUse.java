package net.swofty.type.skyblockgeneric.event.actions.item;

import lombok.SneakyThrows;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.item.ItemStack;
import net.swofty.type.generic.event.EventNodes;
import net.swofty.type.generic.event.HypixelEvent;
import net.swofty.type.generic.event.HypixelEventClass;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.item.components.InteractableComponent;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

public class ActionItemRightUse implements HypixelEventClass {

    @SneakyThrows
    @HypixelEvent(node = EventNodes.PLAYER, requireDataLoaded = true)
    public void run(PlayerUseItemOnBlockEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInMainHand();
        SkyBlockItem item = new SkyBlockItem(itemStack);
        SkyBlockPlayer player = (SkyBlockPlayer) event.getPlayer();

        org.tinylog.Logger.info("[DEBUG] PlayerUseItemOnBlockEvent fired for " + player.getUsername() + ", readyForEvents: " + player.isReadyForEvents());

        if (item.hasComponent(InteractableComponent.class)) {
            InteractableComponent interactableComponent = item.getComponent(InteractableComponent.class);
            org.tinylog.Logger.info("[DEBUG] Calling onRightClick for block interaction");
            interactableComponent.onRightClick(player, item);
        }
    }

    @SneakyThrows
    @HypixelEvent(node = EventNodes.PLAYER, requireDataLoaded = true)
    public void run(PlayerUseItemEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInMainHand();
        SkyBlockItem item = new SkyBlockItem(itemStack);
        SkyBlockPlayer player = (SkyBlockPlayer) event.getPlayer();

        org.tinylog.Logger.info("[DEBUG] PlayerUseItemEvent fired for " + player.getUsername() + ", readyForEvents: " + player.isReadyForEvents());

        if (item.hasComponent(InteractableComponent.class)) {
            InteractableComponent interactableComponent = item.getComponent(InteractableComponent.class);
            org.tinylog.Logger.info("[DEBUG] Calling onRightClick for air interaction");
            interactableComponent.onRightClick(player, item);
        }
    }
}
