package com.kettle.pml.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.kettle.pml.events.DamageHandler;

import ichttt.mods.firstaid.common.util.ArmorUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;


@Mixin(ArmorUtils.class)
public abstract class ArmorUtilsMixin {


    // Intercepting the applyEnchantmentModifiers method
	@SuppressWarnings("all")
    @Inject(method = "applyEnchantmentModifiers", at = @At("HEAD"), remap = false)
    private static void onApplyEnchantmentModifiers(Player player, EquipmentSlot slot, DamageSource source, float damage, CallbackInfoReturnable<Float> ci) {
        // Save the slot that is being passed into the method
        DamageHandler.setDamagedBodyPart(player, slot);
    }
}