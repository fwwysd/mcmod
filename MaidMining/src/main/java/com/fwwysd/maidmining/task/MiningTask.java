package com.fwwysd.maidmining.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.Task;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;

import java.util.Optional;

public class MiningTask extends Task {
    private static final int SEARCH_RADIUS = 16;

    @Override
    public void onStart(EntityMaid maid) {}

    @Override
    public void onStop(EntityMaid maid) {}

    @Override
    public boolean canStart(EntityMaid maid) {
        // 检查主手是否拿着镐子
        ItemStack stack = maid.getMainHandItem();
        return stack.getItem() instanceof DiggerItem;
    }

    @Override
    public void tick(EntityMaid maid) {
        Level level = maid.level();
        if (level.isClientSide) return;

        // 1. 寻找最近的矿石
        Optional<BlockPos> target = findNearestOre(maid);
        if (target.isEmpty()) {
            maid.setTask(EntityMaid.TaskID.WAIT);
            return;
        }

        BlockPos pos = target.get();

        // 2. 检查建筑保护
        if (isNearStructure(maid, pos)) {
            maid.setTask(EntityMaid.TaskID.WAIT);
            return;
        }

        // 3. 移动到矿石位置
        double dx = pos.getX() + 0.5 - maid.getX();
        double dz = pos.getZ() + 0.5 - maid.getZ();
        double distSq = dx * dx + dz * dz;

        if (distSq > 4.0) {
            maid.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.0);
        } else {
            // 4. 挖掘矿石
            maid.swing(maid.getUsedItemHand());
            level.destroyBlock(pos, true, maid);
        }
    }

    private Optional<BlockPos> findNearestOre(EntityMaid maid) {
        Level level = maid.level();
        BlockPos center = maid.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dy = -SEARCH_RADIUS; dy <= SEARCH_RADIUS; dy++) {
                for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                    mutable.setWithOffset(center, dx, dy, dz);
                    BlockState state = level.getBlockState(mutable);
                    if (state.is(BlockTags.ORES)) {
                        return Optional.of(mutable.immutable());
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean isNearStructure(EntityMaid maid, BlockPos pos) {
        Level level = maid.level();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int range = 6;

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    mutable.setWithOffset(pos, dx, dy, dz);
                    BlockState state = level.getBlockState(mutable);
                    Block block = state.getBlock();
                    // 保护常见建筑方块
                    if (block == Blocks.CRAFTING_TABLE || block == Blocks.FURNACE ||
                        block == Blocks.CHEST || block == Blocks.BARREL ||
                        block == Blocks.OAK_PLANKS || block == Blocks.STONE_BRICKS) {
                        return true;
                    }
                    // 保护玩家放置的标记（可自定义）
                }
            }
        }
        return false;
    }
  }
