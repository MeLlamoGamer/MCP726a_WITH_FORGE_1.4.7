package net.minecraft.dispenser;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorPotionDispenseLogic;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class BehaviorPotionDispense implements IBehaviorDispenseItem {

   private final BehaviorDefaultDispenseItem field_82484_c;
   // $FF: synthetic field
   final MinecraftServer field_74272_a;


   public BehaviorPotionDispense(MinecraftServer p_i5008_1_) {
      this.field_74272_a = p_i5008_1_;
      this.field_82484_c = new BehaviorDefaultDispenseItem();
   }

   public ItemStack func_82482_a(IBlockSource p_82482_1_, ItemStack p_82482_2_) {
      return ItemPotion.func_77831_g(p_82482_2_.func_77960_j())?(new BehaviorPotionDispenseLogic(this, p_82482_2_)).func_82482_a(p_82482_1_, p_82482_2_):this.field_82484_c.func_82482_a(p_82482_1_, p_82482_2_);
   }
}
