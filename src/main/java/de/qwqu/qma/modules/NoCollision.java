package de.qwqu.qma.modules;

import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Block;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;

import de.qwqu.qma.Addon;

// from vector addon
public class NoCollision extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  // General

  private final Setting<Boolean> useBoundingBox = sgGeneral.add(new BoolSetting.Builder()
      .name("use-bounding-box")
      .description("Enables using bounding box collision instead of full block collision.")
      .defaultValue(false)
      .build());
  private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
      .name("blocks")
      .description("The blocks to fill the holes with.")
      .defaultValue(List.of())
      .visible(() -> !useBoundingBox.get())
      .build());

  private final Setting<ListMode> blockFilter = sgGeneral.add(new EnumSetting.Builder<ListMode>()
      .name("block-filter")
      .description("How to use the block list setting")
      .defaultValue(ListMode.Whitelist)
      .visible(() -> !useBoundingBox.get())
      .build());

  private final Setting<Boolean> borderCollision = sgGeneral.add(new BoolSetting.Builder()
      .name("no-world-border-collision")
      .description("Cancels the collision with the world border to allow you to walk through it.")
      .defaultValue(true)
      .visible(() -> !useBoundingBox.get())
      .build());

  // Constructor

  public NoCollision() {
    super(Addon.CATEGORY, "no-collision", "Removes block and world border collision client-side.");
  }

  // Canceling Collision

  @EventHandler(priority = EventPriority.LOWEST + 25)
  private void onCollisionShape(CollisionShapeEvent event) {
    if (validBlock(event.state.getBlock()) && !useBoundingBox.get())
      event.shape = VoxelShapes.empty();
  }

  // Utils

  private boolean validBlock(Block block) {
    if (blockFilter.get() == ListMode.Blacklist && blocks.get().contains(block))
      return false;
    else
      return blockFilter.get() != ListMode.Whitelist || blocks.get().contains(block);
  }

  // Getter

  public boolean shouldCancelBorderCollision() {
    return borderCollision.get();
  }

  // BoundingBox noclip implementation
  @EventHandler
  private void onTick(TickEvent.Post event) {
    if (useBoundingBox.get()) {
      mc.player.setBoundingBox(new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
    }
  }

  // Constant

  public enum ListMode {
    Whitelist,
    Blacklist
  }
}