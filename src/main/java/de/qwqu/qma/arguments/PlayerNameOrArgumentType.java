package de.qwqu.qma.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerNameOrArgumentType implements ArgumentType<String> {
  private static final PlayerNameOrArgumentType INSTANCE = new PlayerNameOrArgumentType();

  public static PlayerNameOrArgumentType create() {
    return INSTANCE;
  }

  public static PlayerListEntry get(CommandContext<?> context) {
    return context.getArgument("player", PlayerListEntry.class);
  }

  private PlayerNameOrArgumentType() {
  }

  @Override
  public String parse(StringReader reader) throws CommandSyntaxException {
    return reader.readString();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return CommandSource.suggestMatching(
        mc.getNetworkHandler().getPlayerList().stream().map(playerListEntry -> playerListEntry.getProfile().name()),
        builder);
  }
}