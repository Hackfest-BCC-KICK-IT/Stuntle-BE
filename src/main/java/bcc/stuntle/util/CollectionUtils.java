package bcc.stuntle.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionUtils {

    public static <String, V> Map<String, V> ofLinkedHashMap(String[] keys, V[] values){
        Map<String, V> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
}
