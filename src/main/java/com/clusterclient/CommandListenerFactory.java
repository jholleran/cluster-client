package com.clusterclient;

public interface CommandListenerFactory {

	TextListener makeTextListener(CommandService service, String host, String mode);

	FileStructureListener makeFileNavigationListener(CommandService service, String host, String mode);
}
