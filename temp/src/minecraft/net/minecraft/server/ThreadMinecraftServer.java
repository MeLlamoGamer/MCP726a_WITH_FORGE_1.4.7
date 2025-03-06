package net.minecraft.server;

import net.minecraft.server.MinecraftServer;

public class ThreadMinecraftServer extends Thread {

   // $FF: synthetic field
   final MinecraftServer field_82557_a;


   public ThreadMinecraftServer(MinecraftServer p_i5039_1_, String p_i5039_2_) {
      super(p_i5039_2_);
      this.field_82557_a = p_i5039_1_;
   }

   public void run() {
      this.field_82557_a.run();
   }
}
