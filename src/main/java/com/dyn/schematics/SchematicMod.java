package com.dyn.schematics;

import org.apache.logging.log4j.Logger;

import com.dyn.schematics.block.BlockSchematicClaimStand;
import com.dyn.schematics.block.BlockSchematicClaimWall;
import com.dyn.schematics.block.ClaimBlockTileEntity;
import com.dyn.schematics.commands.CommandBuildSchematic;
import com.dyn.schematics.commands.CommandLoadSchematic;
import com.dyn.schematics.commands.CommandSaveSchematic;
import com.dyn.schematics.network.NetworkManager;
import com.dyn.schematics.proxy.Proxy;
import com.dyn.schematics.reference.Reference;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class SchematicMod {
	@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
	public static class RegistrationHandler {
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			event.getRegistry().register(SchematicMod.schematicBlockStand);
			event.getRegistry().register(SchematicMod.schematicBlockWall);
			RegistrationHandler.registerTileEntities();
		}

		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {
			event.getRegistry().register(SchematicMod.schematic);
		}

		private static void registerTileEntities() {
			GameRegistry.registerTileEntity(ClaimBlockTileEntity.class, "schem_block_te");
		}
	}

	@Mod.Instance(Reference.MOD_ID)
	public static SchematicMod instance;

	public static Logger logger;

	@SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;
	public static final Item schematic = new ItemSchematic();
	public static final Block schematicBlockStand = new BlockSchematicClaimStand();

	public static final Block schematicBlockWall = new BlockSchematicClaimWall();

	public static final SchematicsTab schemTab = new SchematicsTab();
	public static BlockPos startPos = BlockPos.ORIGIN;

	public static BlockPos endPos = BlockPos.ORIGIN;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		SchematicMod.proxy.init();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SchematicMod.logger = event.getModLog();
		SchematicMod.proxy.preInit();

		NetworkManager.registerMessages();
	}

	@Mod.EventHandler
	public void serverstart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBuildSchematic());
		event.registerServerCommand(new CommandSaveSchematic());
		event.registerServerCommand(new CommandLoadSchematic());
	}
}
