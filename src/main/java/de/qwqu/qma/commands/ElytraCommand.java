package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraCommand extends Command {
  public ElytraCommand() {
    super("elytra", "Equips a ghost elytra.");
  }

  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
    builder.executes(context -> {
      ItemStack elytra = new ItemStack(Items.ELYTRA);
      mc.player.setItemSlot(EquipmentSlot.CHEST, elytra);
      return SINGLE_SUCCESS;
    });
  }

}
