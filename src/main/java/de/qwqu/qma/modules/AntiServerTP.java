package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class AntiServerTP extends Module {
  public AntiServerTP() {
    super(Addon.CATEGORY, "anti-server-tp", "Cancels out s2c teleportation packets.");
  }

  @EventHandler(priority = EventPriority.HIGHEST + 1)
  private void onReceivePacket(PacketEvent.Receive event) {
    if (!(event.packet instanceof PlayerPositionLookS2CPacket pkt)) return;
    event.cancel();

    mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(pkt.teleportId()));
  }

}
