package de.qwqu.qma.mixin;

import de.qwqu.qma.modules.CameraUtils;
import meteordevelopment.meteorclient.systems.modules.Modules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class NoPitchLimitMixin {
  @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
  private void removePitchClamp(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
    if (!Modules.get().get(CameraUtils.class).noPitchLimit.get()) return;

    Entity ts = (Entity) (Object) this;

    float f = (float) cursorDeltaY * 0.15F;
    float g = (float) cursorDeltaX * 0.15F;

    ts.setPitch(ts.getPitch() + f);
    ts.setYaw(ts.getYaw() + g);
    ts.setPitch(ts.getPitch());
    ts.lastPitch += f;
    ts.lastYaw += g;

    if (ts.getVehicle() != null) {
      ts.getVehicle().onPassengerLookAround(ts);
    }

    ci.cancel();
  }
}
