package me.swirtzly.regeneration.handlers;

import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.client.gui.BioContainerContainer;
import me.swirtzly.regeneration.common.block.BlockHandInJar;
import me.swirtzly.regeneration.common.entity.LindosEntity;
import me.swirtzly.regeneration.common.entity.OverrideEntity;
import me.swirtzly.regeneration.common.item.FobWatchItem;
import me.swirtzly.regeneration.common.item.HandItem;
import me.swirtzly.regeneration.common.item.LindosVialItem;
import me.swirtzly.regeneration.common.tiles.TileEntityHandInJar;
import me.swirtzly.regeneration.util.RegenDamageSource;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;

import static me.swirtzly.regeneration.RegenerationMod.MODID;

/**
 * Created by Sub
 * on 16/09/2018.
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegenObjects {
	
	public static List<Item> ITEMS = new ArrayList<>();
	
	public static List<Item> ITEM_BLOCKS = new ArrayList<>();
	
	public static DamageSource REGEN_DMG_ENERGY_EXPLOSION = new RegenDamageSource("regen_energy"),
			REGEN_DMG_HEALING = new RegenDamageSource("regen_heal").setDamageAllowedInCreativeMode(), // The irony lmao
			REGEN_DMG_CRITICAL = new RegenDamageSource("regen_crit").setDamageAllowedInCreativeMode(), REGEN_DMG_KILLED = new RegenDamageSource("regen_killed"), REGEN_DMG_LINDOS = new RegenDamageSource("lindos").setDamageAllowedInCreativeMode(), REGEN_DMG_FORCED = new RegenDamageSource("forced").setDamageAllowedInCreativeMode();

	@SubscribeEvent
	public static void addItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().registerAll(
				setUpItem(new FobWatchItem(), "fob_watch"),
				setUpItem(new LindosVialItem(), "lindos_vial"),
				setUpItem(new HandItem(), "hand")
		);
		e.getRegistry().registerAll(ITEM_BLOCKS.toArray(new Item[ITEM_BLOCKS.size()]));
	}
	
	private static Item setUpItem(Item item, String name) {
		item.setRegistryName(MODID, name);
		ITEMS.add(item);
		return item;
	}
	
	
	private static Block setUpBlock(Block block, String name) {
		block.setRegistryName(MODID, name);
		return block;
	}
	
	private static void registerBlocks(IForgeRegistry<Block> reg, Block... blocks) {
		reg.registerAll(blocks);
		for (Block block : blocks) {
			ITEM_BLOCKS.add(new BlockItem(block, new Item.Properties()).setRegistryName(block.getRegistryName()));
		}
	}

	@SubscribeEvent
	public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityHandInJar::new, Blocks.HAND_JAR).build(null).setRegistryName(MODID, "hand_jar"));
	}

	@SubscribeEvent
	public static void addEntities(final RegistryEvent.Register<EntityType<?>> event) {
		IForgeRegistry<EntityType<?>> reg = event.getRegistry();


		//Item Override
		reg.register(EntityEntries.ITEM_OVERRIDE_ENTITY_TYPE = EntityType.Builder.<OverrideEntity>create(OverrideEntity::new, EntityClassification.MISC)
				.size(0.5F, 0.2F)
				.setTrackingRange(128)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new OverrideEntity(world))
				.build(RegenerationMod.MODID + ":item_override")
				.setRegistryName(new ResourceLocation(RegenerationMod.MODID, "item_override")));

		//Lindos
		reg.register(EntityEntries.ITEM_LINDOS_TYPE = EntityType.Builder.<LindosEntity>create(LindosEntity::new, EntityClassification.MISC)
				.size(0.5F, 0.2F)
				.setTrackingRange(128)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new LindosEntity(world))
				.build(RegenerationMod.MODID + ":lindos")
				.setRegistryName(new ResourceLocation(RegenerationMod.MODID, "lindos")));
	}
	
	
	@SubscribeEvent
	public static void addBlocks(RegistryEvent.Register<Block> e) {
		registerBlocks(e.getRegistry(), setUpBlock(new BlockHandInJar(), "hand_jar"));
	}
	
	
	@SubscribeEvent
	public static void addSounds(RegistryEvent.Register<SoundEvent> e) {
		e.getRegistry().registerAll(
				setUpSound("fob_watch"),
				setUpSound("critical_stage"),
				setUpSound("heart_beat"),
				setUpSound("hand_glow"),
				setUpSound("fob_watch_dialogue"),
				setUpSound("grace_hum"),
				setUpSound("regen_breath"),
				setUpSound("alarm"),
				setUpSound("jar_bubbles")
		);

		for (int i = 0; i < 7; i++) {
			e.getRegistry().register(setUpSound("regeneration_" + i));
		}
	}

	@SubscribeEvent
	public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
			BlockPos pos = data.readBlockPos();
			return new BioContainerContainer(windowId, RegenerationMod.proxy.getClientWorld(), pos, inv, RegenerationMod.proxy.getClientPlayer(), (TileEntityHandInJar) Minecraft.getInstance().world.getTileEntity(pos));
		}).setRegistryName(MODID, "bio_container"));
	}

	private static SoundEvent setUpSound(String soundName) {
		return new SoundEvent(new ResourceLocation(MODID, soundName)).setRegistryName(soundName);
	}
	
	@ObjectHolder(MODID)
	public static class Items {
		public static final Item FOB_WATCH = null;
		public static final Item LINDOS_VIAL = null;
		public static final Item HAND = null;
	}
	
	@ObjectHolder(MODID)
	public static class Sounds {
		public static final SoundEvent FOB_WATCH = null;
		public static final SoundEvent FOB_WATCH_DIALOGUE = null;
		public static final SoundEvent CRITICAL_STAGE = null;
		public static final SoundEvent HEART_BEAT = null;
		public static final SoundEvent HAND_GLOW = null;
		public static final SoundEvent GRACE_HUM = null;
		public static final SoundEvent REGEN_BREATH = null;
		public static final SoundEvent ALARM = null;
		public static final SoundEvent JAR_BUBBLES = null;

		public static final SoundEvent REGENERATION_0 = null;
		public static final SoundEvent REGENERATION_1 = null;
		public static final SoundEvent REGENERATION_2 = null;
		public static final SoundEvent REGENERATION_3 = null;
		public static final SoundEvent REGENERATION_4 = null;
		public static final SoundEvent REGENERATION_5 = null;
		public static final SoundEvent REGENERATION_6 = null;
	}

	@ObjectHolder(MODID)
	public static class EntityEntries {
		public static EntityType ITEM_OVERRIDE_ENTITY_TYPE = null;
		public static EntityType ITEM_LINDOS_TYPE = null;
	}

	@ObjectHolder(MODID)
	public static class Blocks {
		public static final Block HAND_JAR = null;
	}

	@ObjectHolder(MODID)
	public static class Tiles {
		public static final TileEntityType<TileEntityHandInJar> HAND_JAR = null;
	}

	@ObjectHolder(MODID)
	public static class Containers {
		public static final ContainerType<BioContainerContainer> BIO_CONTAINER = null;
	}

}
