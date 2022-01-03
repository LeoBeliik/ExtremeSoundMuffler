package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.leobeliik.extremesoundmuffler.Constants.openMufflerScreen;

@Mod(Constants.MOD_ID)
public class SoundMufflerForge {

    public SoundMufflerForge() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));
        ForgeConfig.init();
        ISoundLists.forbiddenSounds.addAll(ForgeConfig.getForbiddenSounds());
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
    }

    private void clientInit(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(openMufflerScreen);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openMufflerScreen.consumeClick()) {
            SoundMufflerCommon.openMainScreen();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseRelease(ScreenEvent.MouseReleasedEvent event) {
        if (event.getButton() == 1 && InvButton.notHolding()) {
            ForgeConfig.setInvButtonHorizontal(InvButton.getButtonX());
            ForgeConfig.setInvButtonVertical(InvButton.getButtonY());
        }
    }
}