package de.qwqu.qma.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class InfoCommand extends Command {
  public InfoCommand() {
    super("info", "Uses info() to send information (useful for macros).", "notify");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("message", StringArgumentType.greedyString()).executes(ctx -> {
      String msg = StringArgumentType.getString(ctx, "message");

      info(msg);

      return SINGLE_SUCCESS;
    }));
  }
}