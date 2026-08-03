package de.qwqu.qma.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerNameOrArgumentType implements ArgumentType<String> {
  private static final PlayerNameOrArgumentType INSTANCE = new PlayerNameOrArgumentType();

  public static PlayerNameOrArgumentType create() {
    return INSTANCE;
  }

  public static PlayerInfo get(CommandContext<?> context) {
    return context.getArgument("player", PlayerInfo.class);
  }

  private PlayerNameOrArgumentType() {
  }

  @Override
  public String parse(StringReader reader) throws CommandSyntaxException {
    return reader.readString();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(
        mc.getConnection().getOnlinePlayers().stream().map(playerInfo -> playerInfo.getProfile().name()),
        builder);
  }
}
