package camp.computer.clay.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cache {

    private long cacheUid = 0L;

    private HashMap<Long, Object> elements;

    public Cache() {
        elements = new HashMap<>();
    }

    public long add(Object object) {
        long objectUid = cacheUid++;
        elements.put(objectUid, object);
        return objectUid;
    }

    public Object get(long uid) {
        return elements.get(uid);
    }

    public List<Long> getUids() {
        return new ArrayList<>(elements.keySet());
    }

    public List<Object> getObjects() {
        return new ArrayList<>(elements.values());
    }

    public List<Object> getObjects(Class<? extends Object>... objectTypes) {
        // TODO:
        return null;
    }
}
