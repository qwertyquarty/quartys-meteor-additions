package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.qwqu.qma.Util;
import de.qwqu.qma.arguments.PlayerNameOrArgumentType;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityTPCommand extends Command {
  public EntityTPCommand() {
    super("etp", "Teleports you to a player by a given name.");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
    builder.then(argument("player", PlayerNameOrArgumentType.create()).executes(context -> {
      String targetName = context.getInput().split(" ")[1];

      Entity targetEntity = Util.getTargetFromName(targetName);

      if (targetEntity == null) {
        error("Couldn't find player.");
        return SINGLE_SUCCESS;
      }

      Vec3 pos = targetEntity.position();
      mc.player.setPos(pos.x(), pos.y(), pos.z());
      return SINGLE_SUCCESS;
    }));
  }
}