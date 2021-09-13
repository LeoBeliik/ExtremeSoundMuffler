package com.leobeliik.extremesoundmuffler.mufflers;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.mufflers.model.MufflerModelLoader;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MufflerRegistry {
    @ObjectHolder("extremesoundmuffler:sound_muffler")
    public static MufflerBlock MUFFLER_BLOCK;

    @ObjectHolder("extremesoundmuffler:sound_muffler")
    static TileEntityType<MufflerEntity> MUFFLER_ENTITY;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new MufflerBlock());
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(MUFFLER_BLOCK, new Item.Properties().tab(ItemGroup.TAB_FOOD))
                .setRegistryName("sound_muffler"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.of(MufflerEntity::new,
                MUFFLER_BLOCK).build(null).setRegistryName("sound_muffler"));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerModelLoaders(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(SoundMuffler.MODID, "muffler_loader"), new MufflerModelLoader());
    }
}
