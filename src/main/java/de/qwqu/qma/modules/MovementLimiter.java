package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class MovementLimiter extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Integer> sendOn = sgGeneral.add(new IntSetting.Builder()
      .name("send-on")
      .description("When to send a packet.")
      .defaultValue(1)
      .noSlider()
      .build());

  private final Setting<Boolean> drawBox = sgGeneral.add(new BoolSetting.Builder()
      .name("draw-box")
      .description("Draws a box around the server-side position.")
      .defaultValue(false)
      .build());

  private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
      .name("shape-mode")
      .description("How the shapes are rendered.")
      .defaultValue(ShapeMode.Lines)
      .visible(drawBox::get)
      .build());

  private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
      .name("side-color")
      .description("The side color of the rendering.")
      .defaultValue(new SettingColor(225, 0, 0, 75))
      .visible(drawBox::get)
      .build());

  private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
      .name("line-color")
      .description("The line color of the rendering.")
      .defaultValue(new SettingColor(225, 0, 0, 255))
      .visible(drawBox::get)
      .build());

  private int packet = 0;
  private Vec3d lastPos = new Vec3d(0, 0, 0);

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
    } else {
      PlayerMoveC2SPacket pkt = (PlayerMoveC2SPacket) event.packet;
      lastPos = new Vec3d(pkt.getX(mc.player.getX()), pkt.getY(mc.player.getY()), pkt.getZ(mc.player.getZ()));
    }

    packet++;
  }

  @EventHandler
  private void onRender3D(Render3DEvent event) {
    if (!drawBox.get())
      return;

    double lastX = lastPos.x;
    double lastY = lastPos.y;
    double lastZ = lastPos.z;

    event.renderer.box(
        lastX - .5,
        lastY,
        lastZ - .5,
        lastX + .5,
        lastY + 2,
        lastZ + .5,
        sideColor.get(),
        lineColor.get(),
        shapeMode.get(), 1);

  }
}
