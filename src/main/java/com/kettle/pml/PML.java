package com.kettle.pml;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PML.MODID)
public class PML
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "pml";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public PML()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PMLCommonConfig.SPEC, "pmlconfig.toml");
    }
}
