package thad.LootExpanded.items.weapons;


import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import thad.LootExpanded.LootExpanded;
import thad.LootExpanded.ThadHelperMethods;
import thad.LootExpanded.entities.FirearmPelletEntity;

public class FlintlockPistolItem extends RangedWeaponItem implements Vanishable {
   public static final int RANGE = 12;
   private boolean charged = false;
   private boolean loaded = false;
   private static final float DEFAULT_SPEED = 10F;
   

   public FlintlockPistolItem(Settings settings) {
      super(settings);
   }

   public Predicate<ItemStack> getHeldProjectiles() {
      return CROSSBOW_HELD_PROJECTILES;
   }

   public Predicate<ItemStack> getProjectiles() {
      return BOW_PROJECTILES;
   }

   public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getStackInHand(hand);
      if (isCharged(itemStack)) {
         shootAll(world, user, hand, itemStack, DEFAULT_SPEED, 1.0F,user);
         setCharged(itemStack, false);
         return TypedActionResult.consume(itemStack);
      } else if (user.getInventory().count(LootExpanded.FIREARM_PELLET)>0) {
         if (!isCharged(itemStack)) {
            this.charged = false;
            this.loaded = false;
            user.setCurrentHand(hand);
         }

         return TypedActionResult.consume(itemStack);
      } else {
         return TypedActionResult.fail(itemStack);
      }
   }

   public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
      int i = this.getMaxUseTime(stack) - remainingUseTicks;
      float f = getPullProgress(i, stack);
      if (f >= .6F && !isCharged(stack) && loadProjectiles(user, stack)) {
         setCharged(stack, true);
         SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
         world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
      }

   }

   private static boolean loadProjectiles(LivingEntity shooter, ItemStack weapon) {
      int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, weapon);
      boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;

      ItemStack itemStack = ThadHelperMethods.FindStackInInventory(((PlayerEntity)shooter).getInventory(),LootExpanded.FIREARM_PELLET);
               if (!loadProjectile(shooter, weapon, itemStack, bl)) {
            return false;
         }
      return true;
   }

   private static boolean loadProjectile(LivingEntity shooter, ItemStack weapon, ItemStack projectile, boolean creative) {
      if (projectile.isEmpty()) {
         return false;
      } else {
         boolean bl = creative && projectile.getItem() instanceof Item;
         ItemStack itemStack;
         if (!bl && !creative) {
            itemStack = projectile.split(1);
            if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
               ((PlayerEntity)shooter).getInventory().removeOne(projectile);
            }
         } else {
            itemStack = projectile.copy();
         }

         return true;
      }
   }

   public static boolean isCharged(ItemStack stack) {
      NbtCompound nbtCompound = stack.getNbt();
      return nbtCompound != null && nbtCompound.getBoolean("Charged");
   }

   public static void setCharged(ItemStack stack, boolean charged) {
      NbtCompound nbtCompound = stack.getOrCreateNbt();
      nbtCompound.putBoolean("Charged", charged);
   }

   

   private static void clearProjectiles(ItemStack crossbow) {
      NbtCompound nbtCompound = crossbow.getNbt();
      if (nbtCompound != null) {
         NbtList nbtList = nbtCompound.getList("ChargedProjectiles", 9);
         nbtList.clear();
         nbtCompound.put("ChargedProjectiles", nbtList);
      }

   }

   private static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow) {
      ArrowItem arrowItem = (ArrowItem)(arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
      PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrow, entity);
      if (entity instanceof PlayerEntity) {
         persistentProjectileEntity.setCritical(true);
      }

      persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
      persistentProjectileEntity.setShotFromCrossbow(true);
      int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
      if (i > 0) {
         persistentProjectileEntity.setPierceLevel((byte)i);
      }

      return persistentProjectileEntity;
   }

   public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence,LivingEntity user) {
      //float[] fs = getSoundPitches(entity.getRandom());
      //boolean bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
      FirearmPelletEntity firearmPellet = new FirearmPelletEntity(world, user);
      firearmPellet.setItem(new ItemStack(LootExpanded.FIREARM_PELLET, 1));
      firearmPellet.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F,speed, 0.0F);
      world.spawnEntity(firearmPellet);
      world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), LootExpanded.FLINTLOCK_SHOT_EVENT, SoundCategory.PLAYERS, 0.5F, 5.0F);
      postShoot(world, entity, stack);
   }

   private static float[] getSoundPitches(Random random) {
      boolean bl = random.nextBoolean();
      return new float[]{1.0F, getSoundPitch(bl, random), getSoundPitch(!bl, random)};
   }

   private static float getSoundPitch(boolean flag, Random random) {
      float f = flag ? 0.63F : 0.43F;
      return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
   }

   private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
         if (!world.isClient) {
            Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
         }

         serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
      }

      clearProjectiles(stack);
   }

   public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
      if (!world.isClient) {
         int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
         SoundEvent soundEvent = this.getQuickChargeSound(i);
         SoundEvent soundEvent2 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
         float f = (float)(stack.getMaxUseTime() - remainingUseTicks) / (float)getPullTime(stack);
         if (f < 0.2F) {
            this.charged = false;
            this.loaded = false;
         }

         if (f >= 0.2F && !this.charged) {
            this.charged = true;
            world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), soundEvent, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }

         if (f >= 0.5F && soundEvent2 != null && !this.loaded) {
            this.loaded = true;
            world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), soundEvent2, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }
      }

   }

   public int getMaxUseTime(ItemStack stack) {
      return getPullTime(stack) + 3;
   }

   public static int getPullTime(ItemStack stack) {
      int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
      return i == 0 ? 20 : 20 - 5 * i;
   }

   public UseAction getUseAction(ItemStack stack) {
      return UseAction.CROSSBOW;
   }

   private SoundEvent getQuickChargeSound(int stage) {
      switch(stage) {
      case 1:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
      case 2:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
      case 3:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
      default:
         return SoundEvents.ITEM_CROSSBOW_LOADING_START;
      }
   }

   private static float getPullProgress(int useTicks, ItemStack stack) {
      float f = (float)useTicks / (float)getPullTime(stack);
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }


   public boolean isUsedOnRelease(ItemStack stack) {
      return stack.isOf(this);
   }

   public int getRange() {
      return RANGE;
   }
}
