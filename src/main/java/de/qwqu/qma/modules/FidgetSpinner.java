package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;

public class FidgetSpinner extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private float spinYaw;
  private float spinPitch;

  private final Setting<RotationMode> yawMode = sgGeneral.add(new EnumSetting.Builder<RotationMode>()
      .name("yaw-mode")
      .description("The way of spinning on the yaw angle.")
      .defaultValue(RotationMode.Spin)
      .build());

  private final Setting<Integer> constYaw = sgGeneral.add(new IntSetting.Builder()
      .name("yaw-constant")
      .description("The yaw constant.")
      .range(0, 360)
      .sliderRange(0, 360)
      .visible(() -> yawMode.get() == RotationMode.Constant)
      .build());

  private final Setting<RotationMode> pitchMode = sgGeneral.add(new EnumSetting.Builder<RotationMode>()
      .name("pitch-mode")
      .description("The way of spinning on the pitch angle.")
      .defaultValue(RotationMode.Constant)
      .build());

  private final Setting<Integer> constPitch = sgGeneral.add(new IntSetting.Builder()
      .name("pitch-constant")
      .description("The pitch constant.")
      .range(-180, 180)
      .sliderRange(-180, 180)
      .visible(() -> pitchMode.get() == RotationMode.Constant)
      .build());

  private final Setting<Integer> spinSpeed = sgGeneral.add(new IntSetting.Builder()
      .name("rotation-speed")
      .description("The speed of spinning.")
      .defaultValue(20)
      .sliderRange(0, 100)
      .visible(() -> pitchMode.get() == RotationMode.Spin || yawMode.get() == RotationMode.Spin)
      .build());

  public FidgetSpinner() {
    super(Addon.CATEGORY, "fidget-spinner", "Spins for you.");
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    float newYaw = 0;
    float newPitch = 0;

    switch (yawMode.get()) {
      case Spin -> {
        spinYaw += spinSpeed.get();
        newYaw = spinYaw;
      }

      case Random -> {
        newYaw = (float) Math.random() * 360;
      }

      case Constant -> {
        newYaw = constYaw.get();
      }

      case Unchanged -> {
        newYaw = mc.player.getYaw();
      }
    }

    switch (pitchMode.get()) {
      case Spin -> {
        spinPitch += spinSpeed.get();
        newPitch = spinPitch;
      }
      case Random -> {
        newPitch = (float) Math.random() * 360 - 180;
      }
      case Constant -> {
        newPitch = constPitch.get();
      }
      case Unchanged -> {
        newPitch = mc.player.getPitch();
      }
    }

    Rotations.rotate(newYaw, newPitch);
  }

  public enum RotationMode {
    Spin,
    Random,
    Constant,
    Unchanged
  }

}
