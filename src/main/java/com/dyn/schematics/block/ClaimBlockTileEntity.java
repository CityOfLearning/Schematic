package com.dyn.schematics.block;

import com.dyn.schematics.Schematic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClaimBlockTileEntity extends TileEntity {

	private Schematic schematic;
	private BlockPos schem_pos;
	private boolean active;
	private int rotation;

	public int getRotation() {
		return rotation;
	}

	public Schematic getSchematic() {
		return schematic;
	}

	public BlockPos getSchematicPos() {
		return schem_pos;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tagCompound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	public boolean isActive() {
		return active;
	}

	public void markForUpdate() {
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		markDirty();
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("schematic")) {
			schematic = new Schematic(compound.getString("schem_name"), compound.getCompoundTag("schematic"));
			schem_pos = BlockPos.fromLong(compound.getLong("schem_loc"));
		}
		// active = compound.getBoolean("active");
		rotation = compound.getInteger("rot");
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void setSchematic(Schematic schematic, BlockPos pos) {
		this.schematic = schematic;
		schem_pos = pos;
	}

	public void setSchematicPos(BlockPos schem_pos) {
		this.schem_pos = schem_pos;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (schematic != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			schematic.writeToNBT(subcompound);
			compound.setString("schem_name", schematic.getName());
			compound.setTag("schematic", subcompound);
			compound.setLong("schem_loc", schem_pos.toLong());
		}
		// compound.setBoolean("active", active);
		compound.setInteger("rot", rotation);
		return compound;
	}
}
