package de.qwqu.qma;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

import java.util.List;

import net.minecraft.entity.Entity;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Util {
  @SuppressWarnings("unchecked")
  private static Setting<Boolean> canAdd = (Setting<Boolean>) Modules.get().get("stick+").settings
      .get("sync-with-sticker");
  private static Module sticker = Modules.get().get("sticker");

  public static Entity getTargetFromName(String name) {
    Entity target = null;
    for (Entity entity : mc.world.getEntities()) {
      if (entity != mc.player && entity.getName().getString().equals(name)) {
        target = entity;
        break;
      }
    }
    return target;
  }

  public static void addStickerTarget(String name) {
    @SuppressWarnings("unchecked")
    List<String> tgts = (List<String>) Modules.get().get("sticker").settings.get("targets").get();

    if (!tgts.contains(Addon.stick_targetName) && canAdd.get() == true && sticker.isActive()) {
      tgts.add(name);
    }
  }
}