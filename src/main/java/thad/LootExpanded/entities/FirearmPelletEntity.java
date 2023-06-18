package thad.LootExpanded.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class FirearmPelletEntity extends ThrownItemEntity {
     public FirearmPelletEntity(EntityType<? extends SnowballEntity> entityType, World world) {
      super(entityType, world);
   }

   public FirearmPelletEntity(World world, LivingEntity owner) {
      super(EntityType.SNOWBALL, owner, world);
   }

   public FirearmPelletEntity(World world, double x, double y, double z) {
      super(EntityType.SNOWBALL, x, y, z, world);
   }

   protected Item getDefaultItem() {
      return Items.SNOWBALL;
   }

   private ParticleEffect getParticleParameters() {
      ItemStack itemStack = this.getItem();
      return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
   }

   public void handleStatus(byte status) {
      if (status == 3) {
         ParticleEffect particleEffect = this.getParticleParameters();

         for(int i = 0; i < 8; ++i) {
            this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      Entity entity = entityHitResult.getEntity();
      entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 200);
   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      if (!this.world.isClient) {
         this.world.sendEntityStatus(this, (byte)3);
         this.discard();
      }

   }
}
