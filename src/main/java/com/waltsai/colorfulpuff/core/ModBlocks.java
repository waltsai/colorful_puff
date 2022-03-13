package com.waltsai.colorfulpuff.core;

import com.waltsai.colorfulpuff.ModInit;
import com.waltsai.colorfulpuff.block.CandyBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    public static final Material PASTRY = new Material.Builder(MapColor.CLEAR).allowsMovement().build();
    public static final Block CARAMEL_CANDY_BLOCK = new Block(candy());
    public static final Block AQUA_CANDY_BLOCK = new Block(candy());
    public static final Block LIME_CANDY_BLOCK = new Block(candy());
    public static final Block ICE_CANDY_BLOCK = new Block(candy());
    public static final Block ORANGE_CANDY_BLOCK = new Block(candy());
    public static final Block PINK_CANDY_BLOCK = new Block(candy());
    public static final Block RED_CANDY_BLOCK = new Block(candy());
    public static final Block YELLOW_CANDY_BLOCK = new Block(candy());
    public static final Block SUGAR_BLOCK = new Block(candy());
    public static final Block GOLD_COOKIE_BLOCK = new Block(cookie());
    public static final Block MINT_COOKIE_BLOCK = new Block(cookie());
    public static final Block ROSE_COOKIE_BLOCK = new Block(cookie());

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "caramel_candy_block"), ModBlocks.CARAMEL_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "aqua_candy_block"), ModBlocks.AQUA_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "lime_candy_block"), ModBlocks.LIME_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "ice_candy_block"), ModBlocks.ICE_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "orange_candy_block"), ModBlocks.ORANGE_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "pink_candy_block"), ModBlocks.PINK_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "red_candy_block"), ModBlocks.RED_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "yellow_candy_block"), ModBlocks.YELLOW_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "sugar_block"), ModBlocks.SUGAR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "gold_cookie_block"), ModBlocks.GOLD_COOKIE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "mint_cookie_block"), ModBlocks.MINT_COOKIE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ModInit.MODID, "rose_cookie_block"), ModBlocks.ROSE_COOKIE_BLOCK);
    }

    public static void registerBlockItems() {
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "caramel_candy_block"), new BlockItem(ModBlocks.CARAMEL_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "aqua_candy_block"), new BlockItem(ModBlocks.AQUA_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "lime_candy_block"), new BlockItem(ModBlocks.LIME_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "ice_candy_block"), new BlockItem(ModBlocks.ICE_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "orange_candy_block"), new BlockItem(ModBlocks.ORANGE_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "pink_candy_block"), new BlockItem(ModBlocks.PINK_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "red_candy_block"), new BlockItem(ModBlocks.RED_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "yellow_candy_block"), new BlockItem(ModBlocks.YELLOW_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "sugar_block"), new BlockItem(ModBlocks.SUGAR_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "gold_cookie_block"), new BlockItem(ModBlocks.GOLD_COOKIE_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "mint_cookie_block"), new BlockItem(ModBlocks.MINT_COOKIE_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, new Identifier(ModInit.MODID, "rose_cookie_block"), new BlockItem(ModBlocks.ROSE_COOKIE_BLOCK, new Item.Settings()));
    }

    private static AbstractBlock.Settings candy() {
        return AbstractBlock.Settings.of(ModBlocks.PASTRY).mapColor(MapColor.WHITE).strength(0.5f, 0.0f).luminance((blockstate) -> 3);
    }

    private static AbstractBlock.Settings cookie() {
        return AbstractBlock.Settings.of(ModBlocks.PASTRY).mapColor(MapColor.WHITE).strength(0.8f, 0.2f).luminance((blockstate) -> 2);
    }
}
