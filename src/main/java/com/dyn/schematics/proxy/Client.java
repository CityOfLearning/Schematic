package com.dyn.schematics.proxy;

import java.io.File;
import com.dyn.schematics.Schematic;
import com.dyn.schematics.SchematicMod;
import com.dyn.schematics.registry.SchematicRegistry;
import com.dyn.schematics.registry.SchematicRenderingRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

public class Client implements Proxy {

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(new SchematicRenderingRegistry());
	}

	@Override
	public void openSchematicGui(boolean build, BlockPos pos, Schematic schem) {
		if (build) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo((result, id) -> {
				if (result) {
					Minecraft.getMinecraft().player
							.sendChatMessage(String.format("/buildschematic " + pos.getX() + " " + pos.getY() + " "
									+ pos.getZ() + " " + SchematicRenderingRegistry.getSchematicRotation(schem)));
				}
				Minecraft.getMinecraft().displayGuiScreen(null);
			}, "Build Schematic", "Would you like to build this schematic?", 1));
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo((result, id) -> {
				if (result) {
					Minecraft.getMinecraft().player.sendChatMessage(String.format(
							"/saveschematic " + SchematicMod.startPos.getX() + " " + SchematicMod.startPos.getY() + " "
									+ SchematicMod.startPos.getZ() + " " + SchematicMod.endPos.getX() + " "
									+ SchematicMod.endPos.getY() + " " + SchematicMod.endPos.getZ()));
				}
				SchematicMod.startPos = BlockPos.ORIGIN;
				SchematicMod.endPos = BlockPos.ORIGIN;
				Minecraft.getMinecraft().displayGuiScreen(null);
			}, "Save Schematic", "Would you like to save this schematic?", 1));
		}

	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preInit() {
		File schematicLocs = new File(Minecraft.getMinecraft().mcDataDir, "schematics");

		if (!schematicLocs.exists()) {
			schematicLocs.mkdir();
		}

		SchematicRegistry.addSchematicLocation(schematicLocs);
	}
}