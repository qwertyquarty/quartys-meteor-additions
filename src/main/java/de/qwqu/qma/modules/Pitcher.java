package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;

public class Pitcher extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Integer> pitch = sgGeneral.add(new IntSetting.Builder()
      .name("pitch")
      .description("The pitch to use.")
      .defaultValue(0)
      .range(-180, 180)
      .sliderRange(-180, 180)
      .build());

  public Pitcher() {
    super(Addon.CATEGORY, "pitcher", "Sets your pitch to a custom number.");
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    Rotations.rotate(mc.player.getYaw(), pitch.get());
  }
}
