package com.example.moderationhitbox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Safe moderation demo:
 * - '=' increases a visual expansion value.
 * - '-' decreases it.
 * - The mixin uses this value only for client-side rendering of bounding boxes.
 *
 * It does NOT alter server-side collision, targeting, combat, reach, packets, or real entity dimensions.
 */
public class ModerationHitboxDemoClient implements ClientModInitializer {
    public static final String MOD_ID = "moderation_hitbox_demo";

    private static final double STEP = 0.05D;
    private static final double MIN_EXPANSION = 0.0D;
    private static final double MAX_EXPANSION = 1.0D;

    private static double visualExpansion = 0.0D;

    private static KeyBinding increaseKey;
    private static KeyBinding decreaseKey;

    @Override
    public void onInitializeClient() {
        increaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + MOD_ID + ".increase",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL,
                "category." + MOD_ID
        ));

        decreaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + MOD_ID + ".decrease",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "category." + MOD_ID
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (increaseKey.wasPressed()) {
                adjust(client, STEP);
            }

            while (decreaseKey.wasPressed()) {
                adjust(client, -STEP);
            }
        });
    }

    public static double getVisualExpansion() {
        return visualExpansion;
    }

    private static void adjust(MinecraftClient client, double delta) {
        visualExpansion = clamp(visualExpansion + delta, MIN_EXPANSION, MAX_EXPANSION);

        if (client.player != null) {
            client.player.sendMessage(
                    Text.literal("Demo visual hitbox expansion: +" + String.format(java.util.Locale.ROOT, "%.2f", visualExpansion)),
                    true
            );
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
