package com.phlosion.xen_integ;

import java.util.function.Supplier;
import com.phlosion.xen_integ.blocks.BlastMinerBlock;
import com.phlosion.xen_integ.reg.XenTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class XenBlocks {
	// Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, XenIntegration.MODID);

	/**
	 * Highest-teir bedrock
	 */
	public static final RegistryObject<Block> RED_BEDROCK = BLOCKS.register("red_bedrock", XenBlocks::bedrock);
	/**
	 * Below red bedrock, but above black bedrock
	 */
	public static final RegistryObject<Block> PURPLE_BEDROCK = BLOCKS.register("purple_bedrock", XenBlocks::bedrock);
	// BLACK_BEDROCK is just vanilla bedrock

	/**
	 * Highest-tier miner. Can mine anything.
	 */
	public static final RegistryObject<Block> RED_BLAST_MINER = BLOCKS.register("red_blast_miner", miner(new BlastMinerBlock.MinerProperties()
			.immuneBlockTag(XenTags.RED_BLAST_MINER_IMMUNE)
			.primerItemTag(XenTags.RED_BLAST_MINER_PRIMER)
			.explosionSize(15))); // The blast miner and

	public static final RegistryObject<Block> PURPLE_BLAST_MINER = BLOCKS.register("purple_blast_miner", miner(new BlastMinerBlock.MinerProperties()
			.immuneBlockTag(XenTags.PURPLE_BLAST_MINER_IMMUNE)
			.primerItemTag(XenTags.PURPLE_BLAST_MINER_PRIMER)
			.explosionSize(10)));

	public static final RegistryObject<Block> BLACK_BLAST_MINER = BLOCKS.register("black_blast_miner", miner(new BlastMinerBlock.MinerProperties()
			.immuneBlockTag(XenTags.BLACK_BLAST_MINER_IMMUNE)
			.primerItemTag(XenTags.BLACK_BLAST_MINER_PRIMER)
			.explosionSize(5)));

	private XenBlocks() {
		throw new UnsupportedOperationException();
	}

	private static Block bedrock() {
		return new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK)
				.isRedstoneConductor(XenBlocks::never));
	}

	private static Supplier<Block> miner(BlastMinerBlock.MinerProperties minerProperties) {
		return () -> new BlastMinerBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).sound(SoundType.NETHERITE_BLOCK), minerProperties);
	}

	/**
	 * Copied from Blocks::never
	 */
	private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
		return false;
	}
}
