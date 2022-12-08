package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.leobeliik.extremesoundmuffler.Constants.soundMufflerKey;

@Mod(Constants.MOD_ID)
public class SoundMufflerForge {

    public SoundMufflerForge() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //prevent server complain when this mod is clientside only
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));

        ForgeConfig.init();
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::keyRegistry));
        bus.addListener(ForgeConfig::onLoad);
    }

    private void keyRegistry(final RegisterKeyMappingsEvent event) {
        event.register(soundMufflerKey);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent //on mod keybind press
    public void onKeyInput(InputEvent event) {
        if (soundMufflerKey.consumeClick()) {
            SoundMufflerCommon.openMainScreen();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent //save the new coordinates for the inv button
    public void onMouseRelease(ScreenEvent.MouseButtonReleased event) {
        if (event.getButton() == 1) {
            for (GuiEventListener widget : event.getScreen().children()) {
                if (widget instanceof InvButton && ((InvButton) widget).isDrag()) {
                    ForgeConfig.setInvButtonHorizontal(((InvButton) widget).getX());
                    ForgeConfig.setInvButtonVertical(((InvButton) widget).getY());
                    break;
                }
            }
        }
    }
}