package de.qwqu.qma.modules;

import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

import de.qwqu.qma.Addon;

public class FarSighted extends Module {
  public FarSighted() {
    super(Addon.CATEGORY, "far-sighted", "Simple module to increase server render distance.");
  }

  @EventHandler(priority = EventPriority.HIGHEST + 69)
  private void onReceivePacket(PacketEvent.Receive event) {
    if (!(event.packet instanceof UnloadChunkS2CPacket)) return;

    mc.options.setServerViewDistance(1337);

    event.cancel();
  }
}
