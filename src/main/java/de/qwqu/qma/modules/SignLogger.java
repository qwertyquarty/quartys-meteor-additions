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

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.error;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.sendMsg;

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
    if (!(event.packet instanceof ClientboundBlockEntityDataPacket pkt)) return;

    Vec3 pos = pkt.getPos().getCenter().subtract(0, .5, 0);
    CompoundTag nbt = pkt.getTag();
    if (nbt == null) return;

    Optional<CompoundTag> frontText = nbt.getCompound("front_text");
    Optional<CompoundTag> backText = nbt.getCompound("back_text");

    if (frontText.isEmpty() || backText.isEmpty()) return;

    Optional<ListTag> frontMessages = frontText.get().getList("messages");
    Optional<ListTag> backMessages = backText.get().getList("messages");

    if (frontMessages.isEmpty() || backMessages.isEmpty()) return;

    int frontSum = 0;
    int backSum = 0;

    ArrayList<Tuple<Integer, String>> frontList = new ArrayList<>();
    ArrayList<Tuple<Integer, String>> backList = new ArrayList<>();

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
    for (Tag t : frontMessages.get()) {
      Optional<String> messageOpt = t.asString();
      if (messageOpt.isEmpty()) continue;
      String message = messageOpt.get();

      if (!regex.isEmpty() && ignorePattern.matcher(message).find()) return;

      frontSum += message.length();
      frontList.add(new Tuple<>(i++, message));
    }

    i = 1;
    for (Tag t : backMessages.get()) {
      Optional<String> messageOpt = t.asString();
      if (messageOpt.isEmpty()) continue;
      String message = messageOpt.get();

      if (!regex.isEmpty() && ignorePattern.matcher(message).find()) return;

      backSum += message.length();
      backList.add(new Tuple<>(i++, message));
    }

    if (frontSum + backSum == 0) return;

    int x = (int) (pos.x() - 0.5);
    int y = (int) pos.y();
    int z = (int) (pos.z() - 0.5);

    Component coords = Component.literal(String.format("%d %d %d", x, y, z))
                      .withStyle(style -> style
                        .withClickEvent(new ClickEvent.SuggestCommand(".tp " + x + " " + y + " " + z))
                      );

    mc.execute(() -> sendMsg(
      Component.literal("┌─ sign ")
          .withStyle(style -> style.withColor(ChatFormatting.GRAY))
          .append(Component.literal("@ "))
          .append(coords)
          .append(String.format(
            " (%.2fm)",
            pos.distanceTo(mc.player.position())
          ))
    ));

    if (frontSum > 0) {
      for (Tuple<Integer, String> pair : frontList) {
        mc.execute(() -> info("│ %s %s", pair.getA(), pair.getB()));
      }
    }
    if (backSum > 0) {
      if (frontSum > 0) info("├─");
      for (Tuple<Integer, String> pair : backList) {
        mc.execute(() -> info("│ %s %s", pair.getA(), pair.getB()));
      }
    }

    if (logClosest.get()) {
      double closestDist = Double.MAX_VALUE;
      Player closestPlr = null;

      for (Player plr : mc.level.players()) {
        double dist = pos.distanceTo(plr.position());
        if (dist < closestDist) {
          closestPlr = plr;
          closestDist = dist;
        }
      }

      if (closestPlr == null) return;

      double finalClosestDist = closestDist;
      Player finalClosestPlr = closestPlr;

      mc.execute(() -> info(
        "└─ by %s (%sm)",
        finalClosestPlr.getName().getString(),
        String.format("%.2f", finalClosestDist)
      ));
    }
  }
}
