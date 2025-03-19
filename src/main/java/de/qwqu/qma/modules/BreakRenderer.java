package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class BreakRenderer extends Module {
  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Boolean> fadeShapes = sgGeneral.add(new BoolSetting.Builder()
      .name("fade-shapes")
      .description("Fade the shape alpha values or not.")
      .defaultValue(true)
      .build());

  private final Setting<Integer> renderTime = sgGeneral.add(new IntSetting.Builder()
      .name("render-time")
      .description("For how long the shapes are rendered.")
      .defaultValue(500)
      .min(1)
      .sliderRange(0, 5000)
      .build());

  private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
      .name("shape-mode")
      .description("How the shapes are rendered.")
      .defaultValue(ShapeMode.Both)
      .build());

  private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
      .name("side-color")
      .description("The side color of the rendering.")
      .defaultValue(new SettingColor(225, 0, 0, 75))
      .build());

  private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
      .name("line-color")
      .description("The line color of the rendering.")
      .defaultValue(new SettingColor(225, 0, 0, 255))
      .build());

  private record BreakTime(long time, BlockPos pos) {
  }

  private final List<BreakTime> breakTimes = new LinkedList<>();

  public BreakRenderer() {
    super(Addon.CATEGORY, "break-renderer", "Renders block breaks.");
  }

  @EventHandler
  private void onBlockUpdate(BlockUpdateEvent event) {
    if (event.newState.isAir()) {
      if (!event.oldState.getFluidState().isEmpty())
        return;
      breakTimes.add(new BreakTime(System.currentTimeMillis(), event.pos));
    } else {
      breakTimes.removeIf(pair -> pair.pos.equals(event.pos));
    }
  }

  @EventHandler
  private void onRender3D(Render3DEvent event) {
    long currTime = System.currentTimeMillis();

    Iterator<BreakTime> iterator = breakTimes.iterator();
    while (iterator.hasNext()) {
      BreakTime pair = iterator.next();
      if (pair.time + renderTime.get() < currTime) {
        iterator.remove();
        continue;
      }

      Color sColor = new Color(sideColor.get());
      Color lColor = new Color(lineColor.get());

      if (fadeShapes.get()) {
        long elapsed = currTime - pair.time;
        float fadeFactor = 1 - ((float) elapsed / renderTime.get());

        sColor.a = (int) (sColor.a * fadeFactor);
        lColor.a = (int) (lColor.a * fadeFactor);
      }

      event.renderer.box(pair.pos, sColor, lColor, shapeMode.get(), 0);
    }
  }
}
