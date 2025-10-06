package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

import java.util.ArrayList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

public class SignLogger extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Boolean> logClosest = sgGeneral.add(new BoolSetting.Builder().name("log-closest-player").description("Logs the closest player (might be the sign author).").defaultValue(true).build());

  public SignLogger() {
    super(Addon.CATEGORY, "sign-logger", "Logs sign texts.");
  }

  @EventHandler(priority = EventPriority.HIGHEST + 1)
  private void onReceivePacket(PacketEvent.Receive event) {
    if (!(event.packet instanceof BlockEntityUpdateS2CPacket pkt)) return;

    Vec3d pos = pkt.getPos().toCenterPos().subtract(0, .5, 0);
    NbtCompound nbt = pkt.getNbt();

    NbtCompound frontText = nbt.getCompound("front_text");
    NbtCompound backText = nbt.getCompound("back_text");

    if (frontText == null || backText == null) return;

    NbtList frontMessages = frontText.getList("messages", NbtElement.STRING_TYPE);
    NbtList backMessages = backText.getList("messages", NbtElement.STRING_TYPE);

    if (frontMessages == null || backMessages == null) return;

    int frontSum = 0;
    int backSum = 0;

    ArrayList<Pair<Integer, String>> frontList = new ArrayList<>();
    ArrayList<Pair<Integer, String>> backList = new ArrayList<>();

    int i = 1;
    for (NbtElement t : frontMessages) {
      String message = t.asString();
      if (message.length() <= 1) continue;

      message = message.substring(1, message.length() - 1);

      frontSum += message.length();

      frontList.add(new Pair<>(i++, message));
    }

    i = 1;
    for (NbtElement t : backMessages) {
      String message = t.asString();
      if (message.length() <= 1) continue;

      message = message.substring(1, message.length() - 1);

      backSum += message.length();

      backList.add(new Pair<>(i++, message));
    }

    if (frontSum + backSum == 0) return;

    mc.execute(()->info("┌─ sign @ %s %s %s (%sm)", (int)(pos.getX() - .5), (int)pos.getY(), (int)(pos.getZ() - .5), String.format("%.2f", pos.distanceTo(mc.player.getPos()))));

    if (frontSum > 0) {
      for (Pair<Integer, String> pair : frontList) {
        mc.execute(()->info("│ %s %s", pair.getLeft(), pair.getRight()));
      }
    }
    if (backSum > 0) {
      if (frontSum > 0) info("├─");
      for (Pair<Integer, String> pair : backList) {
        mc.execute(()->info("│ %s %s", pair.getLeft(), pair.getRight()));
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

      mc.execute(()->info("└─ by %s (%sm)", finalClosestPlr.getName().getString(), String.format("%.2f", finalClosestDist)));
    }
  }
}
