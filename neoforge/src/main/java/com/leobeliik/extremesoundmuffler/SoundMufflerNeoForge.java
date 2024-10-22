package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import static com.leobeliik.extremesoundmuffler.Constants.soundMufflerKey;

@Mod(Constants.MOD_ID)
public class SoundMufflerNeoForge {

    public SoundMufflerNeoForge(IEventBus modEventBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(this);
        NeoForgeConfig.init(container);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientModListener {
        @SubscribeEvent
        public static void keyRegistry(final RegisterKeyMappingsEvent event) {
            event.register(soundMufflerKey);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent //on mod keybind press
    public void onKeyInput(InputEvent.Key event) {
        if (soundMufflerKey.consumeClick()) {
            SoundMufflerCommon.openMainScreen();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent //load data when player joins the world
    public void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        DataManager.loadData();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent //save the new coordinates for the inv button
    public void onMouseRelease(ScreenEvent.MouseButtonReleased.Post event) {
        if (event.getButton() == 1) {
            Screen screen = event.getScreen();
            for (GuiEventListener widget : screen.children()) {
                if (widget instanceof InvButton btn && btn.isHovered()) {
                    if (screen instanceof CreativeModeInventoryScreen) {
                        NeoForgeConfig.setCreativeInvButtonHorizontal(btn.getX());
                        NeoForgeConfig.setCreativeInvButtonVertical(btn.getY());
                    } else {
                        NeoForgeConfig.setInvButtonHorizontal(btn.getX());
                        NeoForgeConfig.setInvButtonVertical(btn.getY());
                    }
                    break;
                }
            }
        }
    }
}