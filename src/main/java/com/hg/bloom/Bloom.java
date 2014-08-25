package com.hg.bloom;

import java.io.Serializable;
import java.util.List;

/**
 * User: rexsheng Date: Mar 18, 2008
 */
public class Bloom implements Serializable, WordCollection {
	private static final long serialVersionUID = -1277923803936186527L;
	private int N;
	private long M;
	private int n;
	private byte[] finger;

	private transient static final int K = 8;
	private transient static final int MDN = 16;

	public Bloom(int capacity) {
		N = capacity;
		resetFinger();
	}

	public int getN() {
		return n;
	}

	public void resetFinger() {
		this.M = this.N * MDN * 1l;
		finger = new byte[N * 2];
		n = 0;
	}

	public float getCapacity() {
		return 1f * n / N;
	}

	public int status() {
		int[] s = new int[MDN];
		int p = N * 2 / s.length;
		int f = 0;
		for (int i = 0; i < N * 2; i++) {
			for (int j = 0; j < K; j++) {
				if (((int) finger[i] & j) == j) {
					s[f]++;
				}
			}
			if (i % p == p - 1)
				f++;
		}
		int sum = 0;
		for (int i = 0; i < s.length; i++) {
			s[i] *= s[i];
		}
		for (int value : s) {
			sum += value;
		}
		float avg = (float) sum / s.length;
		int result = 0;
		for (int value : s) {
			result += (value - avg) * (value - avg);
		}
		return result;
	}

	public boolean add(int ahash) {
		return add(new MersenneTwisterFast(ahash));
	}

	public boolean add(String word) {
		if (word.trim().length() == 0)
			return false;
		return add(new MersenneTwisterFast(word));
	}

	public boolean contains(String word) {
		return contains(new MersenneTwisterFast(word), null);
	}

	public boolean contains(MersenneTwisterFast r, List<Long> result) {
		for (int i = 0; i < K; i++) {
			long p;
			if (result == null || result.size() <= i) {
				p = r.nextLong(M);
				if (result != null)
					result.add(p);
			} else
				p = result.get(i);
			int a = (int) p / K;
			int b = 1 << (p % K);
			if ((finger[a] & b) != b) {
				return false;
			}
		}
		return true;
	}

	private boolean add(MersenneTwisterFast r) {
		boolean isNew = false;
		for (int i = 0; i < K; i++) {
			long p = r.nextLong(M);
			int a = (int) p / K;
			int b = 1 << (p % K);
			if (!isNew && (finger[a] & b) != b) {
				isNew = true;
				if (n++ >= N) {
					System.err.println(this + " over capacity");
				}
			}
			finger[a] |= b;
		}
		return isNew;
	}
}
