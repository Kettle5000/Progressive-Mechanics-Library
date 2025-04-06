package com.kettle.pml.core;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

/**
 * This class allows you to get and track down damage types done on Critical Hits and Sweeping Attacks
 * Perfect to detect in any damage event if a damagesource is critical or sweeping type, so your bonuses are dealt
 * according to what your desired outcome is. By default this library mod automatically modifies the damage done
 * by using mixins.
 */
public class PMLDamageTypes {
	public static final ResourceKey<DamageType> PLAYER_CRITICAL_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("pml", "player_critical"));
	public static final ResourceKey<DamageType> SWEEPING_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("pml", "sweeping_damage"));
    
    public static Holder.Reference<DamageType> getDamageType(Level level, ResourceKey<DamageType> type) {
    	return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
    }
    
    public static boolean isCritical(DamageSource source) {
    	return source.is(PLAYER_CRITICAL_KEY);
    }
    
    public static boolean isSweeping(DamageSource source) {
    	return source.is(SWEEPING_DAMAGE_KEY);
    }
}
