package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.mufflers.MufflerEntity;
import com.leobeliik.extremesoundmuffler.mufflers.model.MufflerModelLoader;
import com.leobeliik.extremesoundmuffler.networking.Network;
import com.leobeliik.extremesoundmuffler.networking.PacketMufflers;
import com.leobeliik.extremesoundmuffler.mufflers.MufflerRegistry;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("extremesoundmuffler")
public class SoundMuffler implements ISoundLists {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "extremesoundmuffler";
    private static KeyBinding openMufflerScreen;


    public SoundMuffler() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () ->
                Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::ServerInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::ClientInit);
    }

    private void ServerInit(final FMLCommonSetupEvent event) {
        Network.registerMessages();
        //load player muffled sounds
        DataManager.loadData();
    }

    private void ClientInit(final FMLClientSetupEvent event) {
        //set keybind empty
        openMufflerScreen = new KeyBinding(
                "Open sound muffler screen",
                KeyConflictContext.IN_GAME,
                InputMappings.UNKNOWN,
                "key.categories.misc");
        ClientRegistry.registerKeyBinding(openMufflerScreen);
        RenderTypeLookup.setRenderLayer(MufflerRegistry.MUFFLER_BLOCK, (RenderType) -> true);
    }

    //load client list of mufflers
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            for (MufflerEntity muffler : ISoundLists.mufflerList) {
                Network.sendToClient(new PacketMufflers(
                                muffler.getCurrentMuffledSounds(),
                                muffler.getBlockPos(),
                                muffler.getRadius(),
                                muffler.isMuffling(),
                                muffler.getTitle(),
                                false),
                        (ServerPlayerEntity) event.getPlayer());
            }
        }
    }

    //Add inventory button
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        Screen screen = event.getGui();
        if (Config.getDisableInventoryButton() || screen instanceof CreativeScreen || event.getWidgetList() == null) {
            return;
        }
        if (screen instanceof DisplayEffectsScreen) {
            event.addWidget(new InvButton((ContainerScreen) screen, 64, 9));
        }
    }

    //listen for keybind (empty by default)
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openMufflerScreen.consumeClick()) {
            MufflerScreen.open(playerMuffledList);
        }
    }

    //clear list to not interfere with other worlds
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        mufflerList.clear();
        mufflerClientList.clear();
    }

    public static int getHotkey() {
        return openMufflerScreen.getKey().getValue();
    }

    //set gui dark or bright
    public static ResourceLocation getGui() {
        String texture = Config.useDarkTheme() ? "textures/gui/sm_gui_dark.png" : "textures/gui/sm_gui.png";
        return new ResourceLocation(SoundMuffler.MODID, texture);
    }
}