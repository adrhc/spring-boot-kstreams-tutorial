package ro.go.adrhc.springbootkstreamstutorial.util;

public class StreamsUtils {
	public static String storeOf(String topicName) {
		return topicName + "-store";
	}

	public static String streamOf(String topicName) {
		return topicName + "-stream";
	}

	public static String tableOf(String topicName) {
		return topicName + "-table";
	}
}
