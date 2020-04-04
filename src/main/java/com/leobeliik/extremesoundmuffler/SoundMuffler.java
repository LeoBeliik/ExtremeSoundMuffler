package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.InvButton;
import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("extremesoundmuffler")
public class SoundMuffler {

    public static final String MODID = "extremesoundmuffler";
    private static KeyBinding openMuffleScreen;

    public SoundMuffler() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
        initKeyBind();
    }

    private void initKeyBind() {
        openMuffleScreen = new KeyBinding(
                "Open sound muffle screen",
                KeyConflictContext.IN_GAME,
                InputMappings.INPUT_INVALID,
                "key.categories.misc");
        ClientRegistry.registerKeyBinding(openMuffleScreen);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        Screen screen = event.getGui();
        if (screen instanceof DisplayEffectsScreen && event.getWidgetList() != null) {
            event.addWidget(new InvButton((DisplayEffectsScreen) screen, 64, 9, 10, 10));
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openMuffleScreen.isPressed()) {
            SoundMufflerScreen.open();
        }
    }
}