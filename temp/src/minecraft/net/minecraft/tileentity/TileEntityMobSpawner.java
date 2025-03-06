package net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawnerSpawnData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity {

   public int field_70394_a = -1;
   private String field_70390_d = "Pig";
   private List field_92060_e = null;
   private TileEntityMobSpawnerSpawnData field_70391_e = null;
   public double field_70392_b;
   public double field_70393_c = 0.0D;
   private int field_70388_f = 200;
   private int field_70389_g = 800;
   private int field_70395_h = 4;
   private Entity field_70396_i;
   private int field_82350_j = 6;
   private int field_82349_r = 16;
   private int field_82348_s = 4;


   public TileEntityMobSpawner() {
      this.field_70394_a = 20;
   }

   public String func_70384_a() {
      return this.field_70391_e == null?this.field_70390_d:this.field_70391_e.field_92084_c;
   }

   public void func_70385_a(String p_70385_1_) {
      this.field_70390_d = p_70385_1_;
   }

   public boolean func_70386_b() {
      return this.field_70331_k.func_72977_a((double)this.field_70329_l + 0.5D, (double)this.field_70330_m + 0.5D, (double)this.field_70327_n + 0.5D, (double)this.field_82349_r) != null;
   }

   public void func_70316_g() {
      if(this.func_70386_b()) {
         double var5;
         if(this.field_70331_k.field_72995_K) {
            double var1 = (double)((float)this.field_70329_l + this.field_70331_k.field_73012_v.nextFloat());
            double var3 = (double)((float)this.field_70330_m + this.field_70331_k.field_73012_v.nextFloat());
            var5 = (double)((float)this.field_70327_n + this.field_70331_k.field_73012_v.nextFloat());
            this.field_70331_k.func_72869_a("smoke", var1, var3, var5, 0.0D, 0.0D, 0.0D);
            this.field_70331_k.func_72869_a("flame", var1, var3, var5, 0.0D, 0.0D, 0.0D);
            if(this.field_70394_a > 0) {
               --this.field_70394_a;
            }

            this.field_70393_c = this.field_70392_b;
            this.field_70392_b = (this.field_70392_b + (double)(1000.0F / ((float)this.field_70394_a + 200.0F))) % 360.0D;
         } else {
            if(this.field_70394_a == -1) {
               this.func_70387_f();
            }

            if(this.field_70394_a > 0) {
               --this.field_70394_a;
               return;
            }

            boolean var12 = false;

            for(int var2 = 0; var2 < this.field_70395_h; ++var2) {
               Entity var13 = EntityList.func_75620_a(this.func_70384_a(), this.field_70331_k);
               if(var13 == null) {
                  return;
               }

               int var4 = this.field_70331_k.func_72872_a(var13.getClass(), AxisAlignedBB.func_72332_a().func_72299_a((double)this.field_70329_l, (double)this.field_70330_m, (double)this.field_70327_n, (double)(this.field_70329_l + 1), (double)(this.field_70330_m + 1), (double)(this.field_70327_n + 1)).func_72314_b((double)(this.field_82348_s * 2), 4.0D, (double)(this.field_82348_s * 2))).size();
               if(var4 >= this.field_82350_j) {
                  this.func_70387_f();
                  return;
               }

               if(var13 != null) {
                  var5 = (double)this.field_70329_l + (this.field_70331_k.field_73012_v.nextDouble() - this.field_70331_k.field_73012_v.nextDouble()) * (double)this.field_82348_s;
                  double var7 = (double)(this.field_70330_m + this.field_70331_k.field_73012_v.nextInt(3) - 1);
                  double var9 = (double)this.field_70327_n + (this.field_70331_k.field_73012_v.nextDouble() - this.field_70331_k.field_73012_v.nextDouble()) * (double)this.field_82348_s;
                  EntityLiving var11 = var13 instanceof EntityLiving?(EntityLiving)var13:null;
                  var13.func_70012_b(var5, var7, var9, this.field_70331_k.field_73012_v.nextFloat() * 360.0F, 0.0F);
                  if(var11 == null || var11.func_70601_bi()) {
                     this.func_70383_a(var13);
                     this.field_70331_k.func_72838_d(var13);
                     this.field_70331_k.func_72926_e(2004, this.field_70329_l, this.field_70330_m, this.field_70327_n, 0);
                     if(var11 != null) {
                        var11.func_70656_aK();
                     }

                     var12 = true;
                  }
               }
            }

            if(var12) {
               this.func_70387_f();
            }
         }

         super.func_70316_g();
      }
   }

   public void func_70383_a(Entity p_70383_1_) {
      if(this.field_70391_e != null) {
         NBTTagCompound var2 = new NBTTagCompound();
         p_70383_1_.func_70039_c(var2);
         Iterator var3 = this.field_70391_e.field_92083_b.func_74758_c().iterator();

         while(var3.hasNext()) {
            NBTBase var4 = (NBTBase)var3.next();
            var2.func_74782_a(var4.func_74740_e(), var4.func_74737_b());
         }

         p_70383_1_.func_70020_e(var2);
      } else if(p_70383_1_ instanceof EntityLiving && p_70383_1_.field_70170_p != null) {
         ((EntityLiving)p_70383_1_).func_82163_bD();
      }

   }

   private void func_70387_f() {
      if(this.field_70389_g <= this.field_70388_f) {
         this.field_70394_a = this.field_70388_f;
      } else {
         this.field_70394_a = this.field_70388_f + this.field_70331_k.field_73012_v.nextInt(this.field_70389_g - this.field_70388_f);
      }

      if(this.field_92060_e != null && this.field_92060_e.size() > 0) {
         this.field_70391_e = (TileEntityMobSpawnerSpawnData)WeightedRandom.func_76271_a(this.field_70331_k.field_73012_v, this.field_92060_e);
         this.field_70331_k.func_72845_h(this.field_70329_l, this.field_70330_m, this.field_70327_n);
      }

      this.field_70331_k.func_72965_b(this.field_70329_l, this.field_70330_m, this.field_70327_n, this.func_70311_o().field_71990_ca, 1, 0);
   }

   public void func_70307_a(NBTTagCompound p_70307_1_) {
      super.func_70307_a(p_70307_1_);
      this.field_70390_d = p_70307_1_.func_74779_i("EntityId");
      this.field_70394_a = p_70307_1_.func_74765_d("Delay");
      if(p_70307_1_.func_74764_b("SpawnPotentials")) {
         this.field_92060_e = new ArrayList();
         NBTTagList var2 = p_70307_1_.func_74761_m("SpawnPotentials");

         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            this.field_92060_e.add(new TileEntityMobSpawnerSpawnData(this, (NBTTagCompound)var2.func_74743_b(var3)));
         }
      } else {
         this.field_92060_e = null;
      }

      if(p_70307_1_.func_74764_b("SpawnData")) {
         this.field_70391_e = new TileEntityMobSpawnerSpawnData(this, p_70307_1_.func_74775_l("SpawnData"), this.field_70390_d);
      } else {
         this.field_70391_e = null;
      }

      if(p_70307_1_.func_74764_b("MinSpawnDelay")) {
         this.field_70388_f = p_70307_1_.func_74765_d("MinSpawnDelay");
         this.field_70389_g = p_70307_1_.func_74765_d("MaxSpawnDelay");
         this.field_70395_h = p_70307_1_.func_74765_d("SpawnCount");
      }

      if(p_70307_1_.func_74764_b("MaxNearbyEntities")) {
         this.field_82350_j = p_70307_1_.func_74765_d("MaxNearbyEntities");
         this.field_82349_r = p_70307_1_.func_74765_d("RequiredPlayerRange");
      }

      if(p_70307_1_.func_74764_b("SpawnRange")) {
         this.field_82348_s = p_70307_1_.func_74765_d("SpawnRange");
      }

      if(this.field_70331_k != null && this.field_70331_k.field_72995_K) {
         this.field_70396_i = null;
      }

   }

   public void func_70310_b(NBTTagCompound p_70310_1_) {
      super.func_70310_b(p_70310_1_);
      p_70310_1_.func_74778_a("EntityId", this.func_70384_a());
      p_70310_1_.func_74777_a("Delay", (short)this.field_70394_a);
      p_70310_1_.func_74777_a("MinSpawnDelay", (short)this.field_70388_f);
      p_70310_1_.func_74777_a("MaxSpawnDelay", (short)this.field_70389_g);
      p_70310_1_.func_74777_a("SpawnCount", (short)this.field_70395_h);
      p_70310_1_.func_74777_a("MaxNearbyEntities", (short)this.field_82350_j);
      p_70310_1_.func_74777_a("RequiredPlayerRange", (short)this.field_82349_r);
      p_70310_1_.func_74777_a("SpawnRange", (short)this.field_82348_s);
      if(this.field_70391_e != null) {
         p_70310_1_.func_74766_a("SpawnData", (NBTTagCompound)this.field_70391_e.field_92083_b.func_74737_b());
      }

      if(this.field_70391_e != null || this.field_92060_e != null && this.field_92060_e.size() > 0) {
         NBTTagList var2 = new NBTTagList();
         if(this.field_92060_e != null && this.field_92060_e.size() > 0) {
            Iterator var3 = this.field_92060_e.iterator();

            while(var3.hasNext()) {
               TileEntityMobSpawnerSpawnData var4 = (TileEntityMobSpawnerSpawnData)var3.next();
               var2.func_74742_a(var4.func_92081_a());
            }
         } else {
            var2.func_74742_a(this.field_70391_e.func_92081_a());
         }

         p_70310_1_.func_74782_a("SpawnPotentials", var2);
      }

   }

   @SideOnly(Side.CLIENT)
   public Entity func_70382_c() {
      if(this.field_70396_i == null) {
         Entity var1 = EntityList.func_75620_a(this.func_70384_a(), (World)null);
         this.func_70383_a(var1);
         this.field_70396_i = var1;
      }

      return this.field_70396_i;
   }

   public Packet func_70319_e() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_70310_b(var1);
      var1.func_82580_o("SpawnPotentials");
      return new Packet132TileEntityData(this.field_70329_l, this.field_70330_m, this.field_70327_n, 1, var1);
   }

   public void func_70315_b(int p_70315_1_, int p_70315_2_) {
      if(p_70315_1_ == 1 && this.field_70331_k.field_72995_K) {
         this.field_70394_a = this.field_70388_f;
      }

   }
}
