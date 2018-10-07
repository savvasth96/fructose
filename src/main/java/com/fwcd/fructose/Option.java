package com.fwcd.fructose;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

/**
 * An immutable container that may or may not hold a non-null value.
 * 
 * <p>This class closely follows the public interface
 * of {@link java.util.Optional}.</p>
 * 
 * <p>Unlike {@link java.util.Optional} which is only designed
 * to support the optional return idiom, {@link Option} is
 * intended to be used as a general-purpose "Maybe"-type that
 * may be stored and serialized too.</p>
 * 
 * <p>Additionally, {@link Option} is not marked as {@code final}, which
 * allows you to create subclasses (although it should be rarely
 * needed).</p>
 * 
 * <p>{@link Option} forms a functional monad ({@code unit} is implemented
 * through {@code Option.of} and {@code bind} through {@code flatMap}).
 * Strictly speaking, the monad laws only apply in code that does
 * not rely on {@code null} as a valid value though, because {@link Option}
 * internally interprets {@code null} as {@code Option.empty} rather
 * than a valid reference.</p>
 */
public class Option<T> implements Serializable, Iterable<T> {
	private static final long serialVersionUID = -7974381950871417577L;
	private static final Option<Void> EMPTY = new Option<>(null);
	private final T value;
	
	/**
	 * Internally constructs a new {@link Option} instance.
	 *  
	 * @param value - A nullable value
	 */
	private Option(T value) {
		this.value = value;
	}
	
	/**
	 * Creates a new {@link Option} instance.
	 * 
	 * @param value - A nullable value
	 * @return An {@link Option} wrapping this value
	 */
	public static <R> Option<R> ofNullable(R value) {
		if (value == null) {
			return empty();
		} else {
			return new Option<>(value);
		}
	}
	
	/**
	 * Creates a new {@link Option} instance.
	 * 
	 * @param value - A non-null value
	 * @return An {@link Option} wrapping this value
	 * @throws NullPointerException if the value is null
	 */
	public static <R> Option<R> of(R value) {
		if (value == null) {
			throw new NullPointerException("Tried to create a non-null Option of " + value);
		}
		
		return new Option<>(value);
	}
	
	/**
	 * Converts a {@link java.util.Optional} to an {@link Option}.
	 */
	public static <R> Option<R> of(Optional<R> value) {
		return ofNullable(value.orElse(null));
	}
	
	/**
	 * Fetches an empty/absent {@link Option} instance.
	 */
	@SuppressWarnings("unchecked")
	public static <R> Option<R> empty() {
		return (Option<R>) EMPTY;
	}
	
	/**
	 * Checks whether this value is present.
	 */
	public boolean isPresent() {
		return value != null;
	}
	
	/**
	 * Invokes the consumer if the value is present.
	 */
	public void ifPresent(Consumer<? super T> then) {
		if (value != null) {
			then.accept(value);
		}
	}
	
	/**
	 * Invokes the first argument if the value is present,
	 * otherwise runs the second argument.
	 */
	public void ifPresentOrElse(Consumer<? super T> then, Runnable otherwise) {
		if (value != null) {
			then.accept(value);
		} else {
			otherwise.run();
		}
	}
	
	/**
	 * Unwraps the value and throws an exception if absent.
	 * 
	 * <p>Generally, you should avoid this method
	 * and use {@code expect} or {@code orElse} instead.</p>
	 * 
	 * @throws NoSuchElementException if absent
	 * @return The wrapped value
	 */
	public T unwrap() {
		if (value == null) {
			throw new NoSuchElementException("Tried to unwrap an empty Option");
		}
		
		return value;
	}
	
	/**
	 * Unwraps the value and throws a message if absent.
	 * 
	 * @param messageIfAbsent - The error message
	 * @throws NoSuchElementException if absent
	 * @return The wrapped value
	 */
	public T unwrap(String messageIfAbsent) {
		if (value == null) {
			throw new NoSuchElementException(messageIfAbsent);
		}
		
		return value;
	}
	
	/**
	 * Returns the wrapped value in case the value is present
	 * and matches the predicate, otherwise returns an empty
	 * {@link Option}.
	 */
	public Option<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate, "Filter predicate can not be null");
		if (value == null) {
			return this;
		} else {
			return predicate.test(value) ? this : empty();
		}
	}
	
	/**
	 * Returns an {@link Option} containing the result of the
	 * function if present, otherwise returns an empty {@link Option}.
	 */
	public <R> Option<R> map(Function<? super T, ? extends R> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return empty();
		} else {
			return of(mapper.apply(value));
		}
	}
	
	/**
	 * Returns an {@link Option} containing the result of the
	 * function if this {@link Option} is present and the result
	 * is not null, otherwise returns an empty {@link Option}.
	 */
	public <R> Option<R> mapToNullable(Function<? super T, ? extends R> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return empty();
		} else {
			return ofNullable(mapper.apply(value));
		}
	}
	
	/**
	 * Returns an {@link OptionInt} containing the result of the
	 * function if present, otherwise returns an empty {@link OptionInt}.
	 */
	public OptionInt mapToInt(ToIntFunction<? super T> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return OptionInt.empty();
		} else {
			return OptionInt.of(mapper.applyAsInt(value));
		}
	}
	
	/**
	 * Returns an {@link OptionDouble} containing the result of the
	 * function if present, otherwise returns an empty {@link OptionDouble}.
	 */
	public OptionDouble mapToDouble(ToDoubleFunction<? super T> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return OptionDouble.empty();
		} else {
			return OptionDouble.of(mapper.applyAsDouble(value));
		}
	}
	
	/**
	 * Returns an {@link OptionLong} containing the result of the
	 * function if present, otherwise returns an empty {@link OptionLong}.
	 */
	public OptionLong mapToLong(ToLongFunction<? super T> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return OptionLong.empty();
		} else {
			return OptionLong.of(mapper.applyAsLong(value));
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link Option}.
	 */
	public <R> Option<R> flatMap(Function<? super T, ? extends Option<R>> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return empty();
		} else {
			return mapper.apply(value);
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link OptionInt}.
	 */
	public OptionInt flatMapToInt(Function<? super T, ? extends OptionInt> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return OptionInt.empty();
		} else {
			return mapper.apply(value);
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link OptionDouble}.
	 */
	public OptionDouble flatMapToDouble(Function<? super T, ? extends OptionDouble> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return OptionDouble.empty();
		} else {
			return mapper.apply(value);
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link OptionLong}.
	 */
	public OptionLong flatMapToLong(Function<? super T, ? extends OptionLong> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (value == null) {
			return OptionLong.empty();
		} else {
			return mapper.apply(value);
		}
	}
	
	public Option<T> or(Supplier<Option<T>> other) {
		Objects.requireNonNull(other);
		return (value == null) ? Objects.requireNonNull(other.get()) : this;
	}
	
	/**
	 * Returns this value if present, otherwise the parameter.
	 */
	public T orElse(T other) {
		return (value == null) ? other : value;
	}
	
	/**
	 * Returns this value if present, otherwise the evaluated parameter.
	 */
	public T orElseGet(Supplier<? extends T> other) {
		return (value == null) ? Objects.requireNonNull(other.get()) : value;
	}
	
	public <E extends Throwable> T orElseThrow(Supplier<? extends E> exception) throws E {
		if (value == null) {
			throw exception.get();
		} else {
			return value;
		}
	}
	
	/**
	 * Converts this {@link Option} to a nullable value.
	 */
	public T orElseNull() { return value; }
	
	/**
	 * Converts this {@link Option} to a {@link java.util.Optional}.
	 */
	public Optional<T> toOptional() { return Optional.ofNullable(value); }
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private boolean done = false;
			
			@Override
			public boolean hasNext() { return done; }
			
			@Override
			public T next() {
				if (done) {
					throw new IllegalStateException("Tried to fetch more than one value from Option.Iterator");
				} else {
					done = true;
					return value;
				}
			}
		};
	}
	
	public Stream<T> stream() {
		return (value == null) ? Stream.empty() : Stream.of(value);
	}
	
	@Override
	public String toString() {
		if (value == null) {
			return "Option.empty";
		} else {
			return "Option(" + value + ")";
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Option)) {
			return false;
		}
		Option<?> other = (Option<?>) obj;
		return Objects.equals(value, other.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
