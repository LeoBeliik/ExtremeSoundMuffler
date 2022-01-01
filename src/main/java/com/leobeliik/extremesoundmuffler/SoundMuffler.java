package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("extremesoundmuffler")
public class SoundMuffler {

    static final String MODID = "extremesoundmuffler";
    private static KeyMapping openMufflerScreen;
    private static final Logger LOGGER = LogManager.getLogger();

    public SoundMuffler() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        Config.init();

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));

        ISoundLists.forbiddenSounds.addAll(Config.getForbiddenSounds());
    }

    private void clientInit(final FMLClientSetupEvent event) {
        openMufflerScreen = new KeyMapping(
                "Open sound muffler screen",
                KeyConflictContext.IN_GAME,
                InputConstants.UNKNOWN,
                "key.categories.misc");
        ClientRegistry.registerKeyBinding(openMufflerScreen);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerLoggin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        DataManager.loadData();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openMufflerScreen.consumeClick()) {
            MainScreen.open();
        }
    }

    public static int getHotkey() {
        return openMufflerScreen.getKey().getValue();
    }

    public static void renderGui() {
        String texture = Config.useDarkTheme() ? "textures/gui/sm_gui_dark.png" : "textures/gui/sm_gui.png";
        RenderSystem.setShaderTexture(0, (new ResourceLocation(SoundMuffler.MODID, texture)));
    }

}