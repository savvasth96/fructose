package fwcd.fructose.parsers.ebnf;

import java.util.Arrays;
import java.util.stream.Stream;

import fwcd.fructose.OptionInt;

/**
 * A set of tokens of which only one has to match.
 * 
 * @author Fredrik
 *
 */
public class OrCombo implements Token {
	private final Token[] tokens;

	public OrCombo(Token... tokens) {
		this.tokens = tokens;
	}
	
	@Override
	public boolean matches(Terminal... sequence) {
		for (Token token : tokens) {
			if (token.matches(sequence)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public OptionInt matchCount(Terminal... sequence) {
		for (Token token : tokens) {
			OptionInt count = token.matchCount(sequence);
			
			if (count.isPresent()) {
				return count;
			}
		}
		
		return OptionInt.empty();
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder("[");
		for (Token token : tokens) {
			res.append(token.toString());
			res.append(" | ");
		}
		res.delete(res.length() - 3, res.length());
		res.append("]");
		return res.toString();
	}

	@Override
	public Token or(Token other) {
		Token[] otherTokens;
		
		if (other instanceof OrCombo) {
			otherTokens = ((OrCombo) other).tokens;
		} else {
			otherTokens = new Token[] {other};
		}
		
		return new OrCombo(Stream.concat(Arrays.stream(tokens), Arrays.stream(otherTokens)).toArray(Token[]::new));
	}
}
