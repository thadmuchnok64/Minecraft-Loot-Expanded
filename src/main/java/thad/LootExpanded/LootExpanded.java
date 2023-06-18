package thad.LootExpanded;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
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

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","thadonium_ingot"),THADONIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","thadonium_pickaxe"),THADONIUM_PICKAXE);
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","flintlock_pistol"),FLINTLOCK_PISTOL);
        Registry.register(Registry.ITEM, new Identifier("lootexpanded","firearm_pellet"),FIREARM_PELLET);

        // Sound
        Registry.register(Registry.SOUND_EVENT, LootExpanded.FLINTLOCK_SHOT,FLINTLOCK_SHOT_EVENT);
        
        LOGGER.info("Hello Fabric world!");
    }
}