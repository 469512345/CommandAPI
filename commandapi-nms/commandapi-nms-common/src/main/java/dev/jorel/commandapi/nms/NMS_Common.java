/*******************************************************************************
 * Copyright 2018, 2021 Jorel Ali (Skepter) - MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dev.jorel.commandapi.nms;

import static dev.jorel.commandapi.preprocessor.Unimplemented.REASON.NAME_CHANGED;
import static dev.jorel.commandapi.preprocessor.Unimplemented.REASON.REQUIRES_CRAFTBUKKIT;
import static dev.jorel.commandapi.preprocessor.Unimplemented.REASON.REQUIRES_CSS;
import static dev.jorel.commandapi.preprocessor.Unimplemented.REASON.REQUIRES_MINECRAFT_SERVER;
import static dev.jorel.commandapi.preprocessor.Unimplemented.REASON.VERSION_SPECIFIC_IMPLEMENTATION;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import dev.jorel.commandapi.CommandAPIHandler;
import dev.jorel.commandapi.arguments.EntitySelector;
import dev.jorel.commandapi.arguments.SuggestionProviders;
import dev.jorel.commandapi.preprocessor.Unimplemented;
import dev.jorel.commandapi.wrappers.FloatRange;
import dev.jorel.commandapi.wrappers.FunctionWrapper;
import dev.jorel.commandapi.wrappers.IntegerRange;
import dev.jorel.commandapi.wrappers.Location2D;
import dev.jorel.commandapi.wrappers.MathOperation;
import dev.jorel.commandapi.wrappers.ParticleData;
import dev.jorel.commandapi.wrappers.Rotation;
import dev.jorel.commandapi.wrappers.ScoreboardSlot;
import dev.jorel.commandapi.wrappers.SimpleFunctionWrapper;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentProfile;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

/**
 * Common NMS code To ensure that this code actually works across all versions
 * of Minecraft that this is supposed to support (1.17+), you should be
 * compiling this code against all of the declared Maven profiles specified in
 * this submodule's pom.xml file, by running the following commands:
 * <ul>
 * <li><code>mvn clean package -pl :commandapi-nms-common -P Spigot_1_19_R1</code></li>
 * <li><code>mvn clean package -pl :commandapi-nms-common -P Spigot_1_18_2_R2</code></li>
 * <li><code>mvn clean package -pl :commandapi-nms-common -P Spigot_1_18_R1</code></li>
 * <li><code>mvn clean package -pl :commandapi-nms-common -P Spigot_1_17_R1</code></li>
 * </ul>
 * Any of these that do not work should be removed or implemented otherwise
 * (introducing another NMS_Common module perhaps?
 */
@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public abstract class NMS_Common implements NMS<CommandSourceStack> {

	@Override
	public final ArgumentType<?> _ArgumentAngle() {
		return AngleArgument.angle();
	}

	@Override
	public final ArgumentType<?> _ArgumentAxis() {
		return SwizzleArgument.swizzle();
	}

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION, introducedIn = "1.19")
	public abstract ArgumentType<?> _ArgumentBlockPredicate();

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION, introducedIn = "1.19")
	public abstract ArgumentType<?> _ArgumentBlockState();

	@Override
	public final ArgumentType<?> _ArgumentChat() {
		return MessageArgument.message();
	}

	@Override
	public final ArgumentType<?> _ArgumentChatComponent() {
		return ComponentArgument.textComponent();
	}

	@Override
	public final ArgumentType<?> _ArgumentChatFormat() {
		return ColorArgument.color();
	}

	@Override
	public final ArgumentType<?> _ArgumentDimension() {
		return DimensionArgument.dimension();
	}

	@Override
	public final ArgumentType<?> _ArgumentEnchantment() {
		return ItemEnchantmentArgument.enchantment();
	}

	@Override
	// TODO:
	@Unimplemented(because = NAME_CHANGED, from = "??", to = "b")
	public abstract ArgumentType<?> _ArgumentEntity(EntitySelector selector);

	@Override
	public final ArgumentType<?> _ArgumentEntitySummon() {
		return EntitySummonArgument.id();
	}

	@Override
	public final ArgumentType<?> _ArgumentFloatRange() {
		return RangeArgument.floatRange();
	}

	@Override
	public final ArgumentType<?> _ArgumentIntRange() {
		return RangeArgument.intRange();
	}

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION, introducedIn = "1.19")
	public abstract ArgumentType<?> _ArgumentItemPredicate();

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION, introducedIn = "1.19")
	public abstract ArgumentType<?> _ArgumentItemStack();

	@Override
	public final ArgumentType<?> _ArgumentMathOperation() {
		return OperationArgument.operation();
	}

	@Override
	public final ArgumentType<?> _ArgumentMinecraftKeyRegistered() {
		return ResourceLocationArgument.id();
	}

	@Override
	public final ArgumentType<?> _ArgumentMobEffect() {
		return MobEffectArgument.effect();
	}

	@Override
	public final ArgumentType<?> _ArgumentNBTCompound() {
		return CompoundTagArgument.compoundTag();
	}

	@Override
	public ArgumentType<?> _ArgumentParticle() {
		return ParticleArgument.particle();
	}

	@Override
	public ArgumentType<?> _ArgumentPosition() {
		return BlockPosArgument.blockPos();
	}

	@Override
	public ArgumentType<?> _ArgumentPosition2D() {
		return ColumnPosArgument.columnPos();
	}

	@Override
	public ArgumentType<?> _ArgumentProfile() {
		return GameProfileArgument.gameProfile();
	}

	@Override
	public ArgumentType<?> _ArgumentRotation() {
		return RotationArgument.rotation();
	}

	@Override
	public ArgumentType<?> _ArgumentScoreboardCriteria() {
		return ObjectiveCriteriaArgument.criteria();
	}

	@Override
	public ArgumentType<?> _ArgumentScoreboardObjective() {
		return ObjectiveArgument.objective();
	}

	@Override
	public ArgumentType<?> _ArgumentScoreboardSlot() {
		return ScoreboardSlotArgument.displaySlot();
	}

	@Override
	public ArgumentType<?> _ArgumentScoreboardTeam() {
		return TeamArgument.team();
	}

	@Override
	public ArgumentType<?> _ArgumentScoreholder(boolean single) {
		return single ? ScoreHolderArgument.scoreHolder() : ScoreHolderArgument.scoreHolders();
	}

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION, introducedIn = "1.18.2")
	public abstract ArgumentType<?> _ArgumentSyntheticBiome();

	@Override
	public ArgumentType<?> _ArgumentTag() {
		return FunctionArgument.functions();
	}

	@Override
	public ArgumentType<?> _ArgumentTime() {
		return TimeArgument.time();
	}

	@Override
	public ArgumentType<?> _ArgumentUUID() {
		return UuidArgument.uuid();
	}

	@Override
	public ArgumentType<?> _ArgumentVec2() {
		return Vec2Argument.vec2();
	}

	@Override
	public ArgumentType<?> _ArgumentVec3() {
		return Vec3Argument.vec3();
	}

	@Override
	public final BaseComponent[] getChat(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return ComponentSerializer.parse(Serializer.toJson(MessageArgument.getMessage(cmdCtx, key)));
	}

	@Override
	public final BaseComponent[] getChatComponent(CommandContext<CommandSourceStack> cmdCtx, String str) {
		return ComponentSerializer.parse(Serializer.toJson(ComponentArgument.getComponent(cmdCtx, str)));
	}

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "SimpleHelpMap")
	public abstract void addToHelpMap(Map<String, HelpTopic> helpTopicsToAdd);

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION)
	public abstract String[] compatibleVersions();

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "CraftItemStack")
	public abstract String convert(ItemStack is);

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION)
	public abstract String convert(ParticleData<?> particle);

	@Override
	public final String convert(PotionEffectType potion) {
		return potion.getName().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public final String convert(Sound sound) {
		return sound.getKey().toString();
	}

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION, introducedIn = "1.19")
	public abstract void createDispatcherFile(File file, CommandDispatcher<CommandSourceStack> dispatcher) throws IOException;

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "CustomHelpTopic")
	public abstract HelpTopic generateHelpTopic(String commandName, String shortDescription, String fullDescription,
		String permission);

	@Override
	public final org.bukkit.advancement.Advancement getAdvancement(CommandContext<CommandSourceStack> cmdCtx, String key)
		throws CommandSyntaxException {
		return ResourceLocationArgument.getAdvancement(cmdCtx, key).bukkit;
	}

	@Override
	public final float getAngle(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return AngleArgument.getAngle(cmdCtx, key);
	}

	@Override
	public final EnumSet<Axis> getAxis(CommandContext<CommandSourceStack> cmdCtx, String key) {
		EnumSet<Axis> set = EnumSet.noneOf(Axis.class);
		EnumSet<net.minecraft.core.Direction.Axis> parsedEnumSet = SwizzleArgument.getSwizzle(cmdCtx, key);
		for (net.minecraft.core.Direction.Axis element : parsedEnumSet) {
			set.add(switch (element) {
				case X -> Axis.X;
				case Y -> Axis.Y;
				case Z -> Axis.Z;
			});
		}
		return set;
	}

	@Override
	public abstract CommandSourceStack getCLWFromCommandSender(CommandSender sender);

	@Override
	public final Environment getDimension(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return DimensionArgument.getDimension(cmdCtx, key).getWorld().getEnvironment();
	}

	@Override
	public final FloatRange getFloatRange(CommandContext<CommandSourceStack> cmdCtx, String key) {
		MinMaxBounds.Doubles range = RangeArgument.Floats.getRange(cmdCtx, key);
		double low = range.getMin() == null ? -Float.MAX_VALUE : range.getMin();
		double high = range.getMax() == null ? Float.MAX_VALUE : range.getMax();
		return new FloatRange((float) low, (float) high);
	}

	@Override
	public final IntegerRange getIntRange(CommandContext<CommandSourceStack> cmdCtx, String key) {
		MinMaxBounds.Ints range = RangeArgument.Ints.getRange(cmdCtx, key);
		int low = range.getMin() == null ? Integer.MIN_VALUE : range.getMin();
		int high = range.getMax() == null ? Integer.MAX_VALUE : range.getMax();
		return new IntegerRange(low, high);
	}

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "CraftItemStack")
	public abstract ItemStack getItemStack(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	public final MathOperation getMathOperation(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		// We run this to ensure the argument exists/parses properly
		OperationArgument.getOperation(cmdCtx, key);
		return MathOperation.fromString(CommandAPIHandler.getRawArgumentInput(cmdCtx, key));
	}

	@Override
	public NamespacedKey getMinecraftKey(CommandContext<CommandSourceStack> cmdCtx, String key) {
		MinecraftKey resourceLocation = ArgumentMinecraftKeyRegistered.e(cmdCtx, key);
		return new NamespacedKey(resourceLocation.b(), resourceLocation.a());
	}

	@Override
	public <NBTContainer> Object getNBTCompound(CommandContext<CommandSourceStack> cmdCtx, String key,
		Function<Object, NBTContainer> nbtContainerConstructor) {
		return nbtContainerConstructor.apply(CompoundTagArgument.getCompoundTag(cmdCtx, key));
	}

	@Override
	public final Rotation getRotation(CommandContext<CommandSourceStack> cmdCtx, String key) {
		Vec2 rotation = RotationArgument.getRotation(cmdCtx, key).getRotation(cmdCtx.getSource());
		return new Rotation(rotation.x, rotation.y);
	}

	@Override
	public final ScoreboardSlot getScoreboardSlot(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return new ScoreboardSlot(ScoreboardSlotArgument.getDisplaySlot(cmdCtx, key));
	}

	@Override
	public final Collection<String> getScoreHolderMultiple(CommandContext<CommandSourceStack> cmdCtx, String key)
		throws CommandSyntaxException {
		return ScoreHolderArgument.getNames(cmdCtx, key);
	}

	@Override
	public final String getScoreHolderSingle(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return ScoreHolderArgument.getName(cmdCtx, key);
	}

	@Override
	public final int getTime(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return (int) cmdCtx.getArgument(key, Integer.class);
	}

	@Override
	public final UUID getUUID(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return UuidArgument.getUuid(cmdCtx, key);
	}

	@Override
	public abstract Component getAdventureChat(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	public abstract Component getAdventureChatComponent(CommandContext<CommandSourceStack> cmdCtx, String key);

	@Override
	public abstract Biome getBiome(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = NAME_CHANGED, from = "getWorld()", to = "f()", in = "1.19")
	public abstract Predicate<Block> getBlockPredicate(CommandContext<CommandSourceStack> cmdCtx, String key)
		throws CommandSyntaxException;

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "CraftBlockData")
	public abstract BlockData getBlockState(CommandContext<CommandSourceStack> cmdCtx, String key);

	@Override
	@Unimplemented(because = REQUIRES_MINECRAFT_SERVER)
	public abstract CommandDispatcher<T> getBrigadierDispatcher();

	
	@Override
	public ChatColor getChatColor(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return ChatColor.getByChar(ColorArgument.getColor(cmdCtx, key).code);
	}

	@Override
	@Unimplemented(because = REQUIRES_CSS)
	public abstract CommandSender getCommandSenderFromCSS(T clw);

	@Override
	public Enchantment getEnchantment(CommandContext<CommandSourceStack> cmdCtx, String key) {
		/* TODO: Requires testing */
		ResourceLocation enchantment = Registry.ENCHANTMENT.getKey(ItemEnchantmentArgument.getEnchantment(cmdCtx, key));
		return Enchantment.getByKey(NamespacedKey.fromString(enchantment.getNamespace() + ":" + enchantment.getPath()));
	}

	@Override
	public abstract Object getEntitySelector(CommandContext<CommandSourceStack> cmdCtx, String key, EntitySelector selector)
		throws CommandSyntaxException;

	@Override
	@Unimplemented(because = NAME_CHANGED, from = "getKey()", to = "a()", in = "1.19")
	public abstract EntityType getEntityType(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = REQUIRES_CSS)
	public abstract FunctionWrapper[] getFunction(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = REQUIRES_MINECRAFT_SERVER)
	public abstract SimpleFunctionWrapper getFunction(NamespacedKey key);

	@Override
	@Unimplemented(because = REQUIRES_MINECRAFT_SERVER)
	public abstract Set<NamespacedKey> getFunctions();

	@Override
	public abstract Predicate<ItemStack> getItemStackPredicate(CommandContext<CommandSourceStack> cmdCtx, String key)
		throws CommandSyntaxException;

	@Override
	@Unimplemented(because = { NAME_CHANGED, REQUIRES_CSS }, from = "a, b", to = "c(), d()")
	public abstract Location2D getLocation2DBlock(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = REQUIRES_CSS)
	public abstract Location2D getLocation2DPrecise(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = { NAME_CHANGED, REQUIRES_CSS }, from = "getX(), getY(), getZ()", to = "u(), v(), w()")
	public abstract Location getLocationBlock(CommandContext<CommandSourceStack> cmdCtx, String str) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = { NAME_CHANGED, REQUIRES_CSS }, from = "getX(), getY(), getZ()", to = "a(), b(), c()")
	public abstract Location getLocationPrecise(CommandContext<CommandSourceStack> cmdCtx, String str) throws CommandSyntaxException;

	@Override
	public abstract LootTable getLootTable(CommandContext<CommandSourceStack> cmdCtx, String key);

	@Override
	public abstract String getObjective(CommandContext<CommandSourceStack> cmdCtx, String key)
		throws IllegalArgumentException, CommandSyntaxException;

	@Override
	public abstract String getObjectiveCriteria(CommandContext<CommandSourceStack> cmdCtx, String key);

	@Override
	public final OfflinePlayer getOfflinePlayer(CommandContext<CommandSourceStack> cmdCtx, String str) throws CommandSyntaxException {
		OfflinePlayer target = Bukkit
			.getOfflinePlayer(((GameProfile) ArgumentProfile.a(cmdCtx, str).iterator().next()).getId());
		if (target == null)
			throw ArgumentProfile.a.create();
		return target;
	}

	@Override
	@Unimplemented(because = VERSION_SPECIFIC_IMPLEMENTATION, introducedIn = "1.18, 1.19")
	public abstract ParticleData<?> getParticle(CommandContext<CommandSourceStack> cmdCtx, String key);

	@Override
	public final Player getPlayer(CommandContext<CommandSourceStack> cmdCtx, String str) throws CommandSyntaxException {
		Player target = Bukkit.getPlayer(((GameProfile) ArgumentProfile.a(cmdCtx, str).iterator().next()).getId());
		if (target == null)
			throw ArgumentProfile.a.create();
		return target;
	}

	@Override
	public abstract PotionEffectType getPotionEffect(CommandContext<CommandSourceStack> cmdCtx, String key)
		throws CommandSyntaxException;

	@Override
	public abstract Recipe getRecipe(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = REQUIRES_CSS)
	public abstract CommandSender getSenderForCommand(CommandContext<CommandSourceStack> cmdCtx, boolean forceNative);

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "CraftServer")
	public abstract SimpleCommandMap getSimpleCommandMap();

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "CraftSound")
	public abstract Sound getSound(CommandContext<CommandSourceStack> cmdCtx, String key);

	@Override
	public abstract SuggestionProvider<T> getSuggestionProvider(SuggestionProviders provider);

	@Override
	@Unimplemented(because = REQUIRES_MINECRAFT_SERVER)
	public abstract SimpleFunctionWrapper[] getTag(NamespacedKey key);

	@Override
	@Unimplemented(because = REQUIRES_MINECRAFT_SERVER)
	public abstract Set<NamespacedKey> getTags();

	@Override
	public abstract String getTeam(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

	@Override
	@Unimplemented(because = REQUIRES_CSS)
	public abstract World getWorldForCSS(T clw);

	@Override
	@Unimplemented(because = REQUIRES_CRAFTBUKKIT, classNamed = "VanillaCommandWrapper")
	public abstract boolean isVanillaCommandWrapper(Command command);

	@Override
	public abstract void reloadDataPacks();

	@Override
	@Unimplemented(because = REQUIRES_MINECRAFT_SERVER)
	public abstract void resendPackets(Player player);
}
