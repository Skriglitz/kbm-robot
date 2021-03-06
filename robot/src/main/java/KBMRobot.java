import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;

public class KBMRobot {
	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		Robot r = null;
		try {
			r = new Robot();
		} catch (AWTException e1) {
			System.out.println("Failed to create robot.");
			e1.printStackTrace();
			System.exit(1);
		}

		// Make a key string to key id map
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field f : KeyEvent.class.getDeclaredFields()) {
			try {
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
					f.setAccessible(true);
					map.put(f.getName(), f.get(null));
				}
			} catch (Exception ex) {
				System.out.println("Failed to map key.");
			}
		}
		
		System.out.println("Starting the loop!!");

		while (true) {
			try {
				String str = in.readLine();

				String[] stringParts = str.split(" ");
				if (stringParts.length > 1) {
					if (stringParts[0].equalsIgnoreCase("MM") || stringParts[0].equalsIgnoreCase("MOUSEMOVE")) {
						r.mouseMove(Integer.parseInt(stringParts[1]), Integer.parseInt(stringParts[2]));
					} else if (stringParts[0].equalsIgnoreCase("MP") || stringParts[0].equalsIgnoreCase("MD")
							|| stringParts[0].equalsIgnoreCase("MOUSEPRESS")) {
						int press = 0;
						press |= stringParts[1].indexOf("1") > -1 ? KeyEvent.BUTTON1_MASK : 0;
						press |= stringParts[1].indexOf("2") > -1 ? KeyEvent.BUTTON2_MASK : 0;
						press |= stringParts[1].indexOf("3") > -1 ? KeyEvent.BUTTON3_MASK : 0;
						r.mousePress(press);
					} else if (stringParts[0].equalsIgnoreCase("MR") || stringParts[0].equalsIgnoreCase("MU")
							|| stringParts[0].equalsIgnoreCase("MOUSERELEASE")) {
						int press = 0;
						press |= stringParts[1].indexOf("1") > -1 ? KeyEvent.BUTTON1_MASK : 0;
						press |= stringParts[1].indexOf("2") > -1 ? KeyEvent.BUTTON2_MASK : 0;
						press |= stringParts[1].indexOf("3") > -1 ? KeyEvent.BUTTON3_MASK : 0;
						r.mouseRelease(press);
					} else if (stringParts[0].equalsIgnoreCase("MW") || stringParts[0].equalsIgnoreCase("MOUSEWHEEL")) {
						r.mouseWheel(Integer.parseInt(stringParts[1]));
					} else {
						Object key = map.get(stringParts[1]);
						if (key != null) {
							if (stringParts[0].equalsIgnoreCase("D") || stringParts[0].equalsIgnoreCase("P")
									|| stringParts[0].equalsIgnoreCase("PRESS")) {
								System.out.println("Press " + stringParts[1]);

								r.keyPress((Integer) key);
							} else if (stringParts[0].equalsIgnoreCase("U") || stringParts[0].equalsIgnoreCase("R")
									|| stringParts[0].equalsIgnoreCase("RELEASE")) {
								System.out.println("Release " + stringParts[1]);
								r.keyRelease((Integer) key);
							}
						} else {
							if (checkForMediaKeys(stringParts[1])) {
								if (stringParts[0].equalsIgnoreCase("D") || stringParts[0].equalsIgnoreCase("P")
										|| stringParts[0].equalsIgnoreCase("PRESS")) {
									System.out.print("Press " + stringParts[1]);

									callNativeKey(true, stringParts[1]);
								} else if (stringParts[0].equalsIgnoreCase("U") || stringParts[0].equalsIgnoreCase("R")
										|| stringParts[0].equalsIgnoreCase("RELEASE")) {
									System.out.print("Release " + stringParts[1]);
									callNativeKey(false, stringParts[1]);
									
								}
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Something bad happened: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	private static boolean checkForMediaKeys(String key) {
		if ("VOLUME_MUTE".equalsIgnoreCase(key) ||
				"VOLUME_DOWN".equalsIgnoreCase(key) || 
				"VOLUME_UP".equalsIgnoreCase(key) || 
				"MEDIA_PLAY_PAUSE".equalsIgnoreCase(key) || 
				"MEDIA_STOP".equalsIgnoreCase(key) || 
				"MEDIA_PREV_TRACK".equalsIgnoreCase(key) || 
				"MEDIA_NEXT_TRACK".equalsIgnoreCase(key)) {
			
			return true;
		}
		return false;

	}
	
	private static void callNativeKey(boolean pressed, String key) {
		int rawCode = 0;
		int keyCode = 0;
		if ("VOLUME_MUTE".equalsIgnoreCase(key)) {
			rawCode = 0xAD;
			keyCode = NativeKeyEvent.VC_VOLUME_MUTE;
		} else if ("VOLUME_DOWN".equalsIgnoreCase(key)) {
			rawCode = 0xAE;
			keyCode = NativeKeyEvent.VC_VOLUME_DOWN;
		} else if ("VOLUME_UP".equalsIgnoreCase(key)) {
			rawCode = 0xAF;
			keyCode = NativeKeyEvent.VC_VOLUME_UP;
		} else if ("MEDIA_PLAY_PAUSE".equalsIgnoreCase(key)) {
			rawCode = 0xB3;
			keyCode = NativeKeyEvent.VC_MEDIA_PLAY;
		} else if ("MEDIA_STOP".equalsIgnoreCase(key)) {
			rawCode = 0xB2;
			keyCode = NativeKeyEvent.VC_MEDIA_STOP;
		} else if ("MEDIA_PREV_TRACK".equalsIgnoreCase(key)) {
			rawCode = 0xB1;
			keyCode = NativeKeyEvent.VC_MEDIA_PREVIOUS;
		} else if ("MEDIA_NEXT_TRACK".equalsIgnoreCase(key)) {
			rawCode = 0xB0;
			keyCode = NativeKeyEvent.VC_MEDIA_NEXT;
		}
		
		GlobalScreen.postNativeEvent(new NativeKeyEvent(
				(pressed ? NativeKeyEvent.NATIVE_KEY_PRESSED : NativeKeyEvent.NATIVE_KEY_RELEASED),
				0,
				rawCode,
				keyCode,
				NativeKeyEvent.CHAR_UNDEFINED
				));
	}
}
