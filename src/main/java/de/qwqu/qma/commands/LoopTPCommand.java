package de.qwqu.qma.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.command.CommandSource;

public class LoopTPCommand extends Command {
  public LoopTPCommand() {
    super("looptp", "Teleports to a place in a loop.", "ltp");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("x", DoubleArgumentType.doubleArg())
        .then(argument("y", DoubleArgumentType.doubleArg())
            .then(argument("z", DoubleArgumentType.doubleArg())
                .executes(context -> {
                  if (!Modules.get().get("loop-teleport").isActive())
                    Modules.get().get("loop-teleport").toggle();

                  Addon.ltp_x = context.getArgument("x", Double.class);
                  Addon.ltp_y = context.getArgument("y", Double.class);
                  Addon.ltp_z = context.getArgument("z", Double.class);
                  return SINGLE_SUCCESS;
                }))));
  }
}
