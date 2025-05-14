package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class CameraUtils extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();
  public final Setting<Boolean> noPitchLimit = sgGeneral.add(
          new BoolSetting.Builder()
                  .name("no-pitch-limit")
                  .description("Disables the pitch limit.")
                  .defaultValue(false)
                  .build()
  );

  public CameraUtils() {
    super(Addon.CATEGORY, "camera-utils", "Fun with camera stuff.");
  }

  private void updateRoll(boolean useRoll) {
    if (!isActive()) return;

    ((RollAccess) mc.gameRenderer.getCamera()).qma$setRoll(useRoll ? roll.get().floatValue() : 0);
  }

  @Override
  public void onActivate() {
    updateRoll(changeRoll.get());
  }

  @Override
  public void onDeactivate() {
    updateRoll(false);
  }

  public interface RollAccess {
    float qma$getRoll();

    void qma$setRoll(float roll);
  }  private final Setting<Boolean> changeRoll = sgGeneral.add(
          new BoolSetting.Builder()
                  .name("change-roll")
                  .description("Changes the camera's roll.")
                  .defaultValue(false)
                  .onChanged(this::updateRoll)
                  .build()
  );

  private final Setting<Double> roll = sgGeneral.add(
          new DoubleSetting.Builder()
                  .name("roll")
                  .description("The camera's roll.")
                  .defaultValue(0.0)
                  .visible(changeRoll::get)
                  .range(-180, 180)
                  .sliderRange(-180, 180)
                  .onChanged(v -> updateRoll(true))
                  .build()
  );


}
