package com.example.moderationhitbox.mixin;

import com.example.moderationhitbox.ModerationHitboxDemoClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Expands only the Box returned by the renderer.
 * This is intended for visual moderation demos and does not affect server/gameplay hitboxes.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void moderation_hitbox_demo$expandVisualBox(T entity, CallbackInfoReturnable<Box> cir) {
        double margin = ModerationHitboxDemoClient.getVisualExpansion();
        if (margin <= 0.0D) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && entity == client.player) {
            return;
        }

        Box original = cir.getReturnValue();
        if (original == null) {
            return;
        }

        cir.setReturnValue(new Box(
                original.minX - margin,
                original.minY - margin,
                original.minZ - margin,
                original.maxX + margin,
                original.maxY + margin,
                original.maxZ + margin
        ));
    }
}
