package dev.lumyrix.quickmove.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow protected ScreenHandler handler;

    // -------------------------------------------------------
    // To change the quick move key, edit the two GLFW constants
    // below to any key from https://www.glfw.org/docs/latest/group__keys.html
    // Current key: Left Control (GLFW_KEY_LEFT_CONTROL)
    // Examples:
    //   GLFW.GLFW_KEY_LEFT_ALT   → Left Alt
    //   GLFW.GLFW_KEY_CAPS_LOCK  → Caps Lock
    //   GLFW.GLFW_KEY_R          → R key
    // -------------------------------------------------------
    private static final int QUICK_MOVE_KEY_1 = GLFW.GLFW_KEY_LEFT_CONTROL;
    private static final int QUICK_MOVE_KEY_2 = GLFW.GLFW_KEY_RIGHT_CONTROL;

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            at = @At("HEAD"), cancellable = true)
    private void quickmove$ctrl(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) return;

        long window = client.getWindow().getHandle();
        boolean quickMovePressed = GLFW.glfwGetKey(window, QUICK_MOVE_KEY_1) == GLFW.GLFW_PRESS
                                || GLFW.glfwGetKey(window, QUICK_MOVE_KEY_2) == GLFW.GLFW_PRESS;
        boolean shift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT)  == GLFW.GLFW_PRESS
                     || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

        if (quickMovePressed && actionType == SlotActionType.PICKUP) {
            client.interactionManager.clickSlot(handler.syncId, slotId, button, SlotActionType.QUICK_MOVE, client.player);
            ci.cancel();
        } else if (shift && actionType == SlotActionType.QUICK_MOVE) {
            client.interactionManager.clickSlot(handler.syncId, slotId, button, SlotActionType.PICKUP, client.player);
            ci.cancel();
        }
    }
}
