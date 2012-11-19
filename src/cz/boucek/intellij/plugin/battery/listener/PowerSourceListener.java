package cz.boucek.intellij.plugin.battery.listener;

/**
 * User: Marian Bouček
 */
public interface PowerSourceListener {

	/**
	 * Indicates that power source has changed.
	 *
	 * @param event event details
	 */
	void powerSourceChanged(PowerSourceChangeEvent event);

}
