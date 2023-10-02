package com.phlosion.xen_integ;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class XenItems {

	private static final int BLAST_MINER_STACK_LIMIT = 1;

	// Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, XenIntegration.MODID);

	// Blocks
	public static final RegistryObject<Item> RED_BEDROCK = ITEMS.register("red_bedrock", block(XenBlocks.RED_BEDROCK, 64));
	public static final RegistryObject<Item> PURPLE_BEDROCK = ITEMS.register("purple_bedrock", block(XenBlocks.PURPLE_BEDROCK, 64));
	public static final RegistryObject<Item> BLACK_BLAST_MINER = ITEMS.register("black_blast_miner", block(XenBlocks.BLACK_BLAST_MINER, BLAST_MINER_STACK_LIMIT));
	public static final RegistryObject<Item> RED_BLAST_MINER = ITEMS.register("red_blast_miner", block(XenBlocks.RED_BLAST_MINER, BLAST_MINER_STACK_LIMIT));
	public static final RegistryObject<Item> PURPLE_BLAST_MINER = ITEMS.register("purple_blast_miner", block(XenBlocks.PURPLE_BLAST_MINER, BLAST_MINER_STACK_LIMIT));

	private XenItems() {
		throw new UnsupportedOperationException();
	}

	private static Supplier<Item> block(RegistryObject<Block> block, int stackLimit) {
		return () -> {
			return new BlockItem(block.get(), new Item.Properties()
					.tab(XenIntegration.CREATIVE_TAB)
					.stacksTo(stackLimit));
		};
	}
}
