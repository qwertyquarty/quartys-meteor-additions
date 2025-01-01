package de.qwqu.qma.commands;

import java.util.UUID;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public class StickTargetCommand extends Command {
  public StickTargetCommand() {
    super("stick", "Sets the stick target.");
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> builder) {
    builder.then(argument("player", PlayerListEntryArgumentType.create()).executes(context -> {
      PlayerListEntry lookUpTarget = PlayerListEntryArgumentType.get(context);
      if (lookUpTarget == null) {
        error("Couldn't find lookup target.");
        return SINGLE_SUCCESS;
      }

      UUID uuid = lookUpTarget.getProfile().getId();
      if (uuid == null) {
        error("Player has no UUID.");
        return SINGLE_SUCCESS;
      }

      Entity targetEntity = null;
      for (Entity entity : mc.world.getEntities()) {
        if (entity.getUuid().equals(uuid)) {
          targetEntity = entity;
          break;
        }
      }

      if (targetEntity == null) {
        error("Couldn't find player.");
        return SINGLE_SUCCESS;
      }

      Addon.stick_targetEntity = targetEntity;
      Addon.stick_targetName = targetEntity.getName().getString();

      return SINGLE_SUCCESS;
    }));
  }
}