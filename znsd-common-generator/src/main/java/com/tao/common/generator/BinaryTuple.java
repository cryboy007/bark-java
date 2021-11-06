package com.tao.common.generator;

import java.io.Serializable;

public class BinaryTuple<A, B> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4995237649864226508L;
	private A first;
	private B second;

	public BinaryTuple(){}
	public BinaryTuple(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}
	
	
}