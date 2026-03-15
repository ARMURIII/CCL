package arr.armuriii.ccl;

import arr.armuriii.ccl.resources.converter.JsonConverterDataLoader;
import arr.armuriii.ccl.resources.script.JsonScript;
import arr.armuriii.ccl.resources.script.JsonScriptDataLoader;
import arr.armuriii.ccl.script.json.JsonComponentInit;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.data.CommandExtraData;
import arr.armuriii.ccl.script.json.data.CraftingExtraData;
import arr.armuriii.ccl.util.TagConversionUtils;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CustomCraftingLibrary implements ModInitializer {
	public static final String MOD_ID = "ccl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		JsonComponentInit.registerComponents();

		CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->{
			dispatcher.register(literal("jscript").then(argument("script",StringArgumentType.string()).executes(ctx -> {
				final String string = StringArgumentType.getString(ctx,"script");
				Optional<JsonObject> reader = JsonScript.getScript(new Identifier(string.replace(".json","")));
				if (reader.isPresent()) {
					Object object = JsonScriptReader.evaluate(reader.get(),new CommandExtraData(ctx.getSource()));
					ctx.getSource().sendFeedback(()->Text.literal("Returns: "+object.toString()),false);
				}
				return 1;
			})).executes(ctx -> {
				for (Map.Entry<Identifier, JsonObject> entry : JsonScript.scripts.entrySet())
					ctx.getSource().sendFeedback(()->Text.literal(entry.getKey().toString()),false);
				return 1;
			}));
		});

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new JsonConverterDataLoader());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new JsonScriptDataLoader());
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID,path);
	}
}