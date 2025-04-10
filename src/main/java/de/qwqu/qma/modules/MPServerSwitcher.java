package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;

public class MPServerSwitcher extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Boolean> autoSetTarget = sgGeneral.add(new BoolSetting.Builder()
          .name("auto-set-target")
          .description("Automatically sets the target server.")
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

  private static final String BRAND = "Mineplay-Lobby (Velocity)";
  private static final Pattern PATTERN = Pattern.compile(".*⇄ Servers ┃ Connecting you to mineplay-(\\d+).*|.*You are currently connected to mineplay-(\\d+).*");

  private boolean msgSent = false;

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
    Addon.LOG.info(event.packet.toString());
    if (!mc.getNetworkHandler().getBrand().equals(BRAND)) return;

    ChatUtils.sendPlayerMsg("/server mineplay-" + targetServer.get());

    msgSent = true;
  }

  @EventHandler
  private void onMessageReceive(ReceiveMessageEvent event) {
    if (!autoSetTarget.get()) return;

    String message = event.getMessage().getString();

    Matcher matcher = PATTERN.matcher(message);
    if (!matcher.find()) return;

    String server = matcher.group(1);

    if (server != null) targetServer.set(server);
  }
}
