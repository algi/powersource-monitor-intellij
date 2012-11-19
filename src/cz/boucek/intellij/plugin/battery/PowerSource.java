package cz.boucek.intellij.plugin.battery;

/**
 * Enumeration of possible power sources.
 *
 * @author Marian Bouček
 */
public enum PowerSource {

	/**
	 * Machine is on adapter.
	 */
	POWER_WALL,

	/**
	 * Machine uses battery power.
	 */
	POWER_BATTERY,

	/**
	 * Unknown source of power.
	 */
	POWER_UNKNOWN;

}
