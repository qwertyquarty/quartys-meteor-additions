package de.qwqu.qma.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.command.CommandSource;

public class OrbitCommand extends Command {
  public OrbitCommand() {
    super("orbit", "Orbits the player around a point.");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("x", DoubleArgumentType.doubleArg())
        .then(argument("y", DoubleArgumentType.doubleArg())
            .then(argument("z", DoubleArgumentType.doubleArg())
                .executes(context -> {
                  if (!Modules.get().get("orbit").isActive())
                    Modules.get().get("orbit").toggle();

                  Addon.orbit_x = context.getArgument("x", Double.class);
                  Addon.orbit_y = context.getArgument("y", Double.class);
                  Addon.orbit_z = context.getArgument("z", Double.class);

                  return SINGLE_SUCCESS;
                }))));

  }

}
