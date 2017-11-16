package smartAP.MessageConsumer_2.transformer;

import java.util.List;
import java.util.Map;

public interface SqlTransformer {

	Map<Integer, Object> transform(List<String> lines);
}
