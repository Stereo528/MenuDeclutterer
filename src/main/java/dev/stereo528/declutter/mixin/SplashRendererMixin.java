package dev.stereo528.declutter.mixin;

import dev.stereo528.declutter.client.DeclutterConfig;
import net.minecraft.client.gui.components.SplashRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {
    public SplashRendererMixin() {}


    @ModifyVariable(method = "render", at= @At(value = "STORE"), ordinal = 1)
    private float modify(float value) {

        return value * DeclutterConfig.splashScale;
    }
}
