package de.qwqu.qma.modules;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
    ItemStack stack = mc.player.getMainHandItem();

    if (!(stack.getItem().equals(Items.CROSSBOW))) return;

    if (mc.player.getUseItemRemainingTicks() == 0) {
      if (mc.player.isUsingItem()) mc.gameMode.releaseUsingItem(mc.player);

      if (!stack.get(DataComponents.CHARGED_PROJECTILES).isEmpty())
        mc.gameMode.useItem(mc.player, mc.player.getUsedItemHand());
    }
  }
}
