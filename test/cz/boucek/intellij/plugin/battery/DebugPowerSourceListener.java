package cz.boucek.intellij.plugin.battery;

import cz.boucek.intellij.plugin.battery.listener.PowerSourceChangeEvent;
import cz.boucek.intellij.plugin.battery.listener.PowerSourceListener;

/**
 * Debug power source listener.
 *
 * @author Marian Bouček
 */
public class DebugPowerSourceListener implements PowerSourceListener {
	@Override
	public void powerSourceChanged(PowerSourceChangeEvent event) {
		System.out.println("old value: " + event.getOldValue() + ", new value: " + event.getNewValue());
	}
}
