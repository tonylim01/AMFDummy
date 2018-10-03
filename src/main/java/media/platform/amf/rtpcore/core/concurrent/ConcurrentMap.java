package media.platform.amf.rtpcore.core.concurrent;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMap<E> extends ConcurrentHashMap<Integer, E> {
	
	private static final long serialVersionUID = 8270100031373807057L;

	@SuppressWarnings("unchecked")
	public Iterator<Integer> keysIterator() {
		return (Iterator<Integer>) keys();
	}

	@SuppressWarnings("unchecked")
	public Iterator<E> valuesIterator() {
		return (Iterator<E>) elements();
	}
}
