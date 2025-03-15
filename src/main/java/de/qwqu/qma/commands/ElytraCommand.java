package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraCommand extends Command {
  public ElytraCommand() {
    super("elytra", "Equips a ghost elytra.");
  }

  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(context -> {
      ItemStack elytra = new ItemStack(Items.ELYTRA);
      mc.player.equipStack(EquipmentSlot.CHEST, elytra);
      return SINGLE_SUCCESS;
    });
  }

}
