package fwcd.fructose.ml.function;

import java.io.Serializable;

@FunctionalInterface
public interface WeightInit extends Serializable {
	float getWeight(int inputNeurons, int outputNeurons);
	
	default WeightInit scale(int factor) {
		return (i, o) -> getWeight(factor, o) * factor;
	}
	
	default WeightInit add(int x) {
		return (i, o) -> getWeight(i, o) + x;
	}
}
