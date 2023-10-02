package com.phlosion.xen_integ.reg;

import com.phlosion.xen_integ.XenIntegration;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class XenTags {

	// Blocks
	public static final TagKey<Block> RED_BLAST_MINER_IMMUNE = blockTag("red_blast_miner_immune");
	public static final TagKey<Block> PURPLE_BLAST_MINER_IMMUNE = blockTag("purple_blast_miner_immune");
	public static final TagKey<Block> BLACK_BLAST_MINER_IMMUNE = blockTag("black_blast_miner_immune");

	// Items
	public static final TagKey<Item> RED_BLAST_MINER_PRIMER = itemTag("red_blast_miner_primer");
	public static final TagKey<Item> PURPLE_BLAST_MINER_PRIMER = itemTag("purple_blast_miner_primer");
	public static final TagKey<Item> BLACK_BLAST_MINER_PRIMER = itemTag("black_blast_miner_primer");

	private XenTags() {
		throw new UnsupportedOperationException();
	}

	private static TagKey<Block> blockTag(String name) {
		return TagKey.create(Registry.BLOCK_REGISTRY, XenIntegration.resource(name));
	}

	private static TagKey<Item> itemTag(String name) {
		return TagKey.create(Registry.ITEM_REGISTRY, XenIntegration.resource(name));
	}
}
