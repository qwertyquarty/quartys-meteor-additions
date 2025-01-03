package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.qwqu.qma.Addon;
import de.qwqu.qma.arguments.ClientPosArgumentType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.command.CommandSource;

public class OrbitCommand extends Command {
  public OrbitCommand() {
    super("orbit", "Orbits the player around a point.");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("pos", ClientPosArgumentType.pos()).executes(ctx -> {
      if (!Modules.get().get("orbit").isActive())
        Modules.get().get("orbit").toggle();

      Addon.orbit_x = ClientPosArgumentType.getPos(ctx, "pos").x;
      Addon.orbit_y = ClientPosArgumentType.getPos(ctx, "pos").y;
      Addon.orbit_z = ClientPosArgumentType.getPos(ctx, "pos").z;

      return SINGLE_SUCCESS;
    }));
  }
}
