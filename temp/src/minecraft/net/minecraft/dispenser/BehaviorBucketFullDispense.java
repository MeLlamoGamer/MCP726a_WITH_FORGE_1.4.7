package net.minecraft.dispenser;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;

public class BehaviorBucketFullDispense extends BehaviorDefaultDispenseItem {

   private final BehaviorDefaultDispenseItem field_82497_c;
   // $FF: synthetic field
   final MinecraftServer field_82496_b;


   public BehaviorBucketFullDispense(MinecraftServer p_i5003_1_) {
      this.field_82496_b = p_i5003_1_;
      this.field_82497_c = new BehaviorDefaultDispenseItem();
   }

   public ItemStack func_82487_b(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      ItemBucket var3 = (ItemBucket)p_82487_2_.func_77973_b();
      int var4 = p_82487_1_.func_82623_d();
      int var5 = p_82487_1_.func_82622_e();
      int var6 = p_82487_1_.func_82621_f();
      EnumFacing var7 = EnumFacing.func_82600_a(p_82487_1_.func_82620_h());
      if(var3.func_77875_a(p_82487_1_.func_82618_k(), (double)var4, (double)var5, (double)var6, var4 + var7.func_82601_c(), var5, var6 + var7.func_82599_e())) {
         p_82487_2_.field_77993_c = Item.field_77788_aw.field_77779_bT;
         p_82487_2_.field_77994_a = 1;
         return p_82487_2_;
      } else {
         return this.field_82497_c.func_82482_a(p_82487_1_, p_82487_2_);
      }
   }
}
