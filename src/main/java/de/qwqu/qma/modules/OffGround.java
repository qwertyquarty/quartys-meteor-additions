package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.orbit.EventHandler;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class OffGround extends Module {
  public OffGround() {
    super(Addon.CATEGORY, "off-ground", "Disables onGround for movement packets.");
  }

  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Boolean> sendFallFlying = sgGeneral.add(new BoolSetting.Builder().name("send-fall-flying").description("Sends a fall flying packet when enabled.").defaultValue(true).build());
  private final Setting<Boolean> sendOnGround = sgGeneral.add(new BoolSetting.Builder().name("send-on-ground").description("Sends an onGround packet when disabled.").defaultValue(true).build());

  @Override
  public void onActivate() {
    if (sendFallFlying.get()) {
      mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
    }
  }

  @Override
  public void onDeactivate() {
    if (sendOnGround.get()) {
      mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
    }
  }

  @EventHandler
  public void onSend(PacketEvent.Send event) {
    if (!(event.packet instanceof PlayerMoveC2SPacket pkt))
      return;

    ((PlayerMoveC2SPacketAccessor) pkt).meteor$setOnGround(false);
  }
}
