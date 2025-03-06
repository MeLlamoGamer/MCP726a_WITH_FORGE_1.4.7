package net.minecraft.dispenser;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class BehaviorSnowballDispense extends BehaviorProjectileDispense {

   // $FF: synthetic field
   final MinecraftServer field_73716_a;


   public BehaviorSnowballDispense(MinecraftServer p_i5005_1_) {
      this.field_73716_a = p_i5005_1_;
   }

   protected IProjectile func_82499_a(World p_82499_1_, IPosition p_82499_2_) {
      return new EntitySnowball(p_82499_1_, p_82499_2_.func_82615_a(), p_82499_2_.func_82617_b(), p_82499_2_.func_82616_c());
   }
}
