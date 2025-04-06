package com.kettle.pml.mixin;

import com.kettle.pml.core.DamageControl;
import com.kettle.pml.events.DamageHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.damagesource.CombatRules;

@Mixin(CombatRules.class)
public class CombatRulesMixin {

    // Intercept the getDamageAfterAbsorb method
    @Inject(method = "getDamageAfterAbsorb", at = @At("RETURN"), cancellable = true)
    private static void captureArmorReduction(float damage, float armorValue, float toughness, CallbackInfoReturnable<Float> cir) {
        // Calculate the original damage and reduced damage
    	if (DamageHandler.lastDamageSource != null && DamageHandler.getDamageControl(DamageHandler.lastDamageSource) != null) {
            float originalDamage = damage;
            float reducedDamage = cir.getReturnValue();
            // Calculate the amount of damage reduced by armor
            DamageControl controler = DamageHandler.getDamageControl(DamageHandler.lastDamageSource);
            controler.setArmorReduction(originalDamage - reducedDamage);
    	}
    }
	
    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("RETURN"), cancellable = true)
    private static void captureEnchantmentReduction(float damage, float armorValue, CallbackInfoReturnable<Float> cir) {
        // Calculate the original damage and reduced damage
    	if (DamageHandler.lastDamageSource != null && DamageHandler.getDamageControl(DamageHandler.lastDamageSource) != null) {
            float originalDamage = damage;
            float reducedDamage = cir.getReturnValue();
            // Calculate the amount of damage reduced by armor
            DamageControl controler = DamageHandler.getDamageControl(DamageHandler.lastDamageSource);
            controler.setEnchantmentReduction(originalDamage - reducedDamage);
    	}
    }
}
