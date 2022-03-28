package com.ibm.broker.supportpac.pgp;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;

/**
 * Java Util class to generate p and g prime factors for ElGamal
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * Java Util class to generate p and g prime factors for ElGamal
 *
 */
public class PGPPrimeFactorGenerator {

	@SuppressWarnings("unused")
	private BigInteger p, g, a, r, pMinus2;
	private SecureRandom secureRnd;
	private static final int CRTTY = 300;

	@SuppressWarnings("unused")
	private BigInteger ZERO = BigInteger.ZERO;
	private BigInteger ONE = BigInteger.ONE;
	private BigInteger TWO = ONE.add(ONE);
	private BigInteger THREE = TWO.add(ONE);

	/**
	 * Public Constructor
	 */
	public PGPPrimeFactorGenerator(int keySize) { 
		
		// Generate a Random number with at least (keySize) bits in p
		secureRnd = new SecureRandom();
		PrimeGenerator primeFactor = new PrimeGenerator(keySize, CRTTY, secureRnd);
		p = primeFactor.getP();
		pMinus2 = p.subtract(TWO);
		g = primeFactor.getG();
		
		// It should be a random integer in range 1 < a < p-1
		BigInteger primeT = p.subtract(THREE);
		a = (new BigInteger(p.bitLength(), secureRnd)).mod(primeT).add(TWO);
		r = g.modPow(a, p);
	}

	public BigInteger getP() {
		return p;
	}

	public BigInteger getG() {
		return g;
	}

	public BigInteger getR() {
		return r;
	}
	
	/**
	 * 
	 * Java class to generate big prime number
	 *
	 */
	private class PrimeGenerator {
		
		private int minBits, crtty;
		private SecureRandom secureRnd;
		private BigInteger p, g;

		private BigInteger ZERO = BigInteger.ZERO;
		private BigInteger ONE = BigInteger.ONE;
		private BigInteger TWO = ONE.add(ONE);
		@SuppressWarnings("unused")
		private BigInteger THREE = TWO.add(ONE);

		/**
		 * Constructor
		 * @param bits
		 * @param crtty
		 */
		@SuppressWarnings("unused")
		PrimeGenerator(int bits, int crtty) {
			this(bits, crtty, new SecureRandom());
		}

		/**
		 * Constructor
		 * @param bits
		 * @param crtty
		 */
		PrimeGenerator(int bits, int crtty, SecureRandom secureRnd) {
			if (bits < 512){
				System.err.println("Safe primes should be at least 512 bits long");
			}

			this.minBits = bits;
			this.crtty = crtty;
			this.secureRnd = secureRnd;
			System.out.println("Generating a prime number >= "+bits+" bits");
			
			generateSafePrimeAndGenerator();
			
			System.out.println("Prime: "+getP());
		}

		/**
		 * Method to make a safe prime (stored in this.p) and a generator (this.g) mod p.
		 */
		private void generateSafePrimeAndGenerator() {
			BigInteger r = BigInteger.valueOf(0x7fffffff);
			BigInteger t = new BigInteger(minBits, crtty, secureRnd);

			// Step 1: Find a safe prime p = 2rt + 1 where r is smallish (~10^9), t prime
			do {
				r = r.add(ONE);
				p = TWO.multiply(r).multiply(t).add(ONE);
			} while (!p.isProbablePrime(crtty));

			// Step 2: Obtain the prime factorization of p-1 (quickish in view of (1))
			HashSet<BigInteger> factors = new HashSet<BigInteger>();
			factors.add(t);
			factors.add(TWO);
			if (r.isProbablePrime(crtty))
				factors.add(r);
			else
				factors.addAll(primeFact(r));

			// Step 3: look for a generator mod p. Repeatedly make a random g, 1 < g < p, until for each prime factor f of p-1, g^((p-1)/f) is incogruent to 1 mod p.
			BigInteger pMinusOne = p.subtract(ONE), z, lnr;
			boolean isGen;
			do {
				isGen = true;
				g = new BigInteger(p.bitLength() - 1, secureRnd);
				for (BigInteger f : factors) {
					z = pMinusOne.divide(f);
					lnr = g.modPow(z, p);
					if (lnr.equals(ONE)) {
						isGen = false;
						break;
					}
				}
			} while (!isGen);
			// Now g is a generator mod p
		} // end of generateSafePrimeAndGenerator() method

		/**
		 * Get Prime factor
		 * @param n
		 * @return
		 */
		private HashSet<BigInteger> primeFact(BigInteger n) {
			BigInteger nn = new BigInteger(n.toByteArray()); // clone n
			HashSet<BigInteger> factors = new HashSet<BigInteger>();
			BigInteger dvsr = TWO, dvsrSq = dvsr.multiply(dvsr);
			while (dvsrSq.compareTo(nn) <= 0) { // divisor <= sqrt of n
				if (nn.mod(dvsr).equals(ZERO)) { // found a factor (must be prime):
					factors.add(dvsr); // add it to set
					while (nn.mod(dvsr).equals(ZERO))
						// divide it out from n completely
						nn = nn.divide(dvsr); // (ensures later factors are prime)
				}
				dvsr = dvsr.add(ONE); // next possible divisor
				dvsrSq = dvsr.multiply(dvsr);
			}
			// if nn's largest prime factor had multiplicity >= 2, nn will now be 1;
			// if the multimplicity is only 1, the loop will have been exited
			// leaving nn == this prime factor;
			if (nn.compareTo(ONE) > 0)
				factors.add(nn);
			return factors;
		}

		BigInteger getP() {
			return p;
		}

		BigInteger getG() {
			return g;
		}
	}
}
