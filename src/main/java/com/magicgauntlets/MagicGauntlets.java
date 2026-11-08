package com.magicgauntlets;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MagicGauntlets implements ModInitializer {
    public static final String MOD_ID = "magicgauntlets";
    public static final Map<UUID, Long> COMBO_TIMER = new HashMap<>();

    // 1. 引力手环
    public static final Item GRAVITY_GAUNTLET = new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "gravity_gauntlet"))).maxCount(1)) {
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient) {
                ServerWorld sw = (ServerWorld) world;
                sw.spawnParticles(ParticleTypes.REVERSE_PORTAL, user.getX(), user.getY() + 1, user.getZ(), 100, 3, 3, 3, 0.1);
                sw.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 2.0f, 0.5f);
                List<Entity> entities = sw.getOtherEntities(user, user.getBoundingBox().expand(15.0));
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity living && living != user) {
                        living.requestTeleport(user.getX(), user.getY(), user.getZ());
                    }
                }
                COMBO_TIMER.put(user.getUuid(), System.currentTimeMillis());
                user.sendMessage(Text.literal("🌌 引力场展开！3秒内释放攻击魔法触发连招！").formatted(Formatting.LIGHT_PURPLE), true);
                user.getItemCooldownManager().set(this, 100);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
    };

    // 2. 星爆手环
    public static final Item STARBURST_GAUNTLET = new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "starburst_gauntlet"))).maxCount(1)) {
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient) {
                ServerWorld sw = (ServerWorld) world;
                boolean isCombo = (System.currentTimeMillis() - COMBO_TIMER.getOrDefault(user.getUuid(), 0L)) < 3000;
                if (isCombo) {
                    sw.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, user.getX(), user.getY(), user.getZ(), 20, 2, 2, 2, 0);
                    sw.spawnParticles(ParticleTypes.FLAME, user.getX(), user.getY(), user.getZ(), 500, 3, 3, 3, 1.0);
                    sw.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 5.0f, 1.0f);
                    sw.createExplosion(user, user.getX(), user.getY(), user.getZ(), 6.0f, false, World.ExplosionSourceType.MOB);
                    user.sendMessage(Text.literal("🔥 COMBO: 超新星爆发！").formatted(Formatting.GOLD, Formatting.BOLD), true);
                    COMBO_TIMER.remove(user.getUuid());
                } else {
                    sw.spawnParticles(ParticleTypes.EXPLOSION, user.getX(), user.getY(), user.getZ(), 5, 1, 1, 1, 0);
                    sw.createExplosion(user, user.getX(), user.getY(), user.getZ(), 3.0f, false, World.ExplosionSourceType.MOB);
                    user.sendMessage(Text.literal("💥 星爆！").formatted(Formatting.RED), true);
                }
                user.getItemCooldownManager().set(this, 60);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
    };

    // 3. 寒冰手环
    public static final Item CRYO_GAUNTLET = new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "cryo_gauntlet"))).maxCount(1)) {
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient) {
                ServerWorld sw = (ServerWorld) world;
                boolean isCombo = (System.currentTimeMillis() - COMBO_TIMER.getOrDefault(user.getUuid(), 0L)) < 3000;
                int duration = isCombo ? 400 : 200;
                int amp = isCombo ? 4 : 2;
                sw.spawnParticles(ParticleTypes.SNOWFLAKE, user.getX(), user.getY() + 1, user.getZ(), isCombo ? 1000 : 200, 10, 5, 10, 0.1);
                sw.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 3.0f, 0.5f);
                List<Entity> entities = sw.getOtherEntities(user, user.getBoundingBox().expand(isCombo ? 20.0 : 10.0));
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity living) {
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, amp));
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration, amp));
                        living.damage(sw, sw.getDamageSources().magic(), isCombo ? 10.0f : 5.0f);
                    }
                }
                user.sendMessage(Text.literal(isCombo ? "🧊 COMBO: 绝对零度！" : "❄️ 寒冰领域！").formatted(Formatting.AQUA, isCombo ? Formatting.BOLD : Formatting.RESET), true);
                if (isCombo) COMBO_TIMER.remove(user.getUuid());
                user.getItemCooldownManager().set(this, 100);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
    };

    // 4. 狂雷手环
    public static final Item THUNDER_GAUNTLET = new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "thunder_gauntlet"))).maxCount(1)) {
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient) {
                ServerWorld sw = (ServerWorld) world;
                boolean isCombo = (System.currentTimeMillis() - COMBO_TIMER.getOrDefault(user.getUuid(), 0L)) < 3000;
                double range = isCombo ? 25.0 : 15.0;
                List<Entity> entities = sw.getOtherEntities(user, user.getBoundingBox().expand(range));
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity) {
                        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(sw, SpawnReason.TRIGGERED);
                        if (lightning != null) {
                            lightning.refreshPositionAfterTeleport(entity.getPos());
                            sw.spawnEntity(lightning);
                            if (isCombo) entity.damage(sw, sw.getDamageSources().lightningBolt(), 20.0f);
                        }
                    }
                }
                sw.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 5.0f, 1.0f);
                user.sendMessage(Text.literal(isCombo ? "⚡ COMBO: 天罚雷狱！" : "🌩️ 雷霆万钧！").formatted(Formatting.YELLOW, isCombo ? Formatting.BOLD : Formatting.RESET), true);
                if (isCombo) COMBO_TIMER.remove(user.getUuid());
                user.getItemCooldownManager().set(this, 120);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
    };

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "gravity_gauntlet"), GRAVITY_GAUNTLET);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "starburst_gauntlet"), STARBURST_GAUNTLET);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "cryo_gauntlet"), CRYO_GAUNTLET);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "thunder_gauntlet"), THUNDER_GAUNTLET);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(GRAVITY_GAUNTLET);
            content.add(STARBURST_GAUNTLET);
            content.add(CRYO_GAUNTLET);
            content.add(THUNDER_GAUNTLET);
        });
    }
}
