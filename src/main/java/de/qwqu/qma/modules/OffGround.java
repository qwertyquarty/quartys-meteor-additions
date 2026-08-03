package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.orbit.EventHandler;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ServerboundMovePlayerPacketAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.syncher.SynchedEntityData;

public class OffGround extends Module {
  public OffGround() {
    super(Addon.CATEGORY, "off-ground", "Disables onGround for movement packets.");
  }

  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Boolean> sendFallFlying = sgGeneral.add(new BoolSetting.Builder().name("send-fall-flying").description("Sends a fall flying packet when enabled.").defaultValue(true).build());
  private final Setting<Boolean> sendOnGround = sgGeneral.add(new BoolSetting.Builder().name("send-on-ground").description("Sends an onGround packet when disabled.").defaultValue(true).build());
  private final Setting<Boolean> spamFallFlying = sgGeneral.add(new BoolSetting.Builder().name("spam-fall-flying").description("Spams fall flying packets.").defaultValue(false).build());
  private final Setting<Integer> spamDelay = sgGeneral.add(new IntSetting.Builder().name("spam-delay").description("The delay between each fall flying packet in ticks.").defaultValue(20).min(0).visible(spamFallFlying::get).build());
  private final Setting<Boolean> reactive = sgGeneral.add(new BoolSetting.Builder().name("reactive").description("Only sends the fall flying packet when the server cancels it.").defaultValue(false).build());

  private int ticks;

  @Override
  public void onActivate() {
    if (sendFallFlying.get() && !reactive.get()) {
      mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
    }
    ticks = 0;
  }

  @Override
  public void onDeactivate() {
    if (sendOnGround.get()) {
      mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true, mc.player.horizontalCollision));
    }
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    if (!spamFallFlying.get() || reactive.get()) return;

    if (ticks >= spamDelay.get()) {
      mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
      ticks = 0;
    } else {
      ticks++;
    }
  }

  @EventHandler
  private void onReceive(PacketEvent.Receive event) {
    if (!reactive.get()) return;
    if (!(event.packet instanceof ClientboundSetEntityDataPacket pkt)) return;
    if (pkt.id() != mc.player.getId()) return;

    for (SynchedEntityData.DataValue<?> entry : pkt.packedItems()) {
      if (entry.id() == 0 && entry.value() instanceof Byte b) {
        if ((b & 0x80) == 0) {
          mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
      }
    }
  }

  @EventHandler
  public void onSend(PacketEvent.Send event) {
    if (!(event.packet instanceof ServerboundMovePlayerPacket pkt))
      return;

    ((ServerboundMovePlayerPacketAccessor) pkt).meteor$setOnGround(false);
  }
}
