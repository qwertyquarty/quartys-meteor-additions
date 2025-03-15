package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class ZeroCommand extends Command {
  public ZeroCommand() {
    super("zero", "Sets your position to (0,0,0).");
  }

  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(context -> {
      mc.player.setPos(0, 0, 0);
      return SINGLE_SUCCESS;
    });
  }
}
