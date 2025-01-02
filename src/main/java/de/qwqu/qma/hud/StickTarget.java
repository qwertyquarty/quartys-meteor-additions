package de.qwqu.qma.hud;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class StickTarget extends HudElement {
  public static final HudElementInfo<StickTarget> INFO = new HudElementInfo<>(Addon.HUD_GROUP, "stick-target",
      "Displays the stick target.", StickTarget::new);

  public StickTarget() {
    super(INFO);
  }

  @Override
  public void render(HudRenderer renderer) {
    if (isInEditor()) {
      renderer.text("Target: qwertyquarty", x, y, Color.WHITE, true);
      setSize(renderer.textWidth("Target: qwertyquarty", true), renderer.textHeight(true));
      return;
    }

    if (Addon.stick_targetName == "")
      return;

    String displayText = "Target: " + Addon.stick_targetName;
    setSize(renderer.textWidth(displayText, true), renderer.textHeight(true));
    renderer.text(displayText, x, y, Color.WHITE, true);

  }
}