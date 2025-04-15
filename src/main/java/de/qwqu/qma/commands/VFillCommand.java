package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.qwqu.qma.arguments.ClientPosArgumentType;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VFillCommand extends Command {
  private static final CommandRegistryAccess REGISTRY_ACCESS = CommandManager
      .createRegistryAccess(BuiltinRegistries.createWrapperLookup());

  public VFillCommand() {
    super("vfill", "Lets you fill areas with ghost blocks.");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("from", ClientPosArgumentType.pos()).then(argument("to", ClientPosArgumentType.pos())
        .then(argument("block", BlockStateArgumentType.blockState(REGISTRY_ACCESS)).executes((ctx) -> {
          Vec3d from = ClientPosArgumentType.getPos(ctx, "from");
          Vec3d to = ClientPosArgumentType.getPos(ctx, "to");
          BlockState blockState = ctx.getArgument("block", BlockStateArgument.class).getBlockState();

          BlockPos fromPos = new BlockPos((int) from.x, (int) from.y, (int) from.z);
          BlockPos toPos = new BlockPos((int) to.x, (int) to.y, (int) to.z);

          BlockPos.stream(fromPos, toPos).forEach(pos -> {
            // place block on client
            mc.world.setBlockState(pos, blockState);
          });

          return 1;
        }))));
  }
}
