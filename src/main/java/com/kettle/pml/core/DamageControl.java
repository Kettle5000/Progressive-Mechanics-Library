package com.kettle.pml.core;

public class DamageControl {
	private float weapondamagecontrol; // this should only be used to reduce damage done by the weapon only, before everything is calculated
    private float baseFlatBonus;   // Base flat damage (e.g., +10)
    private float basePercentBonus; // Base % damage (e.g., +20%)
    private float flatBonus;        // Additional flat damage (e.g., +5)
    private float percentBonus;     // Additional % damage (e.g., +15%)
    private float armorReduction;
    private float specialReduction; //this reduces damage directly so be careful on use it
    private float enchReduction;
    private float originalDamage;
    private float originalAbsorption;

    public DamageControl() {
        this.baseFlatBonus = 0;
        this.basePercentBonus = 1f;
        this.flatBonus = 0;
        this.percentBonus = 1f;
        this.weapondamagecontrol = 1f;
        this.armorReduction = 0;
        this.enchReduction = 0;
        this.specialReduction = 0;
        this.originalDamage = 0;
        this.originalAbsorption = 0;
    }
    
    /**
     * Controls the damage done directly, perfect for regulating base weapon damage, use with caution
     * @param value Accepts floats that are no lower than 0.1f
     */
    public void SetWeaponDamage(float value) {
    	this.weapondamagecontrol *= Math.max(0.1f, value);
    }

    /**
     * Adds the first flat bonus value, this value is further increased by the rest of values
     * @param value Ex. DamageControl.addBaseFlatBonus(5f) will increase damage by 5 and can be increased
     */
    public void addBaseFlatBonus(float value) {
        this.baseFlatBonus += value;
    }

    /**
     * Adds the first percentual bonus value, this value is further increased by the rest of values
     * @param value Ex. DamageControl.addBasePercentBonus(0.2f) will increase damage by 20%, it increases damage done from 
     * SetWeaponDamage and addBaseFlatBonus. 
     */
    public void addBasePercentBonus(float value) {
        this.basePercentBonus += value;
    }

    /**
     * Adds a flat bonus on top of all base bonuses, this means, for example DamageControl.addFlatBonus(3f) will increase
     * damage from SetWeaponDamage, addBaseFlatBonus, addBasePercentBonus by +3f, this value can only be increased by the method
     * DamageControl.addPercentBonus.
     * @param value 
     */
    public void addFlatBonus(float value) {
        this.flatBonus += value;
    }

    /**
     * Adds a percentuial bonus on top of all bonuses, this means, for example DamageControl.addPercentBonus(0.5f) will increase
     * damage from SetWeaponDamage, addBaseFlatBonus, addBasePercentBonus, addFlatBonus by 50%, this value can no longer be increased
     * @param value
     */
    public void addPercentBonus(float value) {
        this.percentBonus += value;
    }
    
    /**
     * Returns the amount of damage reduced by armor, can return 0 if its used inside the LivingHurtEvent, since
     * minecraft hasnt calculated this amount yet. This method MUST be used inside LivingDamageEvent
     * @return float 
     */
    public float getArmorReduction() {
    	return this.armorReduction;
    }
    
    /**
     * Returns the amount of damage reduced by enchantments, can return 0 if its used inside the LivingHurtEvent, since
     * minecraft hasnt calculated this amount yet. This method MUST be used inside LivingDamageEvent
     * @return float
     */
    public float getEnchantmentReduction() {
    	return this.enchReduction;
    }
    
    /**
     * Returns a custom amount of reduction, this reduction is applied before armor and enchantments calculations.
     * It also reduces damage at the very last part of calculation so it must be handled with care.
     * @return float
     */
    public float getSpecialReduction() {
    	return this.specialReduction;
    }
    
    /**
     * Library method only, avoid use it unless you really know what are you doing.
     * It sets the amount of damage reduced by armor
     * @param float
     */
    public void setArmorReduction(float value) {
    	this.armorReduction = value;
    }
    
    /**
     * Library method only, avoid use it unless you really know what are you doing.
     * It sets the amount of damage reduced by enchantments
     * @param float
     */
    public void setEnchantmentReduction(float value) {
    	this.enchReduction = value;
    }
    
    /**
     * This method allows you to define a special damage reduction amount, by default its not used by this library.
     * But it automatically reduces damage. This will only have any effect in LivingHurtEvent. If you want to use it
     * on LivingDamageEvent, you will have to get the value and reduce it manually.
     * @param float
     */
    public void setSpecialReduction(float value) {
    	this.specialReduction = value;
    }
    
    /**
     * This method increases the value of special reduction instead of setting it. Follows the same rules.
     * This will only have any effect in LivingHurtEvent. If you want to use it
     * on LivingDamageEvent, you will have to get the value and reduce it manually.
     * @param float
     */
    public void addSpecialReduction(float value) {
    	this.specialReduction += value;
    }
    
    /**
     * Library method only, avoid use it unless you really know what are you doing.
     * Defines the amount of absorption the target had in LivingHurtEvent, before absorption points are removed.
     * @param float
     */
    public void setOriginalAbsorption(float value) {
    	this.originalAbsorption = value;
    }
    
    /**
     * Library method only, avoid use it unless you really know what are you doing.
     * This is called just after all bonus and reduction calculations are made, idea is for this value to be the last modification
     * before going to LivingDamageEvent, so when calling LivingDamageEvent this value is gonna be returned when calling getAmount
     * If called in LivingHurtEvent, it will return 0
     * @param float
     */
    public void setOriginalDamage(float value) {
    	this.originalDamage = value;
    }

    /**
     * Library method only, avoid use it unless you really know what are you doing.
     * Calculates and returns the damage with all bonuses and reductions in LivingHurtEvent, it takes the event.getAmount()
     * value as a base for calculations
     * @param baseDamage float this library uses LivingHurtEvent#getAmount() to calculate damage
     * @return float damage to be applied
     */
    public float calculateFinalDamage(float baseDamage) {
        // Apply base bonuses first
        float modifiedDamage = (baseDamage * weapondamagecontrol) + baseFlatBonus;
        modifiedDamage = modifiedDamage * basePercentBonus;

        // Apply additional bonuses
        modifiedDamage += flatBonus;
        modifiedDamage = (modifiedDamage * percentBonus) - specialReduction;
        this.originalDamage = modifiedDamage;
        return modifiedDamage;
    }

    /**
     * Method for debug and calculation, returns the total amount of base flat bonus values
     * @return float
     */
    public float getBaseFlatBonus() {
        return this.baseFlatBonus;
    }

    /**
     * Method for debug and calculation, returns the total amount of base percent bonus values
     * @return float
     */
    public float getBasePercentBonus() {
        return this.basePercentBonus;
    }
    
    /**
     * Method for debug and calculation, returns the total amount of flat bonuses.
     * @return float
     */
    public float getFlatBonus() {
        return this.flatBonus;
    }

    /**
     * Method for debug and calculation, returns the total amount of percent bonuses
     * @return float
     */
    public float getPercentBonus() {
        return this.percentBonus;
    }
    
    /**
     * Method for debug and calculation, returns the last damage done in LivingHurtEvent with all the bonuses and reductions.
     * Returns the last damage value after all calculations, including vanilla, this is the last value you should find in LivingHurtEvent
     * If used inside LivingHurtEvent it will return 0, this was meant to be used in LivingDamageEvent
     * @return float
     */
    public float getOriginalDamage() {
    	return this.originalDamage;
    }
    
    /**
     * Method for debug and calculation, returns the last absorption amount in LivingHurtEvent with all the bonuses and reductions.
     * If used inside LivingHurtEvent it will return 0, this was meant to be used in LivingDamageEvent
     * Perfect for calculate the amount of absorption lost after all damage is done
     * @return float
     */
    public float getOriginalAbsorption() {
    	return this.originalAbsorption;
    }

    /**
     * Library method only, not even I use it so you can tell how delicate it is
     * Resets all values. Use very carefully specially when First Aid mod is on
     * since this mod reuses the same DamageSource instance to be posted in LivingDamageEvent
     */
    public void resetBonuses() {
    	this.baseFlatBonus = 0;
        this.basePercentBonus = 1f;
        this.flatBonus = 0;
        this.percentBonus = 1f;
        this.weapondamagecontrol = 1f;
        this.armorReduction = 0;
        this.enchReduction = 0;
        this.specialReduction = 0;
        this.originalDamage = 0;
        this.originalAbsorption = 0;
    }
}