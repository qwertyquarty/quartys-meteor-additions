package de.qwqu.qma.hud;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import java.util.Objects;

public class StickTarget extends HudElement {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();
  private final Setting<SettingColor> primaryColor = sgGeneral.add(new ColorSetting.Builder()
    .name("primary-color")
    .description("Primary Color.")
    .defaultValue(new SettingColor())
    .build()
  );
  private final Setting<SettingColor> secondaryColor = sgGeneral.add(new ColorSetting.Builder()
    .name("secondary-color")
    .description("Secondary Color.")
    .defaultValue(new SettingColor(175, 175, 175))
    .build()
  );
  private final Setting<Boolean> alwaysRender = sgGeneral.add(new BoolSetting.Builder()
    .name("always-render")
    .description("Always render the element even when there is no target.")
    .defaultValue(false)
    .build()
  );
  public static final HudElementInfo<StickTarget> INFO = new HudElementInfo<>(Addon.HUD_GROUP, "stick-target",
      "Displays the stick target.", StickTarget::new);

  public StickTarget() {
    super(INFO);
  }

  @Override
  public void render(HudRenderer renderer) {
    if (isInEditor()) {
      double x = renderer.text("Target: ", this.x, y, primaryColor.get(), true);
      renderer.text("qwertyquarty", x, y, secondaryColor.get(), true);
      setSize(renderer.textWidth("Target: qwertyquarty", true), renderer.textHeight(true));
      return;
    }

    if (Objects.equals(Addon.stick_targetName, "") && !alwaysRender.get())
      return;

    String displayText = "Target: " + Addon.stick_targetName;
    setSize(renderer.textWidth(displayText, true), renderer.textHeight(true));
    double x = renderer.text("Target: ", this.x, y, primaryColor.get(), true);
    renderer.text(Addon.stick_targetName, x, y, secondaryColor.get(), true);
  }
}
