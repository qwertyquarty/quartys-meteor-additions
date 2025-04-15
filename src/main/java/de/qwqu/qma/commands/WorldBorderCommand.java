package de.qwqu.qma.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.command.CommandSource;

public class WorldBorderCommand extends Command {
    public WorldBorderCommand() {
        super("worldborder", "Sets the world border size.", "wb", "border");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("size", DoubleArgumentType.doubleArg()).executes(context -> {
            double size = context.getArgument("size", double.class);
            mc.world.getWorldBorder().setSize(size);

            return SINGLE_SUCCESS;
        }));
    }
}