package de.qwqu.qma.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(Entity.class)
public abstract class NoPitchLimitMixin {
  @Redirect(method = "changeLookDirection", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
  public float hookChangeLookDirection(float value, float min, float max) {

    if (Modules.get().get("no-pitch-limit").isActive()) {
      return value;
    }
    return MathHelper.clamp(value, min, max);
  }
}
