package de.qwqu.qma.mixin;

import de.qwqu.qma.modules.CameraUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import meteordevelopment.meteorclient.systems.modules.Modules;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class NoPitchLimitMixin {
  @Shadow
  public float prevPitch;

  @Shadow
  public float prevYaw;

  @Shadow
  private float pitch;

  @Shadow
  private float yaw;

  @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
  private void removePitchClamp(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
    if (!Modules.get().get(CameraUtils.class).noPitchLimit.get()) return;

    float deltaPitch = (float) cursorDeltaY * 0.15F;
    float deltaYaw = (float) cursorDeltaX * 0.15F;

    pitch += deltaPitch;
    yaw += deltaYaw;

    prevPitch += deltaPitch;
    prevYaw += deltaYaw;

    ci.cancel();
  }
}
