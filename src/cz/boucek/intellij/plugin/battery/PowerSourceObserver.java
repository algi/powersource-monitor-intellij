package cz.boucek.intellij.plugin.battery;

import com.apple.concurrent.Dispatch;
import com.intellij.openapi.diagnostic.Logger;
import cz.boucek.intellij.plugin.battery.listener.PowerSourceChangeEvent;
import cz.boucek.intellij.plugin.battery.listener.PowerSourceListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Monitor for observing power source changes using JNI. Requiers native library
 * <code>{@value #LIBRARY_NAME}.jnilib</code> on classpath.
 *
 * <p>
 *     <em>Implementation note:</em> The only required native method is <code>void registerMonitor()</code>
 *     which will call our static method named <code>void powerSourceChanged(String)</code> when power source changes.
 *     Accepted values of that method can only be as declared in enum PowerSource. Passing any other value is prohibited.
 * </p>
 *
 * @author Marian Bouček
 */
public class PowerSourceObserver {

	private static final Logger LOGGER = Logger.getInstance(PowerSourceObserver.class);

	private static final String LIBRARY_NAME = "BatteryWatcher";
	private static PowerSourceObserver INSTANCE;

	private boolean monitorRunning;
	private PowerSource oldValue;
	private final List<PowerSourceListener> listeners;

	/**
	 * Private constructor which will call native code and create empty
	 * list of battery source listeners.
	 */
	private PowerSourceObserver() {
		this.listeners = new ArrayList<PowerSourceListener>();

		// load native library
		System.loadLibrary(LIBRARY_NAME);

		boolean registeredJNI = registerJNI();
		if (! registeredJNI) {
			throw new IllegalStateException("Cannot obtain connection to native code.");
		}

		// initialize to default value
		oldValue = PowerSource.POWER_UNKNOWN;
	}

	/**
	 * Returns singleton instance of battery monitor.
	 *
	 * @return Singleton instance.
	 */
	public static PowerSourceObserver getInstance() {
		if (INSTANCE == null) {
			System.out.println("creating new instance.");
			INSTANCE = new PowerSourceObserver();
		}

		return INSTANCE;
	}

	/**
	 * Adds new battery source listener to monitor.
	 *
	 * @param listener battery source listener
	 */
	public void addListener(PowerSourceListener listener) {
		listeners.add(listener);
	}

	/**
	 * Returns current power state.
	 *
	 * @return Current power state.
	 */
	public PowerSource getCurrentState() {
		if (! isMonitorRunning()) {
			return PowerSource.POWER_UNKNOWN;
		}

		String powerSourceState = getPowerSourceState();

		for (PowerSource s : PowerSource.values()) {
			if (s.name().equals(powerSourceState)) {
				return s;
			}
		}

		// error in provider
		return PowerSource.POWER_UNKNOWN;
	}

	/**
	 * Pauses observer.
	 */
	public void pauseMonitor() {
		LOGGER.info("Pausing monitor.");
		if (! isMonitorRunning()) {
			return;
		}

		stopMonitor();
	}

	/**
	 * Resumes observer. Forcibly check if something changed during paused state.
	 */
	public void resumeMonitor() {
		// resume only once
		if (isMonitorRunning()) {
			return;
		}

		LOGGER.info("Resuming monitor.");

		// run async task using Grand Central Dispatch
		Executor asyncExecutor = Dispatch.getInstance().getAsyncExecutor(Dispatch.Priority.LOW);
		asyncExecutor.execute(new Runnable() {
			@Override
			public void run() {
				startMonitor();
			}
		});

		LOGGER.info("Monitor is now running.");
		monitorRunning = true;

		powerSourceChanged();
	}

	// private API -----------------------------------------------------------------------------------------------------
	/**
	 * Returns all registered listeners.
	 *
	 * @return All registered listeners.
	 */
	private List<PowerSourceListener> getListeners() {
		return listeners;
	}

	/**
	 * Set old value of power state.
	 *
	 * @param oldValue old value of power state
	 */
	private void setOldValue(PowerSource oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * Returns old value of power state.
	 *
	 * @return Previous value of power state.
	 */
	private PowerSource getOldValue() {
		return oldValue;
	}

	/**
	 * Return flag if JNI was successfuly connected to this Java code.
	 *
	 * @return Flag.
	 */
	private boolean isMonitorRunning() {
		return monitorRunning;
	}

	// calback methods
	/**
	 * Called from native code whe power source changes.
	 */
	@SuppressWarnings("UnusedDeclaration")
	static void powerSourceChanged() {
		final PowerSourceObserver instance = PowerSourceObserver.getInstance();
		if (! instance.isMonitorRunning()) {
			return;
		}

		final PowerSource oldValue = instance.getOldValue();
		final PowerSource newValue = instance.getCurrentState();

		// do not send notification if nothing changed
		if (oldValue == newValue) {
			return;
		}
		LOGGER.info("Power source changed from: " + oldValue + " to: " + newValue);

		// send notification to all listeners via event dispatch thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (PowerSourceListener listener : instance.getListeners()) {
					listener.powerSourceChanged(new PowerSourceChangeEvent(oldValue, newValue));
				}
			}
		});

		instance.setOldValue(newValue);
	}

	// native interface
	/**
	 * Register native power source in OS.
	 *
	 * @return Result of registration.
	 */
	private native boolean registerJNI();

	/**
	 * Starts power source change monitor.
	 *
	 * @return Was start successful?
	 */
	private native boolean startMonitor();

	/**
	 * Stops power source change monitor.
	 *
	 * @return Was stop successful?
	 */
	private native boolean stopMonitor();

	/**
	 * Get current state of power source using JNI.
	 *
	 * @return Current state of power source.
	 */
	private native String getPowerSourceState();

}
