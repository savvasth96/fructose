package com.fredrikw.fructose.geometry;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * An immutable, multi-dimensional vector.
 * 
 * @author Fredrik
 *
 */
public class Vector {
	private final double[] values;
	
	public Vector(double... values) {
		this.values = values;
	}
	
	public double get(int i) {
		return values[i];
	}
	
	public int size() {
		return values.length;
	}
	
	public double length() {
		double sqSum = 0;
		
		for (double v : values) {
			sqSum += v * v;
		}
		
		return Math.sqrt(sqSum);
	}
	
	public Vector add(Vector other) {
		return combine(other, (a, b) -> a + b);
	}
	
	public Vector sub(Vector other) {
		return combine(other, (a, b) -> a - b);
	}
	
	public Vector scale(double factor) {
		return map(v -> v * factor);
	}
	
	public Vector normalize() {
		double length = length();
		return map(v -> v / length);
	}
	
	public Vector map(DoubleUnaryOperator mapper) {
		double[] result = new double[size()];
		
		for (int i=0; i<size(); i++) {
			result[i] = mapper.applyAsDouble(result[i]);
		}
		
		return new Vector(result);
	}
	
	public Vector combine(Vector other, DoubleBinaryOperator operator) {
		if (size() != other.size()) {
			throw new IllegalArgumentException("The two vectors need to have the same size!");
		}
		
		double[] result = new double[size()];
		
		for (int i=0; i<size(); i++) {
			result[i] = operator.applyAsDouble(values[i], other.values[i]);
		}
		
		return new Vector(result);
	}
	
	public double dot(Vector other) {
		if (size() != other.size()) {
			throw new IllegalArgumentException("The two vectors need to have the same size!");
		}
		
		double result = 0;
		
		for (int i=0; i<size(); i++) {
			result += values[i] * other.values[i];
		}
		
		return result;
	}
	
	public Matrix asMatrix() {
		double[][] result = new double[size()][1];
		
		for (int i=0; i<size(); i++) {
			result[i][0] = values[i];
		}
		
		return new Matrix(result);
	}
	
	public Vector2D asVector2D() {
		if (size() != 2) {
			throw new UnsupportedOperationException("Can't convert a vector of length " + Integer.toString(size()) + " to Vector2D!");
		}
		
		return new Vector2D(values[0], values[1]);
	}
	
	public Vector3D asVector3D() {
		if (size() != 3) {
			throw new UnsupportedOperationException("Can't convert a vector of length " + Integer.toString(size()) + " to Vector3D!");
		}
		
		return new Vector3D(values[0], values[1], values[2]);
	}
}
