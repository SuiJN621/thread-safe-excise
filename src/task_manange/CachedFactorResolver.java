package task_manange;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author Sui
 * @date 2018.10.30 11:29
 */
public class CachedFactorResolver {

    private volatile OneValueCache oneValueCache = new OneValueCache(null, null);

    public BigInteger[] getFactors(BigInteger number){
        BigInteger[] factors = oneValueCache.getFactors(number);
        if (factors == null) {
           //do extract factors = factor(number);
           oneValueCache = new OneValueCache(factors, number);
        }
        return factors;
    }

    private class OneValueCache {
        private final BigInteger[] factors;
        private final BigInteger lastNumber;

        public OneValueCache(BigInteger[] factors, BigInteger lastNumber) {
            this.factors = Arrays.copyOf(factors, factors.length);
            this.lastNumber = lastNumber;
        }

        public BigInteger[] getFactors(BigInteger number){
            if (number == null || !number.equals(lastNumber)) {
                return null;
            } else {
                return Arrays.copyOf(factors, factors.length);
            }
        }
    }
}
