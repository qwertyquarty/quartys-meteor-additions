package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class LoopTP extends Module {
  public LoopTP() {
    super(Addon.CATEGORY, "loop-teleport", "Teleports to a place in a loop.");
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    mc.player.setPosition(Addon.ltp_x, Addon.ltp_y, Addon.ltp_z);
  }
}
