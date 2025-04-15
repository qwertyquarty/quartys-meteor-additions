package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import de.qwqu.qma.Util;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;

public class Sticker extends Module {
  private int timer;
  private int index;

  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
      .name("speed")
      .description("The speed.")
      .min(0)
      .sliderMax(20)
      .build());

  public final Setting<List<String>> targets = sgGeneral.add(new StringListSetting.Builder()
      .name("targets")
      .description("The stick targets.")
      .defaultValue(new String[] {})
      .build());

  public Sticker() {
    super(Addon.CATEGORY, "sticker", "Automatically changes the stick target");
  }

  public void onActivate() {
    timer = 0;
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    timer++;
    if (timer >= speed.get()) {
      timer = 0;

      List<String> tgts = targets.get();
      List<String> validTargets = new ArrayList<>();

      for (String target : tgts) {
        Entity entity = Util.getTargetFromName(target);
        if (entity != null) {
          validTargets.add(target);
        }
      }

      if (validTargets.isEmpty())
        return;

      String targetName = validTargets.get(index % validTargets.size());
      Entity target = Util.getTargetFromName(targetName);

      if (target == null) {
        timer = speed.get();
        index++;
        return;
      }

      Addon.stick_targetName = targetName;

      index++;
    }
  }
}
