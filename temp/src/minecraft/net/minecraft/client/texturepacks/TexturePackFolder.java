package net.minecraft.client.texturepacks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.texturepacks.TexturePackImplementation;

@SideOnly(Side.CLIENT)
public class TexturePackFolder extends TexturePackImplementation {

   public TexturePackFolder(String p_i3029_1_, File p_i3029_2_) {
      super(p_i3029_1_, p_i3029_2_, p_i3029_2_.getName());
   }

   public InputStream func_77532_a(String p_77532_1_) {
      try {
         File var2 = new File(this.field_77548_a, p_77532_1_.substring(1));
         if(var2.exists()) {
            return new BufferedInputStream(new FileInputStream(var2));
         }
      } catch (IOException var3) {
         ;
      }

      return super.func_77532_a(p_77532_1_);
   }
}
