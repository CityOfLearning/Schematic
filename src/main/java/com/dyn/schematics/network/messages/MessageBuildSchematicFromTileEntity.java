package com.dyn.schematics.network.messages;

import java.util.Map.Entry;

import com.dyn.schematics.SchematicMod;
import com.dyn.schematics.block.ClaimBlockTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBuildSchematicFromTileEntity implements IMessage {

	public static class Handler implements IMessageHandler<MessageBuildSchematicFromTileEntity, IMessage> {
		@Override
		public IMessage onMessage(final MessageBuildSchematicFromTileEntity message, final MessageContext ctx) {
			SchematicMod.proxy.addScheduledTask(() -> {
				World world = ctx.getServerHandler().player.getEntityWorld();
				TileEntity tileentity = world.getTileEntity(message.getPos());
				if ((tileentity instanceof ClaimBlockTileEntity)
						&& (((ClaimBlockTileEntity) tileentity).getSchematic() != null)) {
					if (!ctx.getServerHandler().player.capabilities.isCreativeMode) {
						for (Entry<Block, Integer> material : ((ClaimBlockTileEntity) tileentity).getSchematic()
								.getRequiredMaterials().entrySet()) {
							ctx.getServerHandler().player.inventory.clearMatchingItems(
									Item.getItemFromBlock(material.getKey()), -1, material.getValue(), null);
						}
					}
					((ClaimBlockTileEntity) tileentity).getSchematic().build(world,
							((ClaimBlockTileEntity) tileentity).getSchematicPos(), message.getRotation(),
							message.getFacing(), ctx.getServerHandler().player);
					world.setBlockState(message.getPos(), Blocks.AIR.getDefaultState(), 3);
				}
			});
			return null;
		}
	}

	private BlockPos pos;
	private int rotation;
	private EnumFacing facing;

	public MessageBuildSchematicFromTileEntity() {
	}

	public MessageBuildSchematicFromTileEntity(BlockPos pos, int rotation, EnumFacing facing) {
		this.pos = pos;
		this.rotation = rotation;
		this.facing = facing;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		rotation = buf.readInt();
		facing = EnumFacing.VALUES[buf.readInt()];
	}

	public EnumFacing getFacing() {
		return facing;
	}

	/**
	 * @return the pos
	 */
	public BlockPos getPos() {
		return pos;
	}

	public int getRotation() {
		return rotation;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(rotation);
		buf.writeInt(facing.getIndex());
	}

}