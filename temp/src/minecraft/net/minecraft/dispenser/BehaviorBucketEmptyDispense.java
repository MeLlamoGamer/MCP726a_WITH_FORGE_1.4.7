package net.minecraft.dispenser;

import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorBucketEmptyDispense extends BehaviorDefaultDispenseItem {

   private final BehaviorDefaultDispenseItem field_92073_c;
   // $FF: synthetic field
   final MinecraftServer field_82310_a;


   public BehaviorBucketEmptyDispense(MinecraftServer p_i5004_1_) {
      this.field_82310_a = p_i5004_1_;
      this.field_92073_c = new BehaviorDefaultDispenseItem();
   }

   public ItemStack func_82487_b(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      EnumFacing var3 = EnumFacing.func_82600_a(p_82487_1_.func_82620_h());
      World var4 = p_82487_1_.func_82618_k();
      int var5 = p_82487_1_.func_82623_d() + var3.func_82601_c();
      int var6 = p_82487_1_.func_82622_e();
      int var7 = p_82487_1_.func_82621_f() + var3.func_82599_e();
      Material var8 = var4.func_72803_f(var5, var6, var7);
      int var9 = var4.func_72805_g(var5, var6, var7);
      Item var10;
      if(Material.field_76244_g.equals(var8) && var9 == 0) {
         var10 = Item.field_77786_ax;
      } else {
         if(!Material.field_76256_h.equals(var8) || var9 != 0) {
            return super.func_82487_b(p_82487_1_, p_82487_2_);
         }

         var10 = Item.field_77775_ay;
      }

      var4.func_72859_e(var5, var6, var7, 0);
      if(--p_82487_2_.field_77994_a == 0) {
         p_82487_2_.field_77993_c = var10.field_77779_bT;
         p_82487_2_.field_77994_a = 1;
      } else if(((TileEntityDispenser)p_82487_1_.func_82619_j()).func_70360_a(new ItemStack(var10)) < 0) {
         this.field_92073_c.func_82482_a(p_82487_1_, new ItemStack(var10));
      }

      return p_82487_2_;
   }
}
