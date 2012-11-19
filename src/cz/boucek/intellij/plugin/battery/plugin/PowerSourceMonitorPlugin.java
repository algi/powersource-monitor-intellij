package cz.boucek.intellij.plugin.battery.plugin;

import com.apple.concurrent.Dispatch;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import cz.boucek.intellij.plugin.battery.PowerSourceObserver;
import cz.boucek.intellij.plugin.battery.eawt.PowerSourceAppEventListener;
import cz.boucek.intellij.plugin.battery.listener.PowerSafeModeListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/**
 * Application component used for tracking power source changes.
 *
 * @author Marian Bouček
 */
public class PowerSourceMonitorPlugin implements ApplicationComponent {

	private static final Logger LOGGER = Logger.getInstance(PowerSourceMonitorPlugin.class);

	@Override
	public void initComponent() {
		// TODO knihovna je prozatím umístěná v ~/Library/Java/Extensions/libBatteryWatcher.dylib

		// setup listeners and register for system events
		PowerSourceObserver.getInstance().addListener(new PowerSafeModeListener());
		PowerSourceAppEventListener.registerSelf();

		// start monitor using GCD
		Executor asyncExecutor = Dispatch.getInstance().getAsyncExecutor(Dispatch.Priority.LOW);
		asyncExecutor.execute(new Runnable() {
			@Override
			public void run() {
				PowerSourceObserver.getInstance().resumeMonitor();
			}
		});

		LOGGER.info("PowerSourceMonitorPlugin was successfuly initialized.");
	}

	@Override
	public void disposeComponent() {
	}

	@NotNull
	@Override
	public String getComponentName() {
		return "cz.boucek.intellij.plugin.battery.plugin.PowerSourceMonitorPlugin";
	}
}
