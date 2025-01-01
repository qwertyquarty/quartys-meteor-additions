package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerInventory;

public class Slotter extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  int tick = 0;

  private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
      .name("speed")
      .description("The speed of slot switching.")
      .defaultValue(10)
      .sliderMin(0)
      .sliderMax(20)
      .build());

  private final Setting<Boolean> reverse = sgGeneral.add(new BoolSetting.Builder()
      .name("reverse")
      .description("Reverses the direction of slot switching.")
      .defaultValue(false)
      .build());

  public Slotter() {
    super(Addon.CATEGORY, "slotter", "Switches hotbar slots all the time.");
  }

  private void ChangeSlot() {
    PlayerInventory inv = mc.player.getInventory();
    int currSlot = inv.selectedSlot;
    int newSlot = currSlot;

    if (reverse.get()) {
      newSlot += 8;
    } else {
      newSlot++;
    }

    newSlot %= 9;

    inv.selectedSlot = newSlot;
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    int currSpeed = 20 - speed.get();
    if (currSpeed == 20)
      return;

    tick++;

    if (tick >= currSpeed) {
      ChangeSlot();
      tick = 0;
    }
  }
}
