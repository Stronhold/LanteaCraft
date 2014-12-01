package lc.client;

import org.lwjgl.opengl.GL11;

import lc.common.base.LCItemRenderer;
import lc.common.util.game.BlockHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class ItemDecoratorRenderer extends LCItemRenderer {

	private static RenderItem renderItem = new RenderItem();

	@Override
	public LCItemRenderer getParent() {
		return null;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public boolean renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		FontRenderer fontRender = Minecraft.getMinecraft().fontRenderer;
		TextureManager textures = Minecraft.getMinecraft().renderEngine;
		renderItem.renderItemIntoGUI(fontRender, textures, stack, 0, 0);
		if (stack.stackTagCompound == null)
			return true;
		ItemStack blockStack = BlockHelper.loadBlock(stack.stackTagCompound.getString("block-name"));
		if (blockStack == null)
			return true;
		GL11.glPushMatrix();
		GL11.glScalef(0.6f, 0.6f, 0.6f);
		GL11.glTranslatef(3.0f, -8.0f, 0.0f);
		renderItem.renderItemIntoGUI(fontRender, textures, blockStack, 8, 8);
		GL11.glPopMatrix();
		return true;
	}
}
