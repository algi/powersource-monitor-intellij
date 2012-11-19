package cz.boucek.intellij.plugin.battery.listener;

import com.intellij.ide.PowerSaveMode;
import com.intellij.openapi.diagnostic.Logger;
import cz.boucek.intellij.plugin.battery.PowerSource;

/**
 * Listener that will turn off/on power safe mode depending on current source of power.
 *
 * User: Marian Bouček
 */
public class PowerSafeModeListener implements PowerSourceListener {

	private static final Logger LOGGER = Logger.getInstance(PowerSafeModeListener.class);

	@Override
	public void powerSourceChanged(PowerSourceChangeEvent event) {
		LOGGER.info("Enabling Power Save Mode.");

		// dispatch it in event dispatch thread ;-)
		boolean enabled = PowerSource.POWER_BATTERY.equals(event.getNewValue());
		PowerSaveMode.setEnabled(enabled);
	}
}
