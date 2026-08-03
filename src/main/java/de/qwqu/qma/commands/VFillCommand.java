package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.qwqu.qma.arguments.ClientPosArgumentType;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class VFillCommand extends Command {
  public VFillCommand() {
    super("vfill", "Lets you fill areas with ghost blocks.");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
    builder.then(argument("from", ClientPosArgumentType.pos()).then(argument("to", ClientPosArgumentType.pos())
        .then(argument("block", BlockStateArgument.block(REGISTRY_ACCESS)).executes((ctx) -> {
          Vec3 from = ClientPosArgumentType.getPos(ctx, "from");
          Vec3 to = ClientPosArgumentType.getPos(ctx, "to");
          BlockInput input = ctx.getArgument("block", BlockInput.class);
          BlockState blockState = input.getState();

          BlockPos fromPos = new BlockPos((int) from.x(), (int) from.y(), (int) from.z());
          BlockPos toPos = new BlockPos((int) to.x(), (int) to.y(), (int) to.z());

          BlockPos.betweenClosed(fromPos, toPos).forEach(pos -> {
            // place block on client
            mc.level.setBlockAndUpdate(pos, blockState);
          });

          return 1;
        }))));
  }
}
