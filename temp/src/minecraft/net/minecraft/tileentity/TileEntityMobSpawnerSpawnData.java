package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomItem;

class TileEntityMobSpawnerSpawnData extends WeightedRandomItem {

   public final NBTTagCompound field_92083_b;
   public final String field_92084_c;
   // $FF: synthetic field
   final TileEntityMobSpawner field_92082_d;


   public TileEntityMobSpawnerSpawnData(TileEntityMobSpawner p_i8017_1_, NBTTagCompound p_i8017_2_) {
      super(p_i8017_2_.func_74762_e("Weight"));
      this.field_92082_d = p_i8017_1_;
      this.field_92083_b = p_i8017_2_.func_74775_l("Properties");
      this.field_92084_c = p_i8017_2_.func_74779_i("Type");
   }

   public TileEntityMobSpawnerSpawnData(TileEntityMobSpawner p_i8018_1_, NBTTagCompound p_i8018_2_, String p_i8018_3_) {
      super(1);
      this.field_92082_d = p_i8018_1_;
      this.field_92083_b = p_i8018_2_;
      this.field_92084_c = p_i8018_3_;
   }

   public NBTTagCompound func_92081_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74766_a("Properties", this.field_92083_b);
      var1.func_74778_a("Type", this.field_92084_c);
      var1.func_74768_a("Weight", this.field_76292_a);
      return var1;
   }
}
