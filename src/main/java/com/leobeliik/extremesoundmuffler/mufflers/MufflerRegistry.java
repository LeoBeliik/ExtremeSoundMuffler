package com.leobeliik.extremesoundmuffler.mufflers;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MufflerRegistry {
    @ObjectHolder("extremesoundmuffler:sound_muffler")
    private static MufflerBlock ANCHOR_BLOCK;

    @ObjectHolder("extremesoundmuffler:sound_muffler")
    static TileEntityType<MufflerEntity> ANCHOR_ENTITY;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new MufflerBlock());
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(ANCHOR_BLOCK, new Item.Properties().tab(ItemGroup.TAB_FOOD))
                .setRegistryName("sound_muffler"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.of(MufflerEntity::new,
                ANCHOR_BLOCK).build(null).setRegistryName("sound_muffler"));
    }
}