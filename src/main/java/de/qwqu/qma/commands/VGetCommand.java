package de.qwqu.qma.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.item.ItemStack;

public class VGetCommand extends Command {
  public VGetCommand() {
    super("vget", "Lets you get a ghost item in survival mode.");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
    builder.then(argument("item", ItemArgument.item(REGISTRY_ACCESS)).executes(context -> {

      ItemStack item = ItemArgument.getItem(context, "item").createItemStack(1);
      giveItem(item);

      return SINGLE_SUCCESS;
    }).then(argument("number", IntegerArgumentType.integer(1, 99)).executes(context -> {
      ItemStack item = ItemArgument.getItem(context, "item")
          .createItemStack(IntegerArgumentType.getInteger(context, "number"));
      giveItem(item);

      return SINGLE_SUCCESS;
    })));
  }

  private void giveItem(ItemStack item) throws CommandSyntaxException {
    FindItemResult fir = InvUtils.find(ItemStack::isEmpty, 0, 8);
    if (!fir.found())
      return;

    mc.player.getInventory().add(item);
  }
}
