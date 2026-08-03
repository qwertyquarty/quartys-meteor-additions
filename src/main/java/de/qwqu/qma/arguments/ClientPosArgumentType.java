package de.qwqu.qma.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.world.phys.Vec3;

// from meteor rejects
public class ClientPosArgumentType implements ArgumentType<Vec3> {
  private static final Minecraft mc = Minecraft.getInstance();

  public static ClientPosArgumentType pos() {
    return new ClientPosArgumentType();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    if (!(context.getSource() instanceof SharedSuggestionProvider sharedSuggestionProvider)) {
      return Suggestions.empty();
    }

    String string = builder.getRemaining();
    Collection<SharedSuggestionProvider.TextCoordinates> collection;
    if (!string.isEmpty() && string.charAt(0) == '^') {
      collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
    } else {
      collection = sharedSuggestionProvider.getRelevantCoordinates();
    }

    return SharedSuggestionProvider.suggestCoordinates(string, collection, builder, Commands.createValidator(this::parse));
  }

  public static Vec3 getPos(final CommandContext<?> context, final String name) {
    return context.getArgument(name, Vec3.class);
  }

  @Override
  public Vec3 parse(StringReader reader) throws CommandSyntaxException {
    int i = reader.getCursor();
    double x, y, z;
    WorldCoordinate coordinateArgument = WorldCoordinate.parseDouble(reader, false);
    WorldCoordinate coordinateArgument2;
    WorldCoordinate coordinateArgument3;
    if (reader.canRead() && reader.peek() == ' ') {
      reader.skip();
      coordinateArgument2 = WorldCoordinate.parseDouble(reader, false);
      if (reader.canRead() && reader.peek() == ' ') {
        reader.skip();
        coordinateArgument3 = WorldCoordinate.parseDouble(reader, false);
      } else {
        reader.setCursor(i);
        throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
      }
    } else {
      reader.setCursor(i);
      throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
    }

    x = coordinateArgument.get(mc.player.getX());
    y = coordinateArgument2.get(mc.player.getY());
    z = coordinateArgument3.get(mc.player.getZ());

    return new Vec3(x, y, z);
  }
}
