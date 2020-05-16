[TOC]



# Java之道系列：BigDecimal如何解决浮点数精度问题

 

如题，今天我们来看下[java.math.BigDecimal](http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html)是如何解决[浮点数的精度问题](http://en.wikipedia.org/wiki/Floating_point#Accuracy_problems)的，在那之前当然得先了解下浮点数精度问题是什么问题了。下面我们先从[IEEE 754](http://grouper.ieee.org/groups/754/)说起。

## IEEE 754

> IEEE二进制浮点数算术标准（IEEE 754）是20世纪80年代以来最广泛使用的浮点数运算标准，为许多CPU与浮点运算器所采用。这个标准定义了表示浮点数的格式（包括负零-0）与反常值（denormal number）），一些特殊数值（无穷（Inf）与非数值（NaN）），以及这些数值的“浮点数运算符”；它也指明了四种数值舍入规则和五种异常状况（包括异常发生的时机与处理方式）。

下面我们就以双精度，也就是double类型，为例来看看浮点数的格式。

![img](image-201806282204/General_floating_point_frac.svg)

| sign | exponent                 | fraction |
| ---- | ------------------------ | -------- |
| 1位  | 11位                     | 52位     |
| 63   | 62-52实际的指数大小+1023 | 51-0     |

下面看个栗子，直接输出double类型的二进制表示，

```
    public static void main(String[] args) {
        printBits(3.5);
    }

    private static void printBits(double d) {
        System.out.println("##"+d);
        long l = Double.doubleToLongBits(d);
        String bits = Long.toBinaryString(l);
        int len = bits.length();
        System.out.println(bits+"#"+len);
        if(len == 64) {
            System.out.println("[63]"+bits.charAt(0));
            System.out.println("[62-52]"+bits.substring(1,12));
            System.out.println("[51-0]"+bits.substring(12, 64));
        } else {
            System.out.println("[63]0");
            System.out.println("[62-52]"+ pad(bits.substring(0, len - 52)));
            System.out.println("[51-0]"+bits.substring(len-52, len));
        }
    }

    private static String pad(String exp) {
        int len = exp.length();
        if(len == 11) {
            return exp;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 11-len; i > 0; i--) {
                sb.append("0");
            }
            sb.append(exp);
            return sb.toString();
        }
    } 
```

```
##3.5
100000000001100000000000000000000000000000000000000000000000000#63
[63]0
[62-52]10000000000
[51-0]110000000000000000000000000000000000000000000000000012345
```

指数大小为`10000000000B-1023=1`，尾数为`1.11B`，所以实际数值大小为`11.1B=3.5`，妥妥的。 
有一点需要注意的是上述格式为[归约形式](http://zh.wikipedia.org/zh-cn/IEEE_754#.E8.A7.84.E7.BA.A6.E5.BD.A2.E5.BC.8F.E7.9A.84.E6.B5.AE.E7.82.B9.E6.95.B0)，所以尾数的整数部分为1，而当[非归约形式](http://zh.wikipedia.org/zh-cn/IEEE_754#.E9.9D.9E.E8.A7.84.E7.BA.A6.E5.BD.A2.E5.BC.8F.E7.9A.84.E6.B5.AE.E7.82.B9.E6.95.B0)时，尾数的整数部分是为0的。

## 0.1 Orz

上面我们使用的浮点数3.5刚好可以准确的用二进制来表示，2121+2020+2−12−1，但并不是所有的小数都可以用二进制来表示，例如，0.1。

```
    public static void main(String[] args) {
        printBits(0.1);
    } 
```

```
##0.1
11111110111001100110011001100110011001100110011001100110011010#62
[63]0
[62-52]01111111011
[51-0]100110011001100110011001100110011001100110011001101012345
```

0.1无法表示成2x2x+2y2y+… 这样的形式，尾数部分后面应该是1100一直循环下去（纯属猜测，不过这个应该也是可以证明的），但是由于计算机无法表示这样的无限循环，所以就需要截断，这就是浮点数的精度问题。精度问题会带来一些unexpected的问题，例如`0.1 + 0.1 + 0.1 == 0.3`将会返回false，

```
    public static void main(String[] args) {
        System.out.println(0.1 + 0.1 == 0.2); // true
        System.out.println(0.1 + 0.1 + 0.1 == 0.3); // false
    } 
```

那么BigDecimal又是如何解决这个问题的？

## BigDecimal

BigDecimal的解决方案就是，不使用二进制，而是使用十进制（BigInteger）+小数点位置(scale)来表示小数，

```
    public static void main(String[] args) {
        BigDecimal bd = new BigDecimal("100.001");
        System.out.println(bd.scale());
        System.out.println(bd.unscaledValue());
    } 
```

输出，

```
3
100001
```

也就是`100.001 = 100001 * 0.1^3`。这种表示方式下，避免了小数的出现，当然也就不会有精度问题了。十进制，也就是整数部分使用了[BigInteger](http://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html)来表示，小数点位置只需要一个整数scale来表示就OK了。 
当使用BigDecimal来进行运算时，也就可以分解成两部分，BigInteger间的运算，以及小数点位置scale的更新，下面先看下运算过程中scale的更新。

## scale

加法运算时，根据下面的公式scale更新为两个BigDecimal中较大的那个scale即可。
$$
X*0.1n + Y*0.1m == X*0.1n + (Y*0.1m−n) * 0.1n == (X+Y*0.1m−n) * 0.1n，其中n>m
$$
相应的代码如下，

```
    /**
     * Returns a {@code BigDecimal} whose value is {@code (this +
     * augend)}, and whose scale is {@code max(this.scale(),
     * augend.scale())}.
     *
     * @param  augend value to be added to this {@code BigDecimal}.
     * @return {@code this + augend}
     */
    public BigDecimal add(BigDecimal augend) {
        long xs = this.intCompact;
        long ys = augend.intCompact;
        BigInteger fst = (xs != INFLATED) ? null : this.intVal;
        BigInteger snd = (ys != INFLATED) ? null : augend.intVal;
        int rscale = this.scale;

        long sdiff = (long)rscale - augend.scale;
        if (sdiff != 0) {
            if (sdiff < 0) {
                int raise = checkScale(-sdiff);
                rscale = augend.scale;
                if (xs == INFLATED ||
                    (xs = longMultiplyPowerTen(xs, raise)) == INFLATED)
                    fst = bigMultiplyPowerTen(raise);
            } else {
                int raise = augend.checkScale(sdiff);
                if (ys == INFLATED ||
                    (ys = longMultiplyPowerTen(ys, raise)) == INFLATED)
                    snd = augend.bigMultiplyPowerTen(raise);
            }
        }
        if (xs != INFLATED && ys != INFLATED) {
            long sum = xs + ys;
            // See "Hacker's Delight" section 2-12 for explanation of
            // the overflow test.
            if ( (((sum ^ xs) & (sum ^ ys))) >= 0L) // not overflowed
                return BigDecimal.valueOf(sum, rscale);
        }
        if (fst == null)
            fst = BigInteger.valueOf(xs);
        if (snd == null)
            snd = BigInteger.valueOf(ys);
        BigInteger sum = fst.add(snd);
        return (fst.signum == snd.signum) ?
            new BigDecimal(sum, INFLATED, rscale, 0) :
            new BigDecimal(sum, rscale);
    } 
```

乘法运算根据下面的公式也可以确定scale更新为两个scale之和。

------

$$
X*0.1n * Y*0.1m == (X*Y)*0.1n+m
$$

------

相应的代码，

```
    /**
     * Returns a {@code BigDecimal} whose value is <tt>(this &times;
     * multiplicand)</tt>, and whose scale is {@code (this.scale() +
     * multiplicand.scale())}.
     *
     * @param  multiplicand value to be multiplied by this {@code BigDecimal}.
     * @return {@code this * multiplicand}
     */
    public BigDecimal multiply(BigDecimal multiplicand) {
        long x = this.intCompact;
        long y = multiplicand.intCompact;
        int productScale = checkScale((long)scale + multiplicand.scale);

        // Might be able to do a more clever check incorporating the
        // inflated check into the overflow computation.
        if (x != INFLATED && y != INFLATED) {
            /*
             * If the product is not an overflowed value, continue
             * to use the compact representation.  if either of x or y
             * is INFLATED, the product should also be regarded as
             * an overflow. Before using the overflow test suggested in
             * "Hacker's Delight" section 2-12, we perform quick checks
             * using the precision information to see whether the overflow
             * would occur since division is expensive on most CPUs.
             */
            long product = x * y;
            long prec = this.precision() + multiplicand.precision();
            if (prec < 19 || (prec < 21 && (y == 0 || product / y == x)))
                return BigDecimal.valueOf(product, productScale);
            return new BigDecimal(BigInteger.valueOf(x).multiply(y), INFLATED,
                                  productScale, 0);
        }
        BigInteger rb;
        if (x == INFLATED && y == INFLATED)
            rb = this.intVal.multiply(multiplicand.intVal);
        else if (x != INFLATED)
            rb = multiplicand.intVal.multiply(x);
        else
            rb = this.intVal.multiply(y);
        return new BigDecimal(rb, INFLATED, productScale, 0);
    } 
```

## BigInteger

BigInteger可以表示任意精度的整数。当你使用long类型进行运算，可能会产生溢出时就要考虑使用BigInteger了。BigDecimal就使用了BigInteger作为backend。 
那么BigInteger是如何做到可以表示任意精度的整数的？答案是使用**数组**来表示，看下面这个栗子就很直观了，

```
    public static void main(String[] args) {
        byte[] mag = {
                2, 1 // 10 00000001 == 513
        };
        System.out.println(new BigInteger(mag));
    } 
```

通过byte[]来当作底层的二进制表示，例如栗子中的[2, 1]，也就是[00000010B, 00000001B]，就是表示二进制的10 00000001B这个数，也就是513了。 
BigInteger内部会将这个byte[]转换成int[]保存，代码在[stripLeadingZeroBytes](http://hg.openjdk.java.net/jdk7u/jdk7u/jdk/file/70e3553d9d6e/src/share/classes/java/math/BigInteger.java#l2832)方法，

```
    /**
     * Translates a byte array containing the two's-complement binary
     * representation of a BigInteger into a BigInteger.  The input array is
     * assumed to be in <i>big-endian</i> byte-order: the most significant
     * byte is in the zeroth element.
     *
     * @param  val big-endian two's-complement binary representation of
     *         BigInteger.
     * @throws NumberFormatException {@code val} is zero bytes long.
     */
    public BigInteger(byte[] val) {
        if (val.length == 0)
            throw new NumberFormatException("Zero length BigInteger");

        if (val[0] < 0) {
            mag = makePositive(val);
            signum = -1;
        } else {
            mag = stripLeadingZeroBytes(val);
            signum = (mag.length == 0 ? 0 : 1);
        }
    } 
```

```
    /**
     * Returns a copy of the input array stripped of any leading zero bytes.
     */
    private static int[] stripLeadingZeroBytes(byte a[]) {
        int byteLength = a.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; keep < byteLength && a[keep]==0; keep++)
            ;

        // Allocate new array and copy relevant part of input array
        int intLength = ((byteLength - keep) + 3) >>> 2;
        int[] result = new int[intLength];
        int b = byteLength - 1;
        for (int i = intLength-1; i >= 0; i--) {
            result[i] = a[b--] & 0xff;
            int bytesRemaining = b - keep + 1;
            int bytesToTransfer = Math.min(3, bytesRemaining);
            for (int j=8; j <= (bytesToTransfer << 3); j += 8)
                result[i] |= ((a[b--] & 0xff) << j);
        }
        return result;
    } 
```

上面也可以看到这个byte[]应该是`big-endian two's-complement binary representation`。 
那么为什么构造函数不直接让我们扔一个int[]进去就得了呢，还要这么转换一下？答案是因为Java的整数都是有符号整数，举个栗子，int类型没办法表示232−1232−1，也就是32位上全都是1这个数的，这时候用byte[]得这么写，`(byte)255,(byte)255,(byte)255,(byte)255`，这样才能表示32个1。

------

最后来看看BigInteger间的加法与乘法运算。

### add

代码如下，

```
    private static int[] add(int[] x, int[] y) {
        // If x is shorter, swap the two arrays
        if (x.length < y.length) {
            int[] tmp = x;
            x = y;
            y = tmp;
        }

        int xIndex = x.length;
        int yIndex = y.length;
        int result[] = new int[xIndex];
        long sum = 0;

        // Add common parts of both numbers
        while(yIndex > 0) {
            // 最低位对齐再开始加
            sum = (x[--xIndex] & LONG_MASK) +
                  (y[--yIndex] & LONG_MASK) + (sum >>> 32); // sum>>>32 是高32位，也就是进位
            result[xIndex] = (int)sum; // 低32位直接保存
        }

        // Copy remainder of longer number while carry propagation is required
        boolean carry = (sum >>> 32 != 0);
        while (xIndex > 0 && carry) // x比y长，且最后还有进位
            carry = ((result[--xIndex] = x[xIndex] + 1) == 0); // 一位一位往前进位，直到没有产生进位

        // Copy remainder of longer number
        while (xIndex > 0)
            result[--xIndex] = x[xIndex];

        // Grow result if necessary
        if (carry) {
            int bigger[] = new int[result.length + 1];
            System.arraycopy(result, 0, bigger, 1, result.length);
            bigger[0] = 0x01;
            return bigger;
        }
        return result;
    } 
```

加法运算比较简单，就是模拟十进制加法运算的过程，从两个加数的最低位开始加，如果有进位就进位。

### multiply

代码如下，

```
    private int[] multiplyToLen(int[] x, int xlen, int[] y, int ylen, int[] z) {
        int xstart = xlen - 1;
        int ystart = ylen - 1;

        if (z == null || z.length < (xlen+ ylen))
            z = new int[xlen+ylen];

        long carry = 0;
        for (int j=ystart, k=ystart+1+xstart; j>=0; j--, k--) {
            long product = (y[j] & LONG_MASK) *
                           (x[xstart] & LONG_MASK) + carry;
            z[k] = (int)product;
            carry = product >>> 32;
        }
        z[xstart] = (int)carry;

        for (int i = xstart-1; i >= 0; i--) {
            carry = 0;
            for (int j=ystart, k=ystart+1+i; j>=0; j--, k--) {
                long product = (y[j] & LONG_MASK) *
                               (x[i] & LONG_MASK) +
                               (z[k] & LONG_MASK) + carry;
                z[k] = (int)product;
                carry = product >>> 32;
            }
            z[i] = (int)carry;
        }
        return z;
    } 
```

乘法运算要复杂一点，不过也一样是模拟十进制乘法运算，也就是一个乘数的每一位与另一个乘数的每一位相乘再相加（乘法运算可以拆成加法运算），所以才有那个双重的for循环。 
最后的最后，想说的一点是，其实BigInteger可以看成是232232进制的计数表示，这样就比较容易理解上面的加法跟乘法运算了。至于为什么是232232进制？自己再想想哈^_^

## 参考资料

- <http://zh.wikipedia.org/wiki/IEEE_754>
- <http://en.wikipedia.org/wiki/Floating_point>





https://blog.csdn.net/kisimple/article/details/43773899