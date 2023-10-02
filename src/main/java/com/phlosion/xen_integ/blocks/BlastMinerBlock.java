package com.phlosion.xen_integ.blocks;

import java.util.Optional;
import com.phlosion.xen_integ.reg.XenBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Based extremely heavily on Spelunkery's CompressionBlastMiner code.
 *
 */
public class BlastMinerBlock extends DirectionalBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty PRIMED = XenBlockProperties.PRIMED;

	public final TagKey<Block> immuneTag;
	public final TagKey<Item> primerTag;
	public final float explosionSize;

	public BlastMinerBlock(Properties properties, MinerProperties minerProperties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, false).setValue(PRIMED, false));
		immuneTag = minerProperties.immuneTag;
		primerTag = minerProperties.primerTag;
		explosionSize = minerProperties.explosionSize;
		if (immuneTag == null) {
			throw new IllegalArgumentException("Immune Tag not specified for blast miner! This is an error in the code.");
		}
		if (primerTag == null) {
			throw new IllegalArgumentException("Immune Tag not specified for blast miner! This is an error in the code.");
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (state.getValue(PRIMED)) {
			if (player.isSecondaryUseActive()) {
				// Un-prime the block and return the item.
				level.setBlockAndUpdate(pos, state.setValue(PRIMED, false));
				return InteractionResult.SUCCESS;
			} else if (itemStack.is(primerTag)) {
				// Player attempted to re-prime
				return InteractionResult.FAIL;
			} else {
				// Player is trying to place some other block.
				return InteractionResult.PASS;
			}
		} else {
			if (player.isSecondaryUseActive()) {
				// Respect sneak-to-place-block, even if it would prime.
				return InteractionResult.PASS;
			} else if (itemStack.is(primerTag)) {
				// Take the item from the player and prime the block.
				level.setBlockAndUpdate(pos, state.setValue(PRIMED, true));
				if (!player.getAbilities().instabuild) {
					itemStack.shrink(1);
				}
				return InteractionResult.SUCCESS;
			} else {
				// Player tried to prime with some other item.
				return InteractionResult.FAIL;
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, PRIMED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		// This state.rotate function is depreciated. The alternative function that it suggests to use:
		// 1) Requires information (world and block position) that I have no idea how to get in this context,
		// 2) Isn't documented anywhere (not even parchment),
		// 3) Isn't used by any minecraft or forge classes.
		// So no, I will not use the new function.
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public void blast(BlockState state, Level level, BlockPos pos) {
		Direction dir = state.getValue(FACING).getOpposite();
		var targetBlockPos = pos.relative(dir);
		if (state.getValue(PRIMED)) {
			level.setBlockAndUpdate(pos, state.setValue(PRIMED, false));
			var damageSource = DamageSource.explosion((LivingEntity) null);
			// The explosion is coming from within a block that has near-infinite explosion resistance.
			// Need to make a special damage calculator that ignores this miner.
			var calc = new SelfIgnoringExplosionCalculator(pos);
			level.explode(null, damageSource, calc, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, explosionSize, false, Explosion.BlockInteraction.DESTROY);

			// This is done after the explosion to allow the block to absorb some of the blast.
			if (!level.getBlockState(targetBlockPos).is(immuneTag)) {
				level.destroyBlock(targetBlockPos, false);
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		boolean bl = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
		boolean bl2 = state.getValue(POWERED);
		if (bl && !bl2) {
			level.scheduleTick(pos, this, 4);
			level.setBlock(pos, state.setValue(POWERED, true), 4);
		} else if (!bl && bl2) {
			level.setBlock(pos, state.setValue(POWERED, false), 4);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.blast(state, level, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var facingDir = context.getNearestLookingDirection().getOpposite();
		var isPowered = context.getLevel().hasNeighborSignal(context.getClickedPos());
		return defaultBlockState().setValue(FACING, facingDir)
				.setValue(PRIMED, false)
				.setValue(POWERED, isPowered);
	}

	private static class SelfIgnoringExplosionCalculator extends ExplosionDamageCalculator {
		private final BlockPos pos;

		private SelfIgnoringExplosionCalculator(BlockPos pos) {
			this.pos = pos;
		}

		private boolean isSelf(BlockPos testPos) {
			return testPos.getX() == pos.getX() && testPos.getY() == pos.getY() && testPos.getZ() == pos.getZ();
		}

		@Override
		public Optional<Float> getBlockExplosionResistance(Explosion pExplosion, BlockGetter pReader, BlockPos pTestPos, BlockState pState, FluidState pFluid) {
			if (isSelf(pTestPos)) {
				return Optional.empty();
			} else {
				return super.getBlockExplosionResistance(pExplosion, pReader, pTestPos, pState, pFluid);
			}
		}

		@Override
		public boolean shouldBlockExplode(Explosion pExplosion, BlockGetter pReader, BlockPos pTestPos, BlockState pState, float pPower) {
			if (isSelf(pTestPos)) {
				return false;
			} else {
				return super.shouldBlockExplode(pExplosion, pReader, pTestPos, pState, pPower);
			}
		}
	}

	public static class MinerProperties {
		private TagKey<Block> immuneTag = null;
		private TagKey<Item> primerTag = null;
		private float explosionSize = 5;

		public MinerProperties() {

		}

		public MinerProperties immuneBlockTag(TagKey<Block> immuneTag) {
			this.immuneTag = immuneTag;
			return this;
		}

		public MinerProperties primerItemTag(TagKey<Item> primerTag) {
			this.primerTag = primerTag;
			return this;
		}

		public MinerProperties explosionSize(float explosionSize) {
			this.explosionSize = explosionSize;
			return this;
		}
	}
}
