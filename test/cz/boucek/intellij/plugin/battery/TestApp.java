package cz.boucek.intellij.plugin.battery;

import com.apple.concurrent.Dispatch;

import javax.swing.*;
import java.util.concurrent.Executor;

/**
 * User: Marian Bouček
 */
public class TestApp {

	public static void main(String[] args) throws Exception {


		PowerSourceObserver instance = PowerSourceObserver.getInstance();

		instance.addListener(new DebugPowerSourceListener());
		instance.resumeMonitor();

		System.out.println("hotovo");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				while (true) {
					; // do shit
				}
			}
		});
	}
}
