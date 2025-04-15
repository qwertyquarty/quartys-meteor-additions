package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.*;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class InteractAura extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();
  private int timer;
  private Set<BlockPos> interactedBlocks = new HashSet<>();

  private final Setting<InteractMode> interactMode = sgGeneral.add(new EnumSetting.Builder<InteractMode>()
      .name("interact-mode")
      .description("The mode for interacting with blocks.")
      .defaultValue(InteractMode.Toggle)
      .build());

  private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
      .name("range")
      .description("Range of interacting with blocks.")
      .defaultValue(3)
      .min(0)
      .sliderMax(6)
      .max(12)
      .build());

  private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
      .name("delay")
      .description("Delay of interacting with blocks.")
      .defaultValue(20)
      .min(0)
      .sliderMax(100)
      .visible(() -> interactMode.get() == InteractMode.Toggle)
      .build());

  private final Setting<List<Block>> whitelist = sgGeneral.add(new BlockListSetting.Builder()
      .name("blacklist")
      .description("The blocks to interact with.")
      .build());

  public InteractAura() {
    super(Addon.CATEGORY, "interact-aura", "Interacts with all nearby interactable blocks (doors, trapdoors, levers).");
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    timer++;
    if (timer >= delay.get() || interactMode.get() != InteractMode.Toggle) {
      timer = 0;
      interactedBlocks.clear();

      for (int i = -range.get(); i <= range.get(); i++) {
        for (int j = -range.get(); j <= range.get(); j++) {
          for (int k = -range.get(); k <= range.get(); k++) {
            int x = (int) mc.player.getX() + i;
            int y = (int) mc.player.getY() + j;
            int z = (int) mc.player.getZ() + k;

            BlockPos pos = new BlockPos(x, y, z);

            BlockState state = mc.world.getBlockState(pos);

            BooleanProperty prop = null;

            if (state.getBlock() instanceof LeverBlock || state.getBlock() instanceof ButtonBlock) {
              prop = Properties.POWERED;
            } else if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapdoorBlock) {
              prop = Properties.OPEN;
            }

            if (!whitelist.get().contains(state.getBlock()))
              continue;

            if (interactedBlocks.contains(pos))
              continue;

            if (state.getBlock() instanceof DoorBlock) {
              BlockPos abovePos = pos.up();
              if (interactedBlocks.contains(abovePos))
                continue;
              interactedBlocks.add(abovePos);
            }

            if (interactMode.get() == InteractMode.Toggle) {
              BlockHitResult result = new BlockHitResult(new Vec3d(x, y, z), Direction.UP, pos, false);
              mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);
            }

            if (prop == null)
              continue;

            if (interactMode.get() == InteractMode.Open && !state.get(prop) ||
                interactMode.get() == InteractMode.Close && state.get(prop)) {
              BlockHitResult result = new BlockHitResult(new Vec3d(x, y, z), Direction.UP, pos, false);
              mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);
            }

          }
        }

      }

    }
  }

  public enum InteractMode {
    Toggle,
    Open,
    Close
  }
}
