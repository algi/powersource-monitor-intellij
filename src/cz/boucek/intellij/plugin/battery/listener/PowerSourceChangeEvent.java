package cz.boucek.intellij.plugin.battery.listener;

import cz.boucek.intellij.plugin.battery.PowerSource;

/**
 * Event capturing old and new state of power source.
 *
 * @author Marian Bouček
 */
public class PowerSourceChangeEvent {

	private PowerSource oldValue;
	private PowerSource newValue;

	public PowerSourceChangeEvent(PowerSource oldValue, PowerSource newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public PowerSource getOldValue() {
		return oldValue;
	}

	public PowerSource getNewValue() {
		return newValue;
	}
}
