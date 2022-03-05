package com.chillycoffey.colorfulpuff.core;

import com.chillycoffey.colorfulpuff.ModInit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModTags {
    public static final TagKey<Block> PUFF_REPELLENTS = TagKey.of(Registry.BLOCK_KEY, new Identifier("minecraft:puff_repellents"));
    public static final TagKey<Item> VIVID_PUFF_TEMPTED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:vivid_puff_tempted_item"));
    public static final TagKey<Item> DIGNIFIED_PUFF_TEMPTED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:dignified_puff_tempted_item"));
    public static final TagKey<Item> TIMID_PUFF_TEMPTED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:timid_puff_tempted_item"));
    public static final TagKey<Item> VIVID_PUFF_TAMED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:vivid_puff_tamed_item"));
    public static final TagKey<Item> DIGNIFIED_PUFF_TAMED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:dignified_puff_tamed_item"));
    public static final TagKey<Item> TIMID_PUFF_TAMED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:timid_puff_tamed_item"));
    public static final TagKey<Item> VIVID_PUFF_BREED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:vivid_puff_breed_item"));
    public static final TagKey<Item> DIGNIFIED_PUFF_BREED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:dignified_puff_breed_item"));
    public static final TagKey<Item> TIMID_PUFF_BREED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:timid_puff_breed_item"));
}
