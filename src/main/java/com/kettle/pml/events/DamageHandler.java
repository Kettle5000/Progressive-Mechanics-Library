package com.kettle.pml.events;

import java.util.WeakHashMap;
import java.util.function.Consumer;

import com.kettle.pml.core.DamageControl;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * In order to make use of this, other mods need to subscribe to events in normal priority, that way the Damage Control is created
 * and bonuses are applied according to the conditions they want, that they can take control over where the damage should apply
 * For example the mod Forgotten Nunchakus is able to manipulate the damage of the weapon by increasing it according to amount of 
 * attacks and if the enchantment combo is present, this can be done on their side by only subscribing on normal and using the methods 
 * getDamageControl and RegisterBonus.
 * In case of conditions that need to track down stuff like, damage reduction from armor/enchantments they can only work on LivingDamageEvent
 * this also applies to First Aid mod in case someone wants to track down which Body Part has been damaged (perfect for baubles)
 * The mod Just a lot more enchantments makes full use of this mod
 */ 

@Mod.EventBusSubscriber
public class DamageHandler {
    private static final WeakHashMap<DamageSource, DamageControl> CONTROL_TRACKER = new WeakHashMap<>();
    private static final WeakHashMap<Player, EquipmentSlot> FIRSTAID_COMPAT = new WeakHashMap<>();
    public static DamageSource lastDamageSource;
    
    /**
     * Library method only, DO NOT USE THIS METHOD UNLESS YOU KNOW WHAT YOU ARE DOING
     * @param player Player that just took damage
     * @param slot EquipmentSlot that just reduced damage when first aid mod is on
     */
    public static void setDamagedBodyPart(Player player, EquipmentSlot slot) {
    	FIRSTAID_COMPAT.put(player, slot);
    }
    
    /**
     * Returns the EquipmentSlot damaged when First aid Mod is on, can return null if first aid mod is not loaded
     * @param player Player that took damage
     * @return EquipmentSlot damaged
     */
    public static EquipmentSlot getDamagedBodyPart(Player player) {
    	return FIRSTAID_COMPAT.getOrDefault(player, null);
    }

    /**
     * Returns the DamageControl instance for a specific DamageSource, can return null.
     * @param source DamageSource linked with its own DamageControl.
     * @return Returns a DamageControl instance or null, so checks to avoid crashes are needed
     */
    public static DamageControl getDamageControl(DamageSource source) {
    	return CONTROL_TRACKER.getOrDefault(source, null);
    }
    
    /**
     * Register method, usually its not needed for mod devs to use it, however it can be used to ensure that the damage source has been registered
     * @param source DamageSource 
     * @param bonusConsumer Function to apply to the new DamageControl
     * @return Returns a DamageControl instance
     */
    public static DamageControl registerBonus(DamageSource source, Consumer<DamageControl> bonusConsumer) {
        CONTROL_TRACKER.computeIfAbsent(source, k -> new DamageControl());
        bonusConsumer.accept(CONTROL_TRACKER.get(source));
        return CONTROL_TRACKER.get(source);
    }
    
    /**
     * Register method, usually its not needed for mod devs to use it, however it can be used to ensure that the damage source has been registered
     * @param source DamageSource 
     * @return Returns a DamageControl instance
     */
    public static DamageControl registerBonus(DamageSource source) {
        CONTROL_TRACKER.computeIfAbsent(source, k -> new DamageControl());
        return CONTROL_TRACKER.get(source);
    }

    //We register every instance of this event when both entities are living entities
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void InitialRegistration(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (!event.isCanceled() && source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
        	CONTROL_TRACKER.put(source, new DamageControl());
        }
    }
    
    //We avoid lowest since First Aid Mod defines it at that point so we subscribe just above it
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        DamageControl control = CONTROL_TRACKER.get(source);
        lastDamageSource = event.getSource();
        if (control != null) {
        	control.setOriginalAbsorption(event.getEntity().getAbsorptionAmount());
            float modifiedDamage = control.calculateFinalDamage(event.getAmount());
            if (modifiedDamage <= 0) {
            	event.setCanceled(true);
            } else {
            	event.setAmount(modifiedDamage);
            }
        }
    }
    
    //When this event fires, armor and enchantments calculations should have already been made
    // therefore here is a good place to remove the Control tracker, cleaning the weakhashmap
    // and run whatever you want to run before removing it
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        DamageControl control = CONTROL_TRACKER.get(source);
        if (control != null) {
            control.resetBonuses();
        }
    }
}
