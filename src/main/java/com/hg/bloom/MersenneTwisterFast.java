package com.hg.bloom;

import java.io.Serializable;

/**
 * User: rexsheng
 * Date: 2007-7-30
 * Time: 12:28:52
 */
public class MersenneTwisterFast implements Serializable, Cloneable {
	private static final long serialVersionUID = -2663744287985891278L;
	// Period parameters
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df;   //    private static final * constant vector a
    private static final int UPPER_MASK = 0x80000000; // most significant w-r bits
    private static final int LOWER_MASK = 0x7fffffff; // least significant r bits


    // Tempering parameters
    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;

    private int mt[]; // the array for the state vector
    private int mti; // mti==N+1 means mt[N] is not initialized
    private int mag01[];

    public MersenneTwisterFast(final String word) {
        setSeed(toInt(word));
    }

    public MersenneTwisterFast(final int ahash) {
        setSeed(new int[]{ahash});
    }

    /**
     * Initalize the pseudo random number generator.  Don't
     * pass in a long that's bigger than an int (Mersenne Twister
     * only uses the first 32 bits for its seed).
     */

    synchronized public void setSeed(final long seed) {
        mt = new int[N];

        mag01 = new int[2];
        mag01[0] = 0x0;
        mag01[1] = MATRIX_A;

        mt[0] = (int) (seed & 0xffffffff);
        for (mti = 1; mti < N; mti++) {
            mt[mti] =
                    (1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
            /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
            /* In the previous versions, MSBs of the seed affect   */
            /* only MSBs of the array mt[].                        */
            /* 2002/01/09 modified by Makoto Matsumoto             */
            mt[mti] &= 0xffffffff;
            /* for >32 bit machines */
        }
    }


    /**
     * Sets the seed of the MersenneTwister using an array of integers.
     * Your array must have a non-zero length.  Only the first 624 integers
     * in the array are used; if the array is shorter than this then
     * integers are repeatedly used in a wrap-around fashion.
     */
    synchronized public void setSeed(final int[] array) {
        if (array.length == 0)
            throw new IllegalArgumentException("Array length must be greater than zero");
        int i, j, k;
        setSeed(19801027);
        i = 1;
        j = 0;
        k = (N > array.length ? N : array.length);
        for (; k != 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1664525)) + array[j] + j; /* non linear */
            mt[i] &= 0xffffffff; /* for WORDSIZE > 32 machines */
            i++;
            j++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
            if (j >= array.length) j = 0;
        }
        for (k = N - 1; k != 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1566083941)) - i; /* non linear */
            mt[i] &= 0xffffffff; /* for WORDSIZE > 32 machines */
            i++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
        }
        mt[0] = 0x80000000; /* MSB is 1; assuring non-zero initial array */
    }

    /**
     * Returns a long drawn uniformly from 0 to n-1.  Suffice it to say,
     * n must be > 0, or an IllegalArgumentException is raised.
     */
    public final long nextLong(final long n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be > 0");

        long bits, val;
        do {
            int y;
            int z;

            if (mti >= N)   // threshold N words at one time
            {
                int kk;
                final int[] mt = this.mt; // locals are slightly faster
                final int[] mag01 = this.mag01; // locals are slightly faster

                for (kk = 0; kk < N - M; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                for (; kk < N - 1; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
                mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

                mti = 0;
            }

            y = mt[mti++];
            y ^= y >>> 11;                          // TEMPERING_SHIFT_U(y)
            y ^= (y << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(y)
            y ^= (y << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(y)
            y ^= (y >>> 18);                        // TEMPERING_SHIFT_L(y)

            if (mti >= N)   // threshold N words at one time
            {
                int kk;
                final int[] mt = this.mt; // locals are slightly faster
                final int[] mag01 = this.mag01; // locals are slightly faster

                for (kk = 0; kk < N - M; kk++) {
                    z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + M] ^ (z >>> 1) ^ mag01[z & 0x1];
                }
                for (; kk < N - 1; kk++) {
                    z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + (M - N)] ^ (z >>> 1) ^ mag01[z & 0x1];
                }
                z = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
                mt[N - 1] = mt[M - 1] ^ (z >>> 1) ^ mag01[z & 0x1];

                mti = 0;
            }

            z = mt[mti++];
            z ^= z >>> 11;                          // TEMPERING_SHIFT_U(z)
            z ^= (z << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(z)
            z ^= (z << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(z)
            z ^= (z >>> 18);                        // TEMPERING_SHIFT_L(z)

            bits = (((((long) y) << 32) + (long) z) >>> 1);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }

    private static int[] toInt(String key) {
        char[] b = key.toCharArray();//UCS-2
        boolean b1 = b.length % 2 == 0;
        int[] mtf = new int[b1 ? (b.length / 2) : (b.length / 2 + 1)];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            if (i % 2 == 1) mtf[i / 2] = j ^ b[i];
            else j = b[i] << 16;
        }
        if (!b1) mtf[mtf.length - 1] = j;
        return mtf;
    }
}
