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

  private final Setting<Integer> delay = this.sgGeneral
      .add(new IntSetting.Builder()
          .name("delay")
          .description("The delay between each twerk in ticks.")
          .defaultValue(Integer.valueOf(20))
          .min(0)
          .sliderMax(100)
          .build());

  private int timer;

  private boolean isSneaking;

  public void onActivate() {
    this.timer = 0;
    this.isSneaking = false;
  }

  public void onDeactivate() {
    this.timer = 0;
    this.mc.options.sneakKey.setPressed(false);
  }

  public MileyCyrus() {
    super(Addon.CATEGORY, "miley Cyrus", "Twerk for you.");
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    this.timer++;
    if (timer >= ((Integer) this.delay.get()).intValue()) {
      this.timer = 0;
      this.isSneaking = !this.isSneaking;
      this.mc.options.sneakKey.setPressed(this.isSneaking);
    }
  }
}
