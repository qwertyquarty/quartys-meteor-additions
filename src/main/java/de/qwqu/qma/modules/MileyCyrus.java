package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class MileyCyrus extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Integer> delay = this.sgGeneral.add(new IntSetting.Builder()
      .name("delay")
      .description("The delay between each twerk in ticks.")
      .defaultValue(Integer.valueOf(20))
      .min(0)
      .sliderMax(100)
      .build());

  private int timer;
  private boolean isSneaking;

  public void onActivate() {
    timer = 0;
    isSneaking = false;
  }

  public void onDeactivate() {
    timer = 0;
    mc.options.sneakKey.setPressed(false);
  }

  public MileyCyrus() {
    super(Addon.CATEGORY, "miley-cyrus", "Twerks for you.");
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    timer++;
    if (timer >= ((Integer) delay.get())) {
      timer = 0;
      isSneaking = !isSneaking;
      mc.options.sneakKey.setPressed(isSneaking);
    }
  }
}
