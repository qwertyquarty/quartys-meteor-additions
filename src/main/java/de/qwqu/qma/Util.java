package de.qwqu.qma;

import static meteordevelopment.meteorclient.MeteorClient.mc;

import net.minecraft.entity.Entity;

public class Util {
  public static Entity getTargetFromName(String name) {
    Entity target = null;
    for (Entity entity : mc.world.getEntities()) {
      if (entity != mc.player && entity.getName().getString().equalsIgnoreCase(name)) {
        target = entity;
        break;
      }
    }
    return target;
  }
}