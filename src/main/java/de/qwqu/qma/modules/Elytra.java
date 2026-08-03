package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Elytra extends Module {
  public Elytra() {
    super(Addon.CATEGORY, "elytra", "Equips a ghost elytra.");
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    ItemStack elytra = new ItemStack(Items.ELYTRA);
    mc.player.setItemSlot(EquipmentSlot.CHEST, elytra);
  }
}
