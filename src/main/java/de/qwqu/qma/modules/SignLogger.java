package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

public class SignLogger extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Boolean> logClosest = sgGeneral.add(new BoolSetting.Builder()
    .name("log-closest-player")
    .description("Logs the closest player (might be the sign author).")
    .defaultValue(true)
    .build());
  private final Setting<String> ignoreRegex = sgGeneral.add(new StringSetting.Builder()
    .name("ignore-regex")
    .description("Ignore sign lines matching this regex. Leave empty to disable.")
    .defaultValue("")
    .build());

  public SignLogger() {
    super(Addon.CATEGORY, "sign-logger", "Logs sign texts.");
  }

  @EventHandler(priority = EventPriority.HIGHEST + 1)
  private void onReceivePacket(PacketEvent.Receive event) {
    if (!(event.packet instanceof BlockEntityUpdateS2CPacket pkt)) return;

    Vec3d pos = pkt.getPos().toCenterPos().subtract(0, .5, 0);
    NbtCompound nbt = pkt.getNbt();

    Optional<NbtCompound> frontText = nbt.getCompound("front_text");
    Optional<NbtCompound> backText = nbt.getCompound("back_text");

    if (frontText.isEmpty() || backText.isEmpty()) return;

    Optional<NbtList> frontMessages = frontText.get().getList("messages");
    Optional<NbtList> backMessages = backText.get().getList("messages");

    if (frontMessages.isEmpty() || backMessages.isEmpty()) return;

    int frontSum = 0;
    int backSum = 0;

    ArrayList<Pair<Integer, String>> frontList = new ArrayList<>();
    ArrayList<Pair<Integer, String>> backList = new ArrayList<>();

    String regex = ignoreRegex.get().trim();
    Pattern ignorePattern = null;
    if (!regex.isEmpty()) {
      try {
        ignorePattern = Pattern.compile(regex);
      } catch (PatternSyntaxException e) {
        e.printStackTrace();
        error("an error occured parsing the regular expression");
        return;
      }
    }

    int i = 1;
    for (NbtElement t : frontMessages.get()) {
      String message = t.asString().get();

      if (!regex.isEmpty() && ignorePattern.matcher(message).find()) return;

      frontSum += message.length();

      frontList.add(new Pair<>(i++, message));
    }

    i = 1;
    for (NbtElement t : backMessages.get()) {
      String message = t.asString().get();

      if (!regex.isEmpty() && ignorePattern.matcher(message).find()) return;

      backSum += message.length();

      backList.add(new Pair<>(i++, message));
    }

    if (frontSum + backSum == 0) return;

    mc.execute(() -> info(
      "┌─ sign @ %s %s %s (%sm)",
      (int) (pos.getX() - .5),
      (int) pos.getY(),
      (int) (pos.getZ() - .5),
      String.format("%.2f", pos.distanceTo(mc.player.getPos()))
    ));

    if (frontSum > 0) {
      for (Pair<Integer, String> pair : frontList) {
        mc.execute(() -> info("│ %s %s", pair.getLeft(), pair.getRight()));
      }
    }
    if (backSum > 0) {
      if (frontSum > 0) info("├─");
      for (Pair<Integer, String> pair : backList) {
        mc.execute(() -> info("│ %s %s", pair.getLeft(), pair.getRight()));
      }
    }

    if (logClosest.get()) {
      double closestDist = Double.MAX_VALUE;
      PlayerEntity closestPlr = null;

      for (PlayerEntity plr : mc.world.getPlayers()) {
        double dist = pos.distanceTo(plr.getPos());
        if (dist < closestDist) {
          closestPlr = plr;
          closestDist = dist;
        }
      }

      if (closestPlr == null) return;

      double finalClosestDist = closestDist;
      PlayerEntity finalClosestPlr = closestPlr;

      mc.execute(() -> info(
        "└─ by %s (%sm)",
        finalClosestPlr.getName().getString(),
        String.format("%.2f", finalClosestDist)
      ));
    }
  }
}
