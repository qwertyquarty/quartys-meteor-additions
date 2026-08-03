package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.core.BlockPos;

public class CeilCommand extends Command {
  public CeilCommand() {
    super("ceiling", "Teleports you to the ceiling.", "ceil", "top");
  }

  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
    builder.executes(context -> {
      double x = mc.player.getX();
      double z = mc.player.getZ();

      for (int y = 319; y >= -64; y--) {
        BlockPos pos = new BlockPos((int) x, y, (int) z);
        if (!mc.level.getBlockState(pos).isAir()) {
          mc.player.setPos(x, pos.getY() + 1, z);
          break;
        }
      }

      return SINGLE_SUCCESS;
    });
  }
}
