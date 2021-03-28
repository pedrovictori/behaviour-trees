package behaviour_trees.core;

import java.util.Objects;
import java.util.function.BiConsumer;

public class BTLogger {
	private static BiConsumer<String, Object[]> info;
	private static BiConsumer<String, Object[]> error;
	private static BiConsumer<String, Object[]> debug;

	public static void setInfoConsumer(BiConsumer<String, Object[]> info) {
		BTLogger.info = info;
	}

	public static void setErrorConsumer(BiConsumer<String, Object[]> error) {
		BTLogger.error = error;
	}

	public static void setDebugConsumer(BiConsumer<String, Object[]> debug) {
		BTLogger.debug = debug;
	}

	public static void info(String entry, Object... args){
		if(info!=null)
		info.accept(entry, args);
	}

	public static void error(String entry, Object... args){
		if(error!= null)
		error.accept(entry, args);
	}

	public static void debug(String entry, Object... args){
		if(debug!=null)
		debug.accept(entry, args);
	}
}
