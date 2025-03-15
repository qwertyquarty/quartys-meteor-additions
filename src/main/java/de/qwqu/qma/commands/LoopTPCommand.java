package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.qwqu.qma.Addon;
import de.qwqu.qma.arguments.ClientPosArgumentType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.command.CommandSource;

public class LoopTPCommand extends Command {
  public LoopTPCommand() {
    super("looptp", "Teleports to a place in a loop.", "ltp");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("pos", ClientPosArgumentType.pos()).executes(ctx -> {
      if (!Modules.get().get("loop-teleport").isActive())
        Modules.get().get("loop-teleport").toggle();

      Addon.ltp_x = ClientPosArgumentType.getPos(ctx, "pos").x;
      Addon.ltp_y = ClientPosArgumentType.getPos(ctx, "pos").y;
      Addon.ltp_z = ClientPosArgumentType.getPos(ctx, "pos").z;

      return SINGLE_SUCCESS;
    }));

  }
}
