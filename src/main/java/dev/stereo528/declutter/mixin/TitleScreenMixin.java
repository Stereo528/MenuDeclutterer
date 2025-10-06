package dev.stereo528.declutter.mixin;

import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.tools.Tool;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow
    private SplashRenderer splash;

    @Shadow
    @Final
    private static Component COPYRIGHT_TEXT;

    @Shadow
    @Nullable
    protected abstract Component getMultiplayerDisabledReason();

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At(value = "HEAD"), cancellable = true)
    private void init(CallbackInfo ci) {
        if (this.splash == null) { // TODO nest this within config check for hidden splash
            this.splash = this.minecraft.getSplashManager().getSplash();
        }
        int copyrightTextWidth = this.font.width(COPYRIGHT_TEXT); // TODO Replace COPYRIGHT_TEXT with config switch between short and long text
        int textOffset = this.width - copyrightTextWidth;  // Maybe just replace this in the call for the text instead of assigning a variable?
        int buttonSpacing = 24;
        int heightPlacement = this.height / 4 + 48;

        if (true) { //TODO replace with config check for small/merged buttons

            //Singleplayer
            this.addRenderableWidget(
                    Button.builder(
                            Component.translatable("menu.singleplayer"),
                            button -> this.minecraft.setScreen(new SelectWorldScreen(this)))
                            .bounds(this.width / 2 - 100, heightPlacement, 98, 20)
                            .build()
            );

            //Online Ban Check
            Component multiplayerDisabledReason = this.getMultiplayerDisabledReason();
            boolean isMultiplayerDisabled = multiplayerDisabledReason == null;
            Tooltip tooltip = multiplayerDisabledReason != null ? Tooltip.create(multiplayerDisabledReason) : null;

            //Multiplayer
            this.addRenderableWidget(
                    Button.builder(
                            Component.translatable("menu.multiplayer"),
                            button -> {
                                Screen screen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
                                this.minecraft.setScreen(screen);
                            })
                            .bounds(this.width / 2 + 2, heightPlacement, 98, 20 )
                            .tooltip(tooltip)
                            .build()
            ).active = isMultiplayerDisabled;

            //Realms
            this.addRenderableWidget(
                    Button.builder(Component.translatable("menu.online"), button -> this.minecraft.setScreen(new RealmsMainScreen(this)))
                            .bounds(this.width / 2 - 100, heightPlacement + buttonSpacing, 98, 20)
                            .tooltip(tooltip)
                            .build()
            ).active = isMultiplayerDisabled;


        }




        ci.cancel();
    }
}
