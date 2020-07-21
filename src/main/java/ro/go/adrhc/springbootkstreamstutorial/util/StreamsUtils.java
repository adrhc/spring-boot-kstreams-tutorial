package ro.go.adrhc.springbootkstreamstutorial.util;

public class StreamsUtils {
	public static String storeNameOf(String topicName) {
		return topicName + "-store";
	}

	public static String streamNameOf(String topicName) {
		return topicName + "-stream";
	}

	public static String tableNameOf(String topicName) {
		return topicName + "-table";
	}
}
