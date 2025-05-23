package de.qwqu.qma.modules;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import de.qwqu.qma.Addon;

public class AutoCross extends Module {
  public AutoCross() {
    super(Addon.CATEGORY, "auto-cross", "Automatically shoots crossbows.");
  }

  @EventHandler
  private void onTick(TickEvent.Pre event) {
    ItemStack stack = mc.player.getMainHandStack();

    if (!(stack.getItem().equals(Items.CROSSBOW))) return;

    if (mc.player.getItemUseTimeLeft() == 0) {
      if (mc.player.isUsingItem()) mc.interactionManager.stopUsingItem(mc.player);

      if (!stack.get(DataComponentTypes.CHARGED_PROJECTILES).isEmpty())
        mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
    }
  }
}
