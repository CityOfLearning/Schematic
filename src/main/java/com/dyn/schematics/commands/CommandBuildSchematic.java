package com.dyn.schematics.commands;

import com.dyn.schematics.Schematic;
import com.dyn.schematics.item.ItemSchematic;
import com.dyn.schematics.reference.ModConfig;
import com.dyn.schematics.registry.SchematicRegistry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandBuildSchematic extends CommandBase {

	public CommandBuildSchematic() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns true if the given command sender is allowed to use this command.
	 */
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(getRequiredPermissionLevel(), getName())
				&& (sender.getCommandSenderEntity() instanceof EntityPlayer);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!ModConfig.getConfig().can_build) {
			throw new CommandException("Building from schematics now allowed", new Object[0]);
		}
		if (args.length < 1) {
			throw new CommandException("Must specify a location", new Object[0]);
		}
		BlockPos pos = BlockPos.ORIGIN;
		World world = sender.getEntityWorld();
		if (args.length < 6) {
			ItemStack stack = CommandBase.getCommandSenderAsPlayer(sender).getHeldItemMainhand();
			if (stack.getItem() instanceof ItemSchematic) {
				Schematic schem = new Schematic(stack.getDisplayName(), stack.getTagCompound());
				int rotation = 0;
				EnumFacing facing = EnumFacing.SOUTH;
				try {
					pos = CommandBase.parseBlockPos(sender, args, 0, false);
				} catch (NumberInvalidException e) {
					throw new CommandException("Location should be in numbers", new Object[0]);
				}

				if (args.length > 3) {
					try {
						rotation = Integer.parseInt(args[3]);
						if (Math.abs(rotation) > 3) {
							if (rotation > 0) {
								rotation = (rotation / 90) % 4;
							} else {
								rotation = 4 - Math.abs((rotation / 90) % 4);
							}
						}
					} catch (NumberFormatException ex) {
						throw new CommandException("Cannot Parse Rotation", new Object[0]);
					}
				}

				if (args.length == 5) {
					facing = EnumFacing.valueOf(args[4]);
				}

				schem.build(world, pos, rotation, facing, sender, true);
			} else {
				throw new CommandException("Must have schematic item equipped", new Object[0]);
			}
		} else {
			Schematic schem = SchematicRegistry.load(args[5]);
			if (schem == null) {
				throw new CommandException("Could not find schematic %s", new Object[] { args[4] });
			}
			int rotation = 0;
			EnumFacing facing = EnumFacing.SOUTH;
			try {
				facing = EnumFacing.valueOf(args[4]);
			} catch (Exception e) {
				throw new CommandException("Facing Direction could not be parsed", new Object[0]);
			}
			try {
				pos = CommandBase.parseBlockPos(sender, args, 0, false);
			} catch (NumberInvalidException e) {
				throw new CommandException("Location should be in numbers", new Object[0]);
			}

			try {
				rotation = Integer.parseInt(args[3]);
				if (Math.abs(rotation) > 3) {
					if (rotation > 0) {
						rotation = (rotation / 90) % 4;
					} else {
						rotation = 4 - Math.abs((rotation / 90) % 4);
					}
				}
			} catch (NumberFormatException ex) {
				throw new CommandException("Cannot Parse Rotation", new Object[0]);
			}

			schem.build(world, pos, rotation, facing, sender, Boolean.parseBoolean(args[6]));
		}
	}

	@Override
	public String getName() {
		return "buildschematic";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/buildschematic <x> <y> <z> [rotation] [facing] [name|equipped schematic]";
	}
}
