package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.orbit.EventHandler;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.systems.modules.Module;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class OffGround extends Module {
  public OffGround() {
    super(Addon.CATEGORY, "off-ground", "Disables onGround for movement packets.");
  }

  @EventHandler
  public void onSend(PacketEvent.Send event) {
    if (!(event.packet instanceof PlayerMoveC2SPacket pkt))
      return;

    ((PlayerMoveC2SPacketAccessor) pkt).setOnGround(false);
  }
}
