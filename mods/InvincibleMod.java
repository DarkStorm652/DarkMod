import org.darkstorm.minecraft.darkmod.events.PlayerProcessEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.Player;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.tools.events.*;

public class InvincibleMod extends Mod implements EventListener {

	@Override
	public String getShortDescription() {
		return "Become god!";
	}

	@Override
	public String getFullDescription() {
		return "<html>No attack damage, fall damage, underwater damage, "
				+ "or fire damage!<br />You will experience getting hurt from "
				+ "attacks, but health will automatically be set back to full. "
				+ "This, of course, means that creepers on hard mode will &quot;"
				+ "kill&quot; you if at close range and cause you to drop "
				+ "your items, but the death timer is reset, meaning you will "
				+ "not end up having to respawn. The only drawback to this is "
				+ "being near lava at the time you were &quot;killed&quot;"
				+ "</html>";
	}

	@Override
	public String getName() {
		return "Invincible Mod";
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public void onStart() {
		eventManager.addListener(PlayerProcessEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(PlayerProcessEvent.class, this);
	}

	@Override
	public int loop() {
		Player player = minecraft.getPlayer();
		if(player != null) {
			if(player.getBreathTimer() < 300)
				player.setBreathTimer(300);
			if(player.getOnFireTimer() != -20)
				player.setOnFireTimer(-20);
			if(player.getHealth() < 32767)
				player.setHealth(32767);
			if(player.getFallDistance() != 0)
				player.setFallDistance(0);
			if(player.getDeathTimer() != 0)
				player.setDeathTimer(0);
			if(player.getHurtTimer() != 0)
				player.setHurtTimer(0);
		}
		return 0;
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof PlayerProcessEvent) {
			PlayerProcessEvent playerProcessEvent = (PlayerProcessEvent) event;
			Player player = playerProcessEvent.getPlayer();
			if(player.getBreathTimer() < 300)
				player.setBreathTimer(300);
			if(player.getOnFireTimer() != -20)
				player.setOnFireTimer(-20);
			if(player.getHealth() < 32767)
				player.setHealth(32767);
			if(player.getFallDistance() != 0)
				player.setFallDistance(0);
			if(player.getDeathTimer() != 0)
				player.setDeathTimer(0);
			if(player.getHurtTimer() != 0)
				player.setHurtTimer(0);
		}
	}

}
