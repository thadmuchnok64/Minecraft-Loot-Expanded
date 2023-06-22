package thad.LootExpanded;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thad.LootExpanded.items.weapons.FlintlockPistolItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

public class LootExpanded implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");


    public static final Item THADONIUM_INGOT = new Item(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item THADONIUM_PICKAXE = new PickaxeItem(ModToolMats.THADONIUM, 12, 20, new FabricItemSettings().group(ItemGroup.TOOLS));
    public static final Item FLINTLOCK_PISTOL = new FlintlockPistolItem( new FabricItemSettings().group(ItemGroup.COMBAT));
    public static final Item FIREARM_PELLET = new Item(new Item.Settings().group(ItemGroup.COMBAT));

    //Sound
    public static final Identifier FLINTLOCK_SHOT = new Identifier("lootexpanded:flintlock_shot");
    public static SoundEvent FLINTLOCK_SHOT_EVENT = new SoundEvent(FLINTLOCK_SHOT);

    //Loot Tables
    private static final Identifier VILLAGE_PLAINS_HOUSE_LOOT_TABLE_ID = new Identifier("minecraft", "chests/village/village_plains_house");
    

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        RegisterItems();
        RegisterSoundEffects();
        ModifyLootTables();
    }

    private void RegisterItems(){
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","thadonium_ingot"),THADONIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","thadonium_pickaxe"),THADONIUM_PICKAXE);
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","flintlock_pistol"),FLINTLOCK_PISTOL);
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","firearm_pellet"),FIREARM_PELLET);

    }
    private void RegisterSoundEffects(){
        Registry.register(Registry.SOUND_EVENT, LootExpanded.FLINTLOCK_SHOT,FLINTLOCK_SHOT_EVENT);

    }
    private void ModifyLootTables(){
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
        // Documentation for modifying loot tables: https://fabricmc.net/wiki/tutorial:adding_to_loot_tables
            if (source.isBuiltin() && VILLAGE_PLAINS_HOUSE_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                    .with(ItemEntry.builder(LootExpanded.FLINTLOCK_PISTOL))
                    .with(ItemEntry.builder(LootExpanded.FIREARM_PELLET).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 5.0f), false)))
                    ;//.with(ItemEntry.builder(Blocks.AIR).weight(3));
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}