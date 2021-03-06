package fwcd.fructose.structs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fwcd.fructose.EventListenerList;
import fwcd.fructose.ReadOnlyListenableValue;
import fwcd.fructose.util.StreamUtils;
import fwcd.fructose.math.IntRange;
import fwcd.fructose.structs.events.ListModifyEvent;

/**
 * A read-only list that can be listened to.
 */
public class ReadOnlyObservableList<T> implements Serializable, Iterable<T>, ReadOnlyListenableValue<List<T>> {
	private static final long serialVersionUID = 5345305630308018532L;
	private transient EventListenerList<List<T>> nullableChangeListeners;
	private transient EventListenerList<ListModifyEvent<T>> nullableModifyListeners;
	private List<T> values;
	
	public ReadOnlyObservableList() { values = new ArrayList<>(); }
	
	public ReadOnlyObservableList(List<T> values) { this.values = values; }
	
	private EventListenerList<List<T>> getChangeListeners() {
		if (nullableChangeListeners == null) {
			nullableChangeListeners = new EventListenerList<>();
		}
		return nullableChangeListeners;
	}
	
	private EventListenerList<ListModifyEvent<T>> getModifyListeners() {
		if (nullableModifyListeners == null) {
			nullableModifyListeners = new EventListenerList<>();
		}
		return nullableModifyListeners;
	}
	
	@Override
	public void listen(Consumer<? super List<T>> listener) { getChangeListeners().add(listener); }
	
	@Override
	public void listenAndFire(Consumer<? super List<T>> listener) {
		listen(listener);
		listener.accept(values);
	}
	
	@Override
	public void unlisten(Consumer<? super List<T>> listener) { getChangeListeners().remove(listener); }
	
	public void listenForModifications(Consumer<? super ListModifyEvent<T>> listener) { getModifyListeners().add(listener); }
	
	public void listenForModificationsAndFire(Consumer<? super ListModifyEvent<T>> listener) {
		listenForModifications(listener);
		listener.accept(new ListModifyEvent<>(values, new IntRange(0, values.size())));
	}
	
	public void unlistenForModifications(Consumer<? super ListModifyEvent<T>> listener) { getModifyListeners().remove(listener); }
	
	@Override
	public List<T> get() { return Collections.unmodifiableList(values); }
	
	public T get(int index) { return values.get(index); }
	
	public int size() { return values.size(); }
	
	public boolean isEmpty() { return values.isEmpty(); }
	
	public boolean contains(Object o) { return values.contains(o); }
	
	public Stream<T> stream() { return StreamUtils.stream(this); }
	
	@Override
	public Iterator<T> iterator() { return values.iterator(); }
	
	public Object[] toArray() { return values.toArray(); }
	
	public <E> E[] toArray(E[] a) { return values.toArray(a); }
	
	public boolean containsAll(Collection<?> c) { return values.containsAll(c); }

	public int indexOf(Object o) { return values.indexOf(o); }

	public int lastIndexOf(Object o) { return values.lastIndexOf(o); }

	public ListIterator<T> listIterator() { return values.listIterator(); }

	public ListIterator<T> listIterator(int index) { return values.listIterator(index); }

	public List<T> subList(int fromIndex, int toIndex) { return values.subList(fromIndex, toIndex); }
	
	protected void fireChange() {
		getChangeListeners().fire(values);
	}
	
	protected void fireModification(List<? extends T> deltaValues, int inclusiveStart, int exclusiveEnd) {
		getModifyListeners().fire(new ListModifyEvent<>(deltaValues, new IntRange(inclusiveStart, exclusiveEnd)));
	}
	
	// Protected, mutating methods
	
	protected List<T> getSilentlyMutable() { return values; }
	
	protected boolean add(T value) {
		int prevSize = values.size();
		boolean success = values.add(value);
		fireChange();
		fireModification(Collections.singletonList(value), prevSize, prevSize);
		return success;
	}
	
	protected void set(List<T> newValues) {
		int prevSize = values.size();
		values = newValues;
		fireChange();
		fireModification(newValues, 0, prevSize);
	}
	
	protected boolean remove(Object value) {
		T removed = remove(values.indexOf(value));
		return removed != null;
	}
	
	protected T remove(int index) {
		T removed = values.remove(index);
		fireChange();
		fireModification(Collections.emptyList(), index, index + 1);
		return removed;
	}
	
	protected void add(int index, T value) {
		values.add(index, value);
		fireChange();
		fireModification(Collections.singletonList(value), index, index);
	}
	
	protected T set(int index, T value) {
		T removed = values.set(index, value);
		fireChange();
		fireModification(Collections.singletonList(value), index, index + 1);
		return removed;
	}
	
	protected void use(Consumer<List<T>> user) {
		int prevSize = values.size();
		user.accept(values);
		fireChange();
		fireModification(values, 0, prevSize);
	}
	
	protected boolean addAll(Collection<? extends T> c) {
		int prevSize = values.size();
		List<? extends T> delta = new ArrayList<>(c);
		boolean success = values.addAll(delta);
		fireChange();
		fireModification(delta, prevSize, prevSize);
		return success;
	}
	
	protected boolean addAll(int index, Collection<? extends T> c) {
		List<? extends T> delta = new ArrayList<>(c);
		boolean success = values.addAll(index, delta);
		fireChange();
		fireModification(delta, index, index);
		return success;
	}

	protected boolean removeAll(Collection<?> c) {
		int prevSize = values.size();
		boolean success = values.removeAll(c);
		fireChange();
		fireModification(values, 0, prevSize);
		return success;
	}

	protected boolean retainAll(Collection<?> c) {
		int prevSize = values.size();
		boolean success = values.retainAll(c);
		fireChange();
		fireModification(values, 0, prevSize);
		return success;
	}
	
	protected void clear() {
		int prevSize = values.size();
		values.clear();
		fireChange();
		fireModification(Collections.emptyList(), 0, prevSize);
	}
}
