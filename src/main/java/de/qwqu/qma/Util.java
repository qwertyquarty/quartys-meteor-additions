package de.qwqu.qma;

import static meteordevelopment.meteorclient.MeteorClient.mc;

import net.minecraft.entity.Entity;

public class Util {
  public static Entity getTargetFromName(String name) {
    return mc.world.getPlayers().stream()
        .filter(player -> player.getName().getString().equals(name) && player != mc.player)
        .findFirst()
        .orElse(null);
  }
}