package de.qwqu.qma.mixin;

import de.qwqu.qma.modules.CameraUtils;
import meteordevelopment.meteorclient.systems.modules.Modules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public abstract class NoPitchLimitMixin {
  @Shadow
  private float xRot;

  @Inject(method = "setXRot", at = @At("HEAD"), cancellable = true)
  private void onSetXRot(float xRot, CallbackInfo ci) {
    if (!Modules.get().get(CameraUtils.class).noPitchLimit.get()) return;
    this.xRot = xRot;
    ci.cancel();
  }

  @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
  private void removePitchClamp(double xo, double yo, CallbackInfo ci) {
    if (!Modules.get().get(CameraUtils.class).noPitchLimit.get()) return;

    Entity ts = (Entity) (Object) this;

    float f = (float) yo * 0.15F;
    float g = (float) xo * 0.15F;

    ts.setXRot(ts.getXRot() + f);
    ts.setYRot(ts.getYRot() + g);
    ts.setXRot(ts.getXRot());
    ts.xRotO += f;
    ts.yRotO += g;

    if (ts.getVehicle() != null) {
      ts.getVehicle().onPassengerTurned(ts);
    }

    ci.cancel();
  }
}
