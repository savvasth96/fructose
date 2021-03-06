package fwcd.fructose;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import fwcd.fructose.operations.ToleranceEquatable;

/**
 * An immutable container that may or may not hold an {@code int}.
 * 
 * <p>{@link OptionInt} is a primitive specialization of {@link Option}.</p>
 * 
 * <p>This class closely follows the public interface
 * of {@link java.util.OptionalInt}.</p>
 * 
 * <p>Unlike {@link java.util.OptionalInt} which is only designed
 * to support the optional return idiom, {@link OptionInt} is
 * intended to be used as a general-purpose "Maybe"-type that
 * may be stored and serialized too.</p>
 * 
 * <p>Additionally, {@link OptionInt} is not marked as {@code final}, which
 * allows you to create subclasses (although it should be rarely
 * needed).</p>
 */
public class OptionInt implements Serializable, Iterable<Integer>, ToleranceEquatable<OptionInt> {
	private static final long serialVersionUID = 142311444892060246L;
	private static final OptionInt EMPTY = new OptionInt(0, false);
	private final int value;
	private final boolean present;
	
	/**
	 * Internally constructs a new {@link OptionInt} instance.
	 *  
	 * @param present - Whether the value is present
	 */
	private OptionInt(int value, boolean present) {
		this.value = value;
		this.present = present;
	}
	
	/**
	 * Creates a new {@link OptionInt} instance.
	 * 
	 * @return An {@link OptionInt} wrapping this value
	 */
	public static OptionInt of(int value) {
		return new OptionInt(value, true);
	}
	
	/**
	 * Converts a {@link java.util.OptionalInt} to an {@link OptionInt}.
	 */
	public static OptionInt of(OptionalInt value) {
		return value.isPresent() ? of(value.orElse(0)) : empty();
	}
	
	/**
	 * Fetches an empty/absent {@link OptionInt} instance.
	 */
	public static OptionInt empty() {
		return EMPTY;
	}
	
	/**
	 * Checks whether this value is present.
	 */
	public boolean isPresent() {
		return present;
	}
	
	/**
	 * Invokes the consumer if the value is present
	 * and returns this {@link OptionInt}.
	 * 
	 * <p>This method is analogous to {@code IntStream.peek}
	 * and is mainly intended for debugging of call chains.
	 * Unlike {@code IntStream.peek}, this method runs synchronously
	 * and can thus be used safely for other purposes too.
	 * If no return type is needed, {@code ifPresent} should be used
	 * instead.</p>
	 */
	public OptionInt peek(IntConsumer action) {
		if (present) {
			action.accept(value);
		}
		return this;
	}
	
	/**
	 * Invokes the consumer if the value is present.
	 */
	public void ifPresent(IntConsumer then) {
		if (present) {
			then.accept(value);
		}
	}
	
	/**
	 * Invokes the first argument if the value is present,
	 * otherwise runs the second argument.
	 */
	public void ifPresentOrElse(IntConsumer then, Runnable otherwise) {
		if (present) {
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
	public int unwrap() {
		if (!present) {
			throw new NoSuchElementException("Tried to unwrap an empty OptionInt");
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
	public int unwrap(String messageIfAbsent) {
		if (!present) {
			throw new NoSuchElementException(messageIfAbsent);
		}
		
		return value;
	}
	
	/**
	 * Returns the wrapped value in case the value is present
	 * and matches the predicate, otherwise returns an empty
	 * {@link OptionInt}.
	 */
	public OptionInt filter(IntPredicate predicate) {
		Objects.requireNonNull(predicate, "Predicate can not be null");
		if (present) {
			return predicate.test(value) ? this : empty();
		} else {
			return this;
		}
	}
	
	/**
	 * Returns an {@link OptionInt} containing the result of the
	 * function if present, otherwise returns an empty {@link OptionInt}.
	 */
	public OptionInt map(IntUnaryOperator mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return of(mapper.applyAsInt(value));
		} else {
			return empty();
		}
	}
	
	/**
	 * Returns an {@link OptionDouble} containing the result of the
	 * function if present, otherwise returns an empty {@link OptionDouble}.
	 */
	public OptionDouble mapToDouble(IntToDoubleFunction mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return OptionDouble.of(mapper.applyAsDouble(value));
		} else {
			return OptionDouble.empty();
		}
	}
	
	/**
	 * Returns an {@link OptionLong} containing the result of the
	 * function if present, otherwise returns an empty {@link OptionLong}.
	 */
	public OptionLong mapToLong(IntToLongFunction mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return OptionLong.of(mapper.applyAsLong(value));
		} else {
			return OptionLong.empty();
		}
	}
	
	/**
	 * Returns an {@link Option} containing the result of the
	 * function if present, otherwise returns an empty {@link Option}.
	 */
	public <R> Option<R> mapToObj(IntFunction<? extends R> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return Option.of(mapper.apply(value));
		} else {
			return Option.empty();
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link OptionInt}.
	 */
	public OptionInt flatMap(IntFunction<? extends OptionInt> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return mapper.apply(value);
		} else {
			return empty();
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link OptionDouble}.
	 */
	public OptionDouble flatMapToDouble(IntFunction<? extends OptionDouble> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return mapper.apply(value);
		} else {
			return OptionDouble.empty();
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link OptionLong}.
	 */
	public OptionLong flatMapToLong(IntFunction<? extends OptionLong> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return mapper.apply(value);
		} else {
			return OptionLong.empty();
		}
	}
	
	/**
	 * Returns the result of the function if present,
	 * otherwise returns an empty {@link Option}.
	 */
	public <R> Option<R> flatMapToObj(IntFunction<? extends Option<R>> mapper) {
		Objects.requireNonNull(mapper, "Mapper function can not be null");
		if (present) {
			return mapper.apply(value);
		} else {
			return Option.empty();
		}
	}
	
	public OptionInt or(Supplier<OptionInt> other) {
		Objects.requireNonNull(other);
		return present ? this : Objects.requireNonNull(other.get());
	}
	
	/**
	 * Returns this value if present, otherwise the parameter.
	 */
	public int orElse(int other) {
		return present ? value : other;
	}
	
	/**
	 * Returns this value if present, otherwise the evaluated parameter.
	 */
	public int orElseGet(IntSupplier other) {
		return present ? value : other.getAsInt();
	}
	
	public <E extends Throwable> int orElseThrow(Supplier<? extends E> exception) throws E {
		if (present) {
			return value;
		} else {
			throw exception.get();
		}
	}
	
	/**
	 * Converts this {@link OptionInt} to a boxed nullable value.
	 */
	public Integer orElseNull() { return present ? value : null; }
	
	/**
	 * Converts this {@link OptionInt} to a {@link java.util.OptionalInt}.
	 */
	public OptionalInt toOptionalInt() { return present ? OptionalInt.of(value) : OptionalInt.empty(); }
	
	public Option<Integer> boxed() { return present ? Option.of(value) : Option.empty(); }
	
	public OptionDouble toOptionDouble() { return present ? OptionDouble.of(value) : OptionDouble.empty(); }
	
	public OptionLong toOptionLong() { return present ? OptionLong.of(value) : OptionLong.empty(); } 
	
	public int[] toArray() { return present ? new int[] {value} : new int[0]; }
	
	@Override
	public Iterator<Integer> iterator() {
		return present ? new SingleIterator<>(value) : Collections.emptyIterator();
	}
	
	public IntStream stream() {
		return present ? IntStream.of(value) : IntStream.empty();
	}
	
	@Override
	public String toString() {
		if (present) {
			return "OptionInt(" + value + ")";
		} else {
			return "OptionInt.empty";
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OptionInt)) {
			return false;
		}
		OptionInt other = (OptionInt) obj;
		return (present == other.present) && (value == other.value);
	}
	
	@Override
	public int hashCode() {
		return present ? Integer.hashCode(value) : 0;
	}

	@Override
	public boolean equals(OptionInt rhs, double tolerance) {
		if (rhs.isPresent()) {
			return Math.abs(rhs.value - value) <= tolerance;
		}
		return false;
	}
}
