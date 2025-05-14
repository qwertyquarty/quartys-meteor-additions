package de.qwqu.qma.mixin;

import de.qwqu.qma.modules.CameraUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraUtils.RollAccess {
  @Unique
  private float roll = 0;

  @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", remap = false), index = 2)
  private float updateRoll(float original) {
    return (float) Math.toRadians(this.roll);
  }

  @Override
  public float qma$getRoll() {
    return this.roll;
  }

  @Override
  public void qma$setRoll(float roll) {
    this.roll = roll;
  }
}