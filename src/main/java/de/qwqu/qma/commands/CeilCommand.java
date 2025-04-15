package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

public class CeilCommand extends Command {
  public CeilCommand() {
    super("ceiling", "Teleports you to the ceiling.", "ceil", "top");
  }

  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.executes(context -> {
      double x = mc.player.getX();
      double z = mc.player.getZ();

      for (int y = 319; y >= -64; y--) {
        BlockPos pos = new BlockPos((int) x, y, (int) z);
        if (!mc.world.getBlockState(pos).isAir()) {
          mc.player.setPosition(x, pos.getY() + 1, z);
          break;
        }
      }

      return SINGLE_SUCCESS;
    });
  }
}
