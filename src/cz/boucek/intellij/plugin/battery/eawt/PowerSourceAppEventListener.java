package cz.boucek.intellij.plugin.battery.eawt;

import com.apple.eawt.*;
import cz.boucek.intellij.plugin.battery.PowerSourceObserver;

/**
 * Apple system event listener.
 *
 * @author Marian Bouček
 */
public class PowerSourceAppEventListener implements UserSessionListener, ScreenSleepListener, SystemSleepListener {

	/**
	 * Register this listener to app.
	 */
	public static void registerSelf() {
		AppEventListener listener = new PowerSourceAppEventListener();
		Application.getApplication().addAppEventListener(listener);
	}

	@Override
	public void userSessionDeactivated(AppEvent.UserSessionEvent userSessionEvent) {
		pause();
	}

	@Override
	public void userSessionActivated(AppEvent.UserSessionEvent userSessionEvent) {
		resume();
	}

	@Override
	public void screenAboutToSleep(AppEvent.ScreenSleepEvent screenSleepEvent) {
		pause();
	}

	@Override
	public void screenAwoke(AppEvent.ScreenSleepEvent screenSleepEvent) {
		resume();
	}

	@Override
	public void systemAboutToSleep(AppEvent.SystemSleepEvent systemSleepEvent) {
		pause();
	}

	@Override
	public void systemAwoke(AppEvent.SystemSleepEvent systemSleepEvent) {
		resume();
	}

	// private methods -------------------------------------------------------------------------------------------------
	private void pause() {
		PowerSourceObserver.getInstance().pauseMonitor();
	}

	private void resume() {
		PowerSourceObserver.getInstance().resumeMonitor();
	}
}
