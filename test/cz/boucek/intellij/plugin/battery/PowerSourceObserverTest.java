package cz.boucek.intellij.plugin.battery;

import cz.boucek.intellij.plugin.battery.listener.PowerSourceChangeEvent;
import cz.boucek.intellij.plugin.battery.listener.PowerSourceListener;
import cz.boucek.intellij.plugin.battery.plugin.PowerSourceMonitorPlugin;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link PowerSourceObserver} class.
 *
 * @author Marian Bouček
 */
public class PowerSourceObserverTest {

	/**
	 * Test proper loading library and registering event loop.
	 */
	@Test
	public void testGetInstance() {
		PowerSourceObserver.getInstance();
	}

	/**
	 * Current state of power connection cannot be determined
	 * so we only check that returned value is in known values.
	 */
	@Test
	public void testGetCurrentState() {
		PowerSource currentState = PowerSourceObserver.getInstance().getCurrentState();
		assertThat(currentState, notNullValue());

		boolean found = false;
		PowerSource[] allowedValues = PowerSource.values();
		for (PowerSource source : allowedValues) {
			if (source.equals(currentState)) {
				found = true;
				break;
			}
		}

		assertThat(found, is(true));
	}

	/**
	 * Add listener and make fake change by directly calling {@link PowerSourceObserver#powerSourceChanged()}.
	 * Number of listener calls should be equal to one but we are in hope that battery source will not change
	 * during this test.
	 */
	@Test
	public void testAddListener() {
		CallPowerSourceListener listener = new CallPowerSourceListener();
		PowerSourceObserver.getInstance().addListener(listener);

		// make fake change
		PowerSourceObserver.powerSourceChanged();
		assertThat(listener.getNumberOfCalls(), is(1));
	}

	@Test
	public void testEAWT() {
		// TODO spustit na jiném OS
		new PowerSourceMonitorPlugin().initComponent();
	}

	/**
	 * Test implementation of power source listener. It tracks number of calls.
	 */
	private static class CallPowerSourceListener implements PowerSourceListener {
		private int numberOfCalls;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void powerSourceChanged(PowerSourceChangeEvent event) {
			numberOfCalls++;
		}

		/**
		 * Returns number of calls of this listener.
		 *
		 * @return Number of calls.
		 */
		public int getNumberOfCalls() {
			return numberOfCalls;
		}
	}
}
