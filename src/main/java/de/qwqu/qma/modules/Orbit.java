package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Orbit extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Double> sineSpeed = sgGeneral.add(new DoubleSetting.Builder()
      .name("orbit-speed")
      .description("Speed of orbit.")
      .defaultValue(1.0)
      .min(0)
      .sliderMax(10)
      .build());
  private final Setting<Double> sineOffset = sgGeneral.add(new DoubleSetting.Builder()
      .name("orbit-size")
      .description("Size of orbit.")
      .defaultValue(1.0)
      .min(0)
      .sliderMax(10)
      .build());

  private final Setting<Boolean> toggleX = sgGeneral.add(new BoolSetting.Builder()
      .name("orbit-x")
      .description("Toggle orbit around X axis.")
      .defaultValue(true)
      .build());
  private final Setting<Boolean> toggleY = sgGeneral.add(new BoolSetting.Builder()
      .name("orbit-y")
      .description("Toggle orbit around Y axis.")
      .defaultValue(true)
      .build());
  private final Setting<Boolean> toggleZ = sgGeneral.add(new BoolSetting.Builder()
      .name("orbit-z")
      .description("Toggle orbit around Z axis.")
      .defaultValue(true)
      .build());

  public Orbit() {
    super(Addon.CATEGORY, "orbit", "Orbits the player around a point.");
  }

  private double time = 0;

  @EventHandler
  public void onTick(TickEvent.Post event) {
    time += 0.05;

    double sineWave = Math.sin(time * sineSpeed.get());
    double cosWave = Math.cos(time * sineSpeed.get());

    double x = sineWave * sineOffset.get();
    double z = cosWave * sineOffset.get();

    double newX = Addon.orbit_x + (toggleX.get() ? x : 0);
    double newY = toggleY.get() ? Addon.orbit_y : mc.player.getY();
    double newZ = Addon.orbit_z + (toggleZ.get() ? z : 0);

    mc.player.setPosition(newX, newY, newZ);
  }
}
