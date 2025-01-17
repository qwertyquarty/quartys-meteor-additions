package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NoPitchLimit extends Module {
  public NoPitchLimit() {
    super(Addon.CATEGORY, "no-pitch-limit", "Removes the pitch limit, inspired by LiquidBounce.");
  }
}
