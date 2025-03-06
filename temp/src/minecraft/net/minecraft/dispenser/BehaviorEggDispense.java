package net.minecraft.dispenser;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class BehaviorEggDispense extends BehaviorProjectileDispense {

   // $FF: synthetic field
   final MinecraftServer field_82503_b;


   public BehaviorEggDispense(MinecraftServer p_i5044_1_) {
      this.field_82503_b = p_i5044_1_;
   }

   protected IProjectile func_82499_a(World p_82499_1_, IPosition p_82499_2_) {
      return new EntityEgg(p_82499_1_, p_82499_2_.func_82615_a(), p_82499_2_.func_82617_b(), p_82499_2_.func_82616_c());
   }
}
