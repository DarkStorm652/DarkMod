import java.lang.reflect.Field;
import java.util.*;

import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;

public class ReiMinimapAllowAllMod extends Mod {

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "ReiMinimap AllowAll Mod";
	}

	@Override
	public String getShortDescription() {
		return "Enables cave rendering and all entities radar";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		try {
			Class<?> minimapClass = Class.forName("reifnsk.minimap.ReiMinimap");
			Field instanceField = minimapClass.getDeclaredField("instance");
			Object minimap = instanceField.get(null);

			List<Field> allowFields = new ArrayList<Field>();
			List<Field> visibleFields = new ArrayList<Field>();
			List<Field> configFields = new ArrayList<Field>();

			Field allowCavemapField = minimapClass
					.getDeclaredField("allowCavemap");
			if(!allowCavemapField.isAccessible())
				allowCavemapField.setAccessible(true);
			allowCavemapField.setBoolean(minimap, true);

			for(Field field : minimapClass.getDeclaredFields()) {
				String fieldName = field.getName();
				if(fieldName.startsWith("allowEntit"))
					allowFields.add(field);
				else if(fieldName.startsWith("visibleEntit"))
					visibleFields.add(field);
				else if(fieldName.startsWith("configEntit")
						&& !fieldName.equals("configEntityDirection"))
					configFields.add(field);
			}
			if(allowFields.size() != visibleFields.size()
					|| allowFields.size() != configFields.size())
				throw new RuntimeException(
						"inequal allow, visible, and config fields");

			boolean[] allows = new boolean[allowFields.size()];
			boolean[] visibles = new boolean[visibleFields.size()];
			boolean[] configs = new boolean[configFields.size()];

			for(int i = 0; i < allows.length; i++) {
				Field field = allowFields.get(i);
				if(!field.isAccessible())
					field.setAccessible(true);
				allows[i] = field.getBoolean(minimap);
			}

			for(int i = 0; i < visibles.length; i++) {
				Field field = visibleFields.get(i);
				if(!field.isAccessible())
					field.setAccessible(true);
				visibles[i] = field.getBoolean(minimap);
			}

			for(int i = 0; i < configs.length; i++) {
				Field field = configFields.get(i);
				if(!field.isAccessible())
					field.setAccessible(true);
				configs[i] = field.getBoolean(minimap);
			}

			boolean valueChanged = false;
			for(int i = 0; i < allows.length; i++) {
				boolean allow = allows[i];
				if(!allow) {
					Field allowField = allowFields.get(i);
					allowField.setBoolean(minimap, true);
					Field visibleField = visibleFields.get(i);
					visibleField.set(minimap, configs[i]);
				}
			}
			if(valueChanged)
				displayText(ChatColor.AQUA
						+ "Successfully changed minimap values!");
		} catch(ClassNotFoundException exception) {
			displayText(ChatColor.RED + "Minimap not found!");
			handler.stopMod(getName());
		} catch(Throwable exception) {
			exception.printStackTrace();
		}
		return 5000;
	}

}
