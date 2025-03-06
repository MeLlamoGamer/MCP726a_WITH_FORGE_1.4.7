package net.minecraft.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.dedicated.DedicatedServer;

@SideOnly(Side.SERVER)
public final class ThreadDedicatedServer extends Thread {

   // $FF: synthetic field
   final DedicatedServer field_92021_a;


   public ThreadDedicatedServer(DedicatedServer p_i4100_1_) {
      this.field_92021_a = p_i4100_1_;
   }

   public void run() {
      this.field_92021_a.func_71260_j();
   }
}
