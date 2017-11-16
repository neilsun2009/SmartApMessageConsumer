package smartAP.MessageConsumer_2.util;

import java.util.List;

public class ListUtils {
	private ListUtils() {

	}

	public static String toString(List<? extends Object> list) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < list.size() - 1; i++) {
			stringBuilder.append(list.get(i) + ";");
		}

		stringBuilder.append(list.get(list.size() - 1));

		return stringBuilder.toString();
	}
}
