package de.qwqu.qma.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;

public class VGetCommand extends Command {
  private static final CommandRegistryAccess REGISTRY_ACCESS = CommandManager
      .createRegistryAccess(BuiltinRegistries.createWrapperLookup());

  public VGetCommand() {
    super("vget", "Lets you get a ghost item in survival mode.");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("item", ItemStackArgumentType.itemStack(REGISTRY_ACCESS)).executes(context -> {

      ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
      giveItem(item);

      return SINGLE_SUCCESS;
    }).then(argument("number", IntegerArgumentType.integer(1, 99)).executes(context -> {
      ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item")
          .createStack(IntegerArgumentType.getInteger(context, "number"), true);
      giveItem(item);

      return SINGLE_SUCCESS;
    })));
  }

  private void giveItem(ItemStack item) throws CommandSyntaxException {
    FindItemResult fir = InvUtils.find(ItemStack::isEmpty, 0, 8);
    if (!fir.found())
      return;

    mc.player.giveItemStack(item);
  }
}