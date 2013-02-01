package com.clusterclient;

/**
 * This interface is used to create listeners for the {@link CommandService}'s
 * 
 * @author HolleranJ
 * 
 */
public interface CommandListenerFactory {

	/**
	 * Make a text listener for the {@link CommandService}. The listener will
	 * need to be added to the {@link CommandService}.
	 * 
	 * @param service
	 * @param host
	 * @param mode
	 * @return
	 */
	TextListener makeTextListener(CommandService service, String host,
			String mode);

	/**
	 * Makes a listener that can display system file structure to the user. The
	 * Listener will need to be added to the {@link CommandService}.
	 * 
	 * @param service
	 * @param host
	 * @param mode
	 * @return
	 */
	FileStructureListener makeFileNavigationListener(CommandService service,
			String host, String mode);
}
