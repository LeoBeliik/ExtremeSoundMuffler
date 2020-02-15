package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.blocks.BlockReg;
import com.leobeliik.extremesoundmuffler.blocks.SoundMufflerBlock;
import com.leobeliik.extremesoundmuffler.blocks.SoundMufflerContainer;
import com.leobeliik.extremesoundmuffler.blocks.SoundMufflerTE;
import com.leobeliik.extremesoundmuffler.setup.ClientProxy;
import com.leobeliik.extremesoundmuffler.setup.IProxy;
import com.leobeliik.extremesoundmuffler.setup.ModSetup;
import com.leobeliik.extremesoundmuffler.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("extremesoundmuffler")
public class SoundMuffler {

    public static SoundMuffler instance;
    private static final Logger LOGGER = LogManager.getLogger("extremesoundmuffler");

    private static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
    private static final ModSetup setup = new ModSetup();

    public SoundMuffler() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistries);
        MinecraftForge.EVENT_BUS.register(this);
    }

    //run in server
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading and Muffling - ExremeSoundMuffler");
        setup.init();
        proxy.init();
    }

    //run in client
    private void clientRegistries(final FMLClientSetupEvent event) {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new SoundMufflerBlock());
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(new BlockItem(BlockReg.SOUNDMUFFLERBLOCK, new Item.Properties().group(ItemGroup.DECORATIONS))
                    .setRegistryName("sound_muffler")
            );
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().register(TileEntityType.Builder.create(SoundMufflerTE::new,
                    BlockReg.SOUNDMUFFLERBLOCK).build(null).setRegistryName("sound_muffler"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new SoundMufflerContainer(pos, windowId, proxy.getClientWorld(), proxy.getClientPlayer());
            }).setRegistryName("sound_muffler"));
        }
    }
}
