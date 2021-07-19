package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
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

@Mod("extremesoundmuffler")
public class SoundMuffler {

    static final String MODID = "extremesoundmuffler";
    private static KeyBinding openMufflerScreen;

    public SoundMuffler() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

    }

    private void init(final FMLCommonSetupEvent event) {
        DataManager.loadData();
    }

    private void clientInit(final FMLClientSetupEvent event) {
        openMufflerScreen = new KeyBinding(
                "Open sound muffler screen",
                KeyConflictContext.IN_GAME,
                InputMappings.UNKNOWN,
                "key.categories.misc");
        ClientRegistry.registerKeyBinding(openMufflerScreen);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        Screen screen = event.getGui();
        if (Config.getDisableInventoryButton() || screen instanceof CreativeScreen || event.getWidgetList() == null) {
            return;
        }
        try {
            if (screen instanceof DisplayEffectsScreen) {
                event.addWidget(new InvButton((ContainerScreen) screen, 64, 9));
            }
        } catch (NullPointerException ignored) {}
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

    public static ResourceLocation getGui() {
        String texture = Config.useDarkTheme() ? "textures/gui/sm_gui_dark.png" : "textures/gui/sm_gui.png";
        return new ResourceLocation(SoundMuffler.MODID, texture);
    }

}