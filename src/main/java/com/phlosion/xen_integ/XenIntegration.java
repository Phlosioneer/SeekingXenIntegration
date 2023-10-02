package com.phlosion.xen_integ;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(XenIntegration.MODID)
public class XenIntegration {
	// Define mod id in a common place for everything to reference
	public static final String MODID = "xen_integ";
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(XenBlocks.RED_BEDROCK.get().asItem());
		};
	};

	public XenIntegration() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Register the commonSetup method for modloading
		// modEventBus.addListener(this::commonSetup);

		// Register the Deferred Register to the mod event bus so blocks get registered
		XenBlocks.BLOCKS.register(modEventBus);

		// Register the Deferred Register to the mod event bus so items get registered
		XenItems.ITEMS.register(modEventBus);

		// Register ourselves for server and other game events we are interested in
		// MinecraftForge.EVENT_BUS.register(this);
	}

	public static ResourceLocation resource(String subpath) {
		return new ResourceLocation(MODID, subpath);
	}

	/*
	 * private void commonSetup(final FMLCommonSetupEvent event) {
	 * // Some common setup code
	 * // LOGGER.info("HELLO FROM COMMON SETUP");
	 * // LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
	 * }
	 * 
	 * // You can use SubscribeEvent and let the Event Bus discover methods to call
	 * 
	 * @SubscribeEvent
	 * public void onServerStarting(ServerStartingEvent event) {
	 * // Do something when the server starts
	 * // LOGGER.info("HELLO from server starting");
	 * }
	 * 
	 * // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
	 * 
	 * @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	 * public static class ClientModEvents {
	 * 
	 * @SubscribeEvent
	 * public static void onClientSetup(FMLClientSetupEvent event) {
	 * // Some client setup code
	 * // LOGGER.info("HELLO FROM CLIENT SETUP");
	 * // LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
	 * }
	 * }
	 */
}
