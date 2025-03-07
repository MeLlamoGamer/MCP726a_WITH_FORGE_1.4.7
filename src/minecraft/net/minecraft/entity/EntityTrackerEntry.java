package net.minecraft.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet20NamedEntitySpawn;
import net.minecraft.network.packet.Packet23VehicleSpawn;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet25EntityPainting;
import net.minecraft.network.packet.Packet26EntityExpOrb;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet31RelEntityMove;
import net.minecraft.network.packet.Packet32EntityLook;
import net.minecraft.network.packet.Packet33RelEntityMoveLook;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet35EntityHeadRotation;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet40EntityMetadata;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;

public class EntityTrackerEntry
{
    public Entity myEntity;
    public int blocksDistanceThreshold;

    /** check for sync when ticks % updateFrequency==0 */
    public int updateFrequency;
    public int lastScaledXPosition;
    public int lastScaledYPosition;
    public int lastScaledZPosition;
    public int lastYaw;
    public int lastPitch;
    public int lastHeadMotion;
    public double motionX;
    public double motionY;
    public double motionZ;
    public int ticks = 0;
    private double posX;
    private double posY;
    private double posZ;

    /** set to true on first sendLocationToClients */
    private boolean isDataInitialized = false;
    private boolean sendVelocityUpdates;

    /**
     * every 400 ticks a  full teleport packet is sent, rather than just a "move me +x" command, so that position
     * remains fully synced.
     */
    private int ticksSinceLastForcedTeleport = 0;
    private Entity field_85178_v;
    private boolean ridingEntity = false;
    public boolean playerEntitiesUpdated = false;
    public Set trackedPlayers = new HashSet();

    public EntityTrackerEntry(Entity par1Entity, int par2, int par3, boolean par4)
    {
        this.myEntity = par1Entity;
        this.blocksDistanceThreshold = par2;
        this.updateFrequency = par3;
        this.sendVelocityUpdates = par4;
        this.lastScaledXPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.lastScaledYPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.lastScaledZPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.lastYaw = MathHelper.floor_float(par1Entity.rotationYaw * 256.0F / 360.0F);
        this.lastPitch = MathHelper.floor_float(par1Entity.rotationPitch * 256.0F / 360.0F);
        this.lastHeadMotion = MathHelper.floor_float(par1Entity.setRotationYawHead() * 256.0F / 360.0F);
    }

    public boolean equals(Object par1Obj)
    {
        return par1Obj instanceof EntityTrackerEntry ? ((EntityTrackerEntry)par1Obj).myEntity.entityId == this.myEntity.entityId : false;
    }

    public int hashCode()
    {
        return this.myEntity.entityId;
    }

    /**
     * also sends velocity, rotation, and riding info.
     */
    public void sendLocationToAllClients(List par1List)
    {
        this.playerEntitiesUpdated = false;

        if (!this.isDataInitialized || this.myEntity.getDistanceSq(this.posX, this.posY, this.posZ) > 16.0D)
        {
            this.posX = this.myEntity.posX;
            this.posY = this.myEntity.posY;
            this.posZ = this.myEntity.posZ;
            this.isDataInitialized = true;
            this.playerEntitiesUpdated = true;
            this.sendEventsToPlayers(par1List);
        }

        if (this.field_85178_v != this.myEntity.ridingEntity)
        {
            this.field_85178_v = this.myEntity.ridingEntity;
            this.sendPacketToAllTrackingPlayers(new Packet39AttachEntity(this.myEntity, this.myEntity.ridingEntity));
        }

        if (this.myEntity instanceof EntityItemFrame && this.ticks % 10 == 0)
        {
            EntityItemFrame var23 = (EntityItemFrame)this.myEntity;
            ItemStack var24 = var23.getDisplayedItem();

            if (var24 != null && var24.getItem() instanceof ItemMap)
            {
                MapData var26 = Item.map.getMapData(var24, this.myEntity.worldObj);
                Iterator var29 = par1List.iterator();

                while (var29.hasNext())
                {
                    EntityPlayer var30 = (EntityPlayer)var29.next();
                    EntityPlayerMP var31 = (EntityPlayerMP)var30;
                    var26.updateVisiblePlayers(var31, var24);

                    if (var31.playerNetServerHandler.packetSize() <= 5)
                    {
                        Packet var32 = Item.map.createMapDataPacket(var24, this.myEntity.worldObj, var31);

                        if (var32 != null)
                        {
                            var31.playerNetServerHandler.sendPacketToPlayer(var32);
                        }
                    }
                }
            }

            DataWatcher var27 = this.myEntity.getDataWatcher();

            if (var27.hasChanges())
            {
                this.sendPacketToAllAssociatedPlayers(new Packet40EntityMetadata(this.myEntity.entityId, var27, false));
            }
        }
        else if (this.ticks++ % this.updateFrequency == 0 || this.myEntity.isAirBorne)
        {
            int var2;
            int var3;

            if (this.myEntity.ridingEntity == null)
            {
                ++this.ticksSinceLastForcedTeleport;
                var2 = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posX);
                var3 = MathHelper.floor_double(this.myEntity.posY * 32.0D);
                int var4 = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posZ);
                int var5 = MathHelper.floor_float(this.myEntity.rotationYaw * 256.0F / 360.0F);
                int var6 = MathHelper.floor_float(this.myEntity.rotationPitch * 256.0F / 360.0F);
                int var7 = var2 - this.lastScaledXPosition;
                int var8 = var3 - this.lastScaledYPosition;
                int var9 = var4 - this.lastScaledZPosition;
                Object var10 = null;
                boolean var11 = Math.abs(var7) >= 4 || Math.abs(var8) >= 4 || Math.abs(var9) >= 4 || this.ticks % 60 == 0;
                boolean var12 = Math.abs(var5 - this.lastYaw) >= 4 || Math.abs(var6 - this.lastPitch) >= 4;

                if (var7 >= -128 && var7 < 128 && var8 >= -128 && var8 < 128 && var9 >= -128 && var9 < 128 && this.ticksSinceLastForcedTeleport <= 400 && !this.ridingEntity)
                {
                    if (var11 && var12)
                    {
                        var10 = new Packet33RelEntityMoveLook(this.myEntity.entityId, (byte)var7, (byte)var8, (byte)var9, (byte)var5, (byte)var6);
                    }
                    else if (var11)
                    {
                        var10 = new Packet31RelEntityMove(this.myEntity.entityId, (byte)var7, (byte)var8, (byte)var9);
                    }
                    else if (var12)
                    {
                        var10 = new Packet32EntityLook(this.myEntity.entityId, (byte)var5, (byte)var6);
                    }
                }
                else
                {
                    this.ticksSinceLastForcedTeleport = 0;
                    var10 = new Packet34EntityTeleport(this.myEntity.entityId, var2, var3, var4, (byte)var5, (byte)var6);
                }

                if (this.sendVelocityUpdates)
                {
                    double var13 = this.myEntity.motionX - this.motionX;
                    double var15 = this.myEntity.motionY - this.motionY;
                    double var17 = this.myEntity.motionZ - this.motionZ;
                    double var19 = 0.02D;
                    double var21 = var13 * var13 + var15 * var15 + var17 * var17;

                    if (var21 > var19 * var19 || var21 > 0.0D && this.myEntity.motionX == 0.0D && this.myEntity.motionY == 0.0D && this.myEntity.motionZ == 0.0D)
                    {
                        this.motionX = this.myEntity.motionX;
                        this.motionY = this.myEntity.motionY;
                        this.motionZ = this.myEntity.motionZ;
                        this.sendPacketToAllTrackingPlayers(new Packet28EntityVelocity(this.myEntity.entityId, this.motionX, this.motionY, this.motionZ));
                    }
                }

                if (var10 != null)
                {
                    this.sendPacketToAllTrackingPlayers((Packet)var10);
                }

                DataWatcher var33 = this.myEntity.getDataWatcher();

                if (var33.hasChanges())
                {
                    this.sendPacketToAllAssociatedPlayers(new Packet40EntityMetadata(this.myEntity.entityId, var33, false));
                }

                if (var11)
                {
                    this.lastScaledXPosition = var2;
                    this.lastScaledYPosition = var3;
                    this.lastScaledZPosition = var4;
                }

                if (var12)
                {
                    this.lastYaw = var5;
                    this.lastPitch = var6;
                }

                this.ridingEntity = false;
            }
            else
            {
                var2 = MathHelper.floor_float(this.myEntity.rotationYaw * 256.0F / 360.0F);
                var3 = MathHelper.floor_float(this.myEntity.rotationPitch * 256.0F / 360.0F);
                boolean var25 = Math.abs(var2 - this.lastYaw) >= 4 || Math.abs(var3 - this.lastPitch) >= 4;

                if (var25)
                {
                    this.sendPacketToAllTrackingPlayers(new Packet32EntityLook(this.myEntity.entityId, (byte)var2, (byte)var3));
                    this.lastYaw = var2;
                    this.lastPitch = var3;
                }

                this.lastScaledXPosition = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posX);
                this.lastScaledYPosition = MathHelper.floor_double(this.myEntity.posY * 32.0D);
                this.lastScaledZPosition = this.myEntity.myEntitySize.multiplyBy32AndRound(this.myEntity.posZ);
                DataWatcher var28 = this.myEntity.getDataWatcher();

                if (var28.hasChanges())
                {
                    this.sendPacketToAllAssociatedPlayers(new Packet40EntityMetadata(this.myEntity.entityId, var28, false));
                }

                this.ridingEntity = true;
            }

            var2 = MathHelper.floor_float(this.myEntity.setRotationYawHead() * 256.0F / 360.0F);

            if (Math.abs(var2 - this.lastHeadMotion) >= 4)
            {
                this.sendPacketToAllTrackingPlayers(new Packet35EntityHeadRotation(this.myEntity.entityId, (byte)var2));
                this.lastHeadMotion = var2;
            }

            this.myEntity.isAirBorne = false;
        }

        if (this.myEntity.velocityChanged)
        {
            this.sendPacketToAllAssociatedPlayers(new Packet28EntityVelocity(this.myEntity));
            this.myEntity.velocityChanged = false;
        }
    }

    /**
     * if this is a player, then it is not informed
     */
    public void sendPacketToAllTrackingPlayers(Packet par1Packet)
    {
        Iterator var2 = this.trackedPlayers.iterator();

        while (var2.hasNext())
        {
            EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
            var3.playerNetServerHandler.sendPacketToPlayer(par1Packet);
        }
    }

    /**
     * if this is a player, then it recieves the message also
     */
    public void sendPacketToAllAssociatedPlayers(Packet par1Packet)
    {
        this.sendPacketToAllTrackingPlayers(par1Packet);

        if (this.myEntity instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)this.myEntity).playerNetServerHandler.sendPacketToPlayer(par1Packet);
        }
    }

    public void informAllAssociatedPlayersOfItemDestruction()
    {
        Iterator var1 = this.trackedPlayers.iterator();

        while (var1.hasNext())
        {
            EntityPlayerMP var2 = (EntityPlayerMP)var1.next();
            var2.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.entityId));
        }
    }

    public void removeFromWatchingList(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.trackedPlayers.contains(par1EntityPlayerMP))
        {
            par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.entityId));
            this.trackedPlayers.remove(par1EntityPlayerMP);
        }
    }

    /**
     * if the player is more than the distance threshold (typically 64) then the player is removed instead
     */
    public void tryStartWachingThis(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP != this.myEntity)
        {
            double var2 = par1EntityPlayerMP.posX - (double)(this.lastScaledXPosition / 32);
            double var4 = par1EntityPlayerMP.posZ - (double)(this.lastScaledZPosition / 32);

            if (var2 >= (double)(-this.blocksDistanceThreshold) && var2 <= (double)this.blocksDistanceThreshold && var4 >= (double)(-this.blocksDistanceThreshold) && var4 <= (double)this.blocksDistanceThreshold)
            {
                if (!this.trackedPlayers.contains(par1EntityPlayerMP) && this.isPlayerWatchingThisChunk(par1EntityPlayerMP))
                {
                    this.trackedPlayers.add(par1EntityPlayerMP);
                    Packet var6 = this.getPacketForThisEntity();
                    par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(var6);

                    if (!this.myEntity.getDataWatcher().func_92085_d())
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet40EntityMetadata(this.myEntity.entityId, this.myEntity.getDataWatcher(), true));
                    }

                    this.motionX = this.myEntity.motionX;
                    this.motionY = this.myEntity.motionY;
                    this.motionZ = this.myEntity.motionZ;
                    
                    int posX = MathHelper.floor_double(this.myEntity.posX * 32.0D);
                    int posY = MathHelper.floor_double(this.myEntity.posY * 32.0D);
                    int posZ = MathHelper.floor_double(this.myEntity.posZ * 32.0D);
                    if (posX != this.lastScaledXPosition || posY != this.lastScaledYPosition || posZ != this.lastScaledZPosition)
                    {
                        FMLNetworkHandler.makeEntitySpawnAdjustment(this.myEntity.entityId, par1EntityPlayerMP, this.lastScaledXPosition, this.lastScaledYPosition, this.lastScaledZPosition);
                    }

                    if (this.sendVelocityUpdates && !(var6 instanceof Packet24MobSpawn))
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet28EntityVelocity(this.myEntity.entityId, this.myEntity.motionX, this.myEntity.motionY, this.myEntity.motionZ));
                    }

                    if (this.myEntity.ridingEntity != null)
                    {
                        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet39AttachEntity(this.myEntity, this.myEntity.ridingEntity));
                    }

                    if (this.myEntity instanceof EntityLiving)
                    {
                        for (int var7 = 0; var7 < 5; ++var7)
                        {
                            ItemStack var8 = ((EntityLiving)this.myEntity).getCurrentItemOrArmor(var7);

                            if (var8 != null)
                            {
                                par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet5PlayerInventory(this.myEntity.entityId, var7, var8));
                            }
                        }
                    }

                    if (this.myEntity instanceof EntityPlayer)
                    {
                        EntityPlayer var10 = (EntityPlayer)this.myEntity;

                        if (var10.isPlayerSleeping())
                        {
                            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet17Sleep(this.myEntity, 0, MathHelper.floor_double(this.myEntity.posX), MathHelper.floor_double(this.myEntity.posY), MathHelper.floor_double(this.myEntity.posZ)));
                        }
                    }

                    if (this.myEntity instanceof EntityLiving)
                    {
                        EntityLiving var11 = (EntityLiving)this.myEntity;
                        Iterator var12 = var11.getActivePotionEffects().iterator();

                        while (var12.hasNext())
                        {
                            PotionEffect var9 = (PotionEffect)var12.next();
                            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(this.myEntity.entityId, var9));
                        }
                    }
                }
            }
            else if (this.trackedPlayers.contains(par1EntityPlayerMP))
            {
                this.trackedPlayers.remove(par1EntityPlayerMP);
                par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.entityId));
            }
        }
    }

    private boolean isPlayerWatchingThisChunk(EntityPlayerMP par1EntityPlayerMP)
    {
        return par1EntityPlayerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(par1EntityPlayerMP, this.myEntity.chunkCoordX, this.myEntity.chunkCoordZ);
    }

    public void sendEventsToPlayers(List par1List)
    {
        for (int var2 = 0; var2 < par1List.size(); ++var2)
        {
            this.tryStartWachingThis((EntityPlayerMP)par1List.get(var2));
        }
    }

    private Packet getPacketForThisEntity()
    {
        if (this.myEntity.isDead)
        {
            System.out.println("Fetching addPacket for removed entity");
        }

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(this.myEntity);

        if (pkt != null)
        {
            return pkt;
        }

        if (this.myEntity instanceof EntityItem)
        {
            return new Packet23VehicleSpawn(this.myEntity, 2, 1);
        }
        else if (this.myEntity instanceof EntityPlayerMP)
        {
            return new Packet20NamedEntitySpawn((EntityPlayer)this.myEntity);
        }
        else
        {
            if (this.myEntity instanceof EntityMinecart)
            {
                EntityMinecart var1 = (EntityMinecart)this.myEntity;

                if (var1.minecartType == 0)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 10);
                }

                if (var1.minecartType == 1)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 11);
                }

                if (var1.minecartType == 2)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 12);
                }
            }

            if (this.myEntity instanceof EntityBoat)
            {
                return new Packet23VehicleSpawn(this.myEntity, 1);
            }
            else if (!(this.myEntity instanceof IAnimals) && !(this.myEntity instanceof EntityDragon))
            {
                if (this.myEntity instanceof EntityFishHook)
                {
                    EntityPlayer var8 = ((EntityFishHook)this.myEntity).angler;
                    return new Packet23VehicleSpawn(this.myEntity, 90, var8 != null ? var8.entityId : this.myEntity.entityId);
                }
                else if (this.myEntity instanceof EntityArrow)
                {
                    Entity var7 = ((EntityArrow)this.myEntity).shootingEntity;
                    return new Packet23VehicleSpawn(this.myEntity, 60, var7 != null ? var7.entityId : this.myEntity.entityId);
                }
                else if (this.myEntity instanceof EntitySnowball)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 61);
                }
                else if (this.myEntity instanceof EntityPotion)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 73, ((EntityPotion)this.myEntity).getPotionDamage());
                }
                else if (this.myEntity instanceof EntityExpBottle)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 75);
                }
                else if (this.myEntity instanceof EntityEnderPearl)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 65);
                }
                else if (this.myEntity instanceof EntityEnderEye)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 72);
                }
                else if (this.myEntity instanceof EntityFireworkRocket)
                {
                    return new Packet23VehicleSpawn(this.myEntity, 76);
                }
                else
                {
                    Packet23VehicleSpawn var2;

                    if (this.myEntity instanceof EntityFireball)
                    {
                        EntityFireball var6 = (EntityFireball)this.myEntity;
                        var2 = null;
                        byte var3 = 63;

                        if (this.myEntity instanceof EntitySmallFireball)
                        {
                            var3 = 64;
                        }
                        else if (this.myEntity instanceof EntityWitherSkull)
                        {
                            var3 = 66;
                        }

                        if (var6.shootingEntity != null)
                        {
                            var2 = new Packet23VehicleSpawn(this.myEntity, var3, ((EntityFireball)this.myEntity).shootingEntity.entityId);
                        }
                        else
                        {
                            var2 = new Packet23VehicleSpawn(this.myEntity, var3, 0);
                        }

                        var2.speedX = (int)(var6.accelerationX * 8000.0D);
                        var2.speedY = (int)(var6.accelerationY * 8000.0D);
                        var2.speedZ = (int)(var6.accelerationZ * 8000.0D);
                        return var2;
                    }
                    else if (this.myEntity instanceof EntityEgg)
                    {
                        return new Packet23VehicleSpawn(this.myEntity, 62);
                    }
                    else if (this.myEntity instanceof EntityTNTPrimed)
                    {
                        return new Packet23VehicleSpawn(this.myEntity, 50);
                    }
                    else if (this.myEntity instanceof EntityEnderCrystal)
                    {
                        return new Packet23VehicleSpawn(this.myEntity, 51);
                    }
                    else if (this.myEntity instanceof EntityFallingSand)
                    {
                        EntityFallingSand var5 = (EntityFallingSand)this.myEntity;
                        return new Packet23VehicleSpawn(this.myEntity, 70, var5.blockID | var5.metadata << 16);
                    }
                    else if (this.myEntity instanceof EntityPainting)
                    {
                        return new Packet25EntityPainting((EntityPainting)this.myEntity);
                    }
                    else if (this.myEntity instanceof EntityItemFrame)
                    {
                        EntityItemFrame var4 = (EntityItemFrame)this.myEntity;
                        var2 = new Packet23VehicleSpawn(this.myEntity, 71, var4.hangingDirection);
                        var2.xPosition = MathHelper.floor_float((float)(var4.xPosition * 32));
                        var2.yPosition = MathHelper.floor_float((float)(var4.yPosition * 32));
                        var2.zPosition = MathHelper.floor_float((float)(var4.zPosition * 32));
                        return var2;
                    }
                    else if (this.myEntity instanceof EntityXPOrb)
                    {
                        return new Packet26EntityExpOrb((EntityXPOrb)this.myEntity);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Don\'t know how to add " + this.myEntity.getClass() + "!");
                    }
                }
            }
            else
            {
                this.lastHeadMotion = MathHelper.floor_float(this.myEntity.setRotationYawHead() * 256.0F / 360.0F);
                return new Packet24MobSpawn((EntityLiving)this.myEntity);
            }
        }
    }

    public void removePlayerFromTracker(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.trackedPlayers.contains(par1EntityPlayerMP))
        {
            this.trackedPlayers.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.destroyedItemsNetCache.add(Integer.valueOf(this.myEntity.entityId));
        }
    }
}
