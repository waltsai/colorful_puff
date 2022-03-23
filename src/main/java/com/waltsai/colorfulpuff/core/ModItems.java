package com.waltsai.colorfulpuff.core;

import com.waltsai.colorfulpuff.ModInit;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class ModItems {
    public static final Item PUFF_SPAWN_EGG = new SpawnEggItem(ModEntities.PUFF, -1, -1, new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON)) {
        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            stack.getOrCreateNbt().put(EntityType.ENTITY_TAG_KEY, new NbtCompound());
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putInt("Age", 0);
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putBoolean("CanGrow", true);
        }
    };
    public static final Item MINI_PUFF_SPAWN_EGG = new SpawnEggItem(ModEntities.PUFF, -1, -1, new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON)) {
        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            stack.getOrCreateNbt().put(EntityType.ENTITY_TAG_KEY, new NbtCompound());
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putInt("Age", -36000);
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putInt("ForcedAge", 0);
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putBoolean("CanGrow", false);
        }
    };
    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "puff_spawn_egg"), PUFF_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "mini_puff_spawn_egg"), MINI_PUFF_SPAWN_EGG);
        FabricItemGroupBuilder.create(new Identifier(ModInit.MODID, "mod_item")).icon(() -> new ItemStack(ModItems.PUFF_SPAWN_EGG))
                .appendItems(stacks -> {
                    stacks.add(new ItemStack(ModItems.PUFF_SPAWN_EGG));
                    stacks.add(new ItemStack(ModItems.MINI_PUFF_SPAWN_EGG));
                    stacks.add(new ItemStack(ModBlocks.CARAMEL_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.AQUA_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.LIME_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.ICE_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.ORANGE_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.PINK_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.RED_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.YELLOW_CANDY_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.SUGAR_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.GOLD_COOKIE_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.MINT_COOKIE_BLOCK));
                    stacks.add(new ItemStack(ModBlocks.ROSE_COOKIE_BLOCK));
                }).build();
    }
}
