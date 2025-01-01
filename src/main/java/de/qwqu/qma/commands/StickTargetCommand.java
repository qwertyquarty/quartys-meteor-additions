package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.qwqu.qma.Addon;
import de.qwqu.qma.commands.arguments.PlayerNameOrArgumentType;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class StickTargetCommand extends Command {
  public StickTargetCommand() {
    super("stick", "Sets the stick target.");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("player", PlayerNameOrArgumentType.create()).executes(context -> {
      // as simple as that, no need to overcomplicate it (see previous code)
      Addon.stick_targetName = context.getInput().split(" ")[1];

      return SINGLE_SUCCESS;
    }));
  }
}