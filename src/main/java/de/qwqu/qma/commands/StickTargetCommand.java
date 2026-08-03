package de.qwqu.qma.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.qwqu.qma.Addon;
import de.qwqu.qma.Util;
import de.qwqu.qma.arguments.PlayerNameOrArgumentType;
import meteordevelopment.meteorclient.commands.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class StickTargetCommand extends Command {
  public StickTargetCommand() {
    super("stick", "Sets the stick target.");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
    builder.then(argument("player", PlayerNameOrArgumentType.create()).executes(context -> {
      // as simple as that, no need to overcomplicate it (see previous code)
      Addon.stick_targetName = context.getInput().split(" ")[1];

      Util.addStickerTarget(Addon.stick_targetName);

      return SINGLE_SUCCESS;
    }));
  }
}