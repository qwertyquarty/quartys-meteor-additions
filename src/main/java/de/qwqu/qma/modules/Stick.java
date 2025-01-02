package de.qwqu.qma.modules;

import de.qwqu.qma.Addon;
import de.qwqu.qma.Util;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Position;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

// skidded from lyra addon & improved
public class Stick extends Module {

  private final SettingGroup sgGeneral = settings.getDefaultGroup();

  private final Setting<Follow> followMode = sgGeneral.add(new EnumSetting.Builder<Follow>()
      .name("follow")
      .description("Which parts rotation to follow.")
      .defaultValue(Follow.Head)
      .build());
  private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
      .name("rotate")
      .description("Sets the rotation to the target's rotation.")
      .defaultValue(true)
      .build());
  private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
      .name("range")
      .description("The maximum range to set target.")
      .defaultValue(6)
      .min(0)
      .sliderMax(6)
      .build());
  private final Setting<Vector3d> offset = sgGeneral.add(new Vector3dSetting.Builder()
      .name("offset")
      .description("Offset from target.")
      .defaultValue(0, 0, 0)
      .sliderRange(-3, 3)
      .decimalPlaces(1)
      .build());
  private final Setting<Boolean> enableSine = sgGeneral.add(new BoolSetting.Builder()
      .name("enable-sine-wave")
      .description("Enable sine wave movement.")
      .defaultValue(false)
      .build());
  private final Setting<Double> sineSpeed = sgGeneral.add(new DoubleSetting.Builder()
      .name("sine-speed")
      .description("Speed of sine wave movement.")
      .defaultValue(1.0)
      .min(0)
      .sliderMax(10)
      .build());
  private final Setting<Double> sineOffset = sgGeneral.add(new DoubleSetting.Builder()
      .name("sine-offset")
      .description("Offset of sine wave movement.")
      .defaultValue(1.0)
      .min(0)
      .sliderMax(10)
      .build());

  public Stick() {
    super(Addon.CATEGORY, "stick+", "Stick to a player.");
  }

  private final List<Entity> targets = new ArrayList<>();
  private double time = 0;

  private boolean entityCheck(Entity entity) {
    if (entity.equals(mc.player) || entity.equals(mc.cameraEntity))
      return false;
    if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive())
      return false;
    if (!PlayerUtils.isWithin(entity, range.get()))
      return false;
    if (!PlayerUtils.canSeeEntity(entity) && !PlayerUtils.isWithin(entity, range.get()))
      return false;
    return true;
  }

  @EventHandler
  private void onMouseButton(MouseButtonEvent event) {
    if (event.action == KeyAction.Press && event.button == GLFW_MOUSE_BUTTON_MIDDLE
        && mc.currentScreen == null) {
      if (mc.targetedEntity instanceof PlayerEntity player) {
        Addon.stick_targetName = player.getName().getString();
        Addon.stick_targetEntity = null;
      } else if (mc.targetedEntity != null) {
        Addon.stick_targetEntity = mc.targetedEntity;
        Addon.stick_targetName = "";
      } else {
        Addon.stick_targetName = "";
        Addon.stick_targetEntity = null;
        mc.player.getAbilities().flying = false;
      }

    }
  }

  @EventHandler
  private void onTick(TickEvent.Post event) {
    if ((Addon.stick_targetEntity != null && Addon.stick_targetEntity.isPlayer()) || Addon.stick_targetName != "")
      Addon.stick_targetEntity = Util.getTargetFromName(Addon.stick_targetName);

    Entity target = Addon.stick_targetEntity;

    mc.player.getAbilities().flying = !(target == null);
    if (target == null)
      return;

    time += 0.05;

    double sineWave = 0;
    double cosWave = 0;

    if (enableSine.get()) {
      sineWave = Math.sin(time * sineSpeed.get()) * sineOffset.get();
      cosWave = Math.cos(time * sineSpeed.get()) * sineOffset.get();
    }

    if (rotate.get())
      Rotations.rotate(target.getBodyYaw(), 0);

    switch (followMode.get()) {
      case Head -> {
        Position head = target.raycast(-1 + offset.get().z, 1f / 20f, false).getPos();
        mc.player.setPosition(head.getX() + offset.get().x + sineWave, head.getY() + offset.get().y,
            head.getZ() + cosWave);
      }
      case Body -> {
        mc.player.setPosition(target.getX() + offset.get().x + sineWave, target.getY() + offset.get().y,
            target.getZ() + offset.get().z + cosWave);
      }
    }
  }

  @Override
  public void onDeactivate() {
    mc.player.getAbilities().flying = false;
  }

  public void setTarget() {
    TargetUtils.getList(targets, this::entityCheck, SortPriority.LowestDistance, 1);
    if (!targets.isEmpty()) {
      Entity closest = targets.get(0);
      if (closest instanceof PlayerEntity) {
        Addon.stick_targetName = closest.getName().getString();
        Addon.stick_targetEntity = null;
      } else {
        Addon.stick_targetEntity = closest;
        Addon.stick_targetName = "";
      }
    }
  }

  public enum Follow {
    Head,
    Body
  }
}
