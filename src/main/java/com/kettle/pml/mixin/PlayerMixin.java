package com.kettle.pml.mixin;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kettle.pml.core.PMLDamageTypes;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(Player.class)
public class PlayerMixin {

	//Aims for Flag5 so when dealing a critical hit, damage is done as one
	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0))
	private boolean modifyDamageFlag(Entity target, DamageSource originalSource, float originalDamage, @Local(ordinal = 2) boolean flag2) {
		Player player = getThis();
		if (flag2) {
		    DamageSource critical_damage = new DamageSource(PMLDamageTypes.getDamageType(player.level(), PMLDamageTypes.PLAYER_CRITICAL_KEY), player);
			return target.hurt(critical_damage, originalDamage);
		}
		return target.hurt(originalSource, originalDamage);
	}
	
	@ModifyArg(method = {"attack"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0), index = 0)
	private DamageSource modifySweepAttackDamage(DamageSource original) {
		Player player = getThis();
		if (original.is(DamageTypes.PLAYER_ATTACK)) {
			return new DamageSource(
					PMLDamageTypes.getDamageType(player.level(), PMLDamageTypes.SWEEPING_DAMAGE_KEY), player);
		} else {
			return original;
		}
	}

	private Player getThis() {
		return (Player) (Object) this;
	}
}
