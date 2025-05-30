package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.Vec3d;

public class MPServerSwitcher extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Boolean> autoSetTarget = sgGeneral.add(new BoolSetting.Builder()
          .name("auto-set-target")
          .description("Automatically sets the target server.")
          .defaultValue(true)
          .build());

  private final Setting<Boolean> preservePosition = sgGeneral.add(new BoolSetting.Builder()
          .name("preserve-position")
          .description("Preserves your position when joining.")
          .defaultValue(true)
          .build());

  private final Setting<String> targetServer = sgGeneral.add(new StringSetting.Builder()
          .name("target-server")
          .description("The server to teleport to when when spawning in.")
          .defaultValue("lobby")
          .build());

  public MPServerSwitcher() {
    super(Addon.CATEGORY, "MPServerSwitcher", "Automatically switches the server. (for mc.mineplay.nl)");
  }

  @Override
  public String getInfoString() {
    return targetServer.get();
  }

  private static final String BRAND = "Mineplay-Lobby (Velocity)";

  private boolean msgSent = false;
  public boolean inLobby = false;
  private boolean teleported = false;
  private Vec3d lastPos = new Vec3d(0, 0, 0);
  private float lastYaw = 0;
  private float lastPitch = 0;

  private void onDisable() {
    msgSent = false;
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    msgSent = false;
  }

  @EventHandler
  private void onPacket(PacketEvent.Receive event) {
    if (msgSent) return;
    if (!(event.packet instanceof ChunkDataS2CPacket)) return;
    if (! inLobby) return;

    ChatUtils.sendPlayerMsg("/server mineplay-" + targetServer.get());

    msgSent = true;
    inLobby = false;
    teleported = false;
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    inLobby = mc.getNetworkHandler().getBrand().equals(BRAND);
    if (inLobby) return;

    if (!preservePosition.get()) return;

    if (mc.player.getPos().equals(lastPos) || teleported) {
      teleported = true;
    } else {
      mc.player.setVelocity(0, 0, 0);
      mc.player.setPosition(lastPos);
      mc.player.setYaw(lastYaw);
      mc.player.setPitch(lastPitch);
      return;
    }

    lastPos = mc.player.getPos();
    lastYaw = mc.player.getYaw();
    lastPitch = mc.player.getPitch();
  }

  @EventHandler
  private void onMessageReceive(ReceiveMessageEvent event) {
    if (!autoSetTarget.get()) return;

    String message = event.getMessage().getString();

    if (message.startsWith("[")) return;
    if (!message.contains("to mineplay-")) return;

    String server = message.split("mineplay-")[1];
    server = server.replaceAll("[^0-9]", "");

    targetServer.set(server);
  }
}
