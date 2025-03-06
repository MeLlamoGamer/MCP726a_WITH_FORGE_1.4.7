package net.minecraft.client.texturepacks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.texturepacks.TexturePackImplementation;

@SideOnly(Side.CLIENT)
public class TexturePackDefault extends TexturePackImplementation {

   public TexturePackDefault() {
      super("default", "Default");
   }

   protected void func_77540_a() {
      this.field_77546_b = "The default look of Minecraft";
   }
}
