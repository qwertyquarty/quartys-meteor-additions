package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class MovementLimiter extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Integer> sendOn = sgGeneral.add(new IntSetting.Builder()
      .name("send-on")
      .description("When to send a packet.")
      .defaultValue(1)
      .noSlider()
      .build());

  private int packet = 0;

  public MovementLimiter() {
    super(Addon.CATEGORY, "movement-limiter", "Cancels out some movement packets in order to save packets.");
  }

  @Override
  public void onActivate() {
    packet = 0;
  }

  @EventHandler
  public void onSend(PacketEvent.Send event) {
    if (!(event.packet instanceof PlayerMoveC2SPacket))
      return;

    sendOn.set(Math.max(1, sendOn.get()));

    if (packet % sendOn.get() != 0) {
      event.cancel();
    }

    packet++;
  }
}
