package com.labijie.application.captcha.utils

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
class Quant(thepic: ByteArray, len: Int, sample: Int) {
    protected var alphadec: Int = 0 /* biased by 10 bits */

    /* Types and Global Variables
	-------------------------- */
    protected var thepicture: ByteArray /* the input image itself */
    protected var lengthcount: Int /* lengthcount = H*W*3 */

    protected var samplefac: Int /* sampling factor 1..30 */

    //   typedef int pixel[4];                /* BGRc */
    protected var network: Array<IntArray> /* the network itself - [netsize][4] */

    protected var netindex: IntArray = IntArray(256)

    /* for network lookup - really 256 */
    protected var bias: IntArray = IntArray(netsize)

    /* bias and freq arrays for learning */
    protected var freq: IntArray = IntArray(netsize)
    protected var radpower: IntArray = IntArray(initrad)

    /* radpower for precomputation */ /* Initialise network in range (0,0,0) to (255,255,255) and set parameters
       ----------------------------------------------------------------------- */
    init {
        var i: Int
        var p: IntArray

        thepicture = thepic
        lengthcount = len
        samplefac = sample

        val list = mutableListOf<IntArray>()

        i = 0
        while (i < netsize) {
            p = IntArray(4)
            p[2] = (i shl (netbiasshift + 8)) / netsize
            p[1] = p[2]
            p[0] = p[1]
            freq[i] = intbias / netsize /* 1/netsize */
            bias[i] = 0
            list.add(p)
            i++
        }
        network = list.toTypedArray()
    }

    fun colorMap(): ByteArray {
        val map = ByteArray(3 * netsize)
        val index = IntArray(netsize)
        for (i in 0..<netsize) index[network[i][3]] = i
        var k = 0
        for (i in 0..<netsize) {
            val j = index[i]
            map[k++] = (network[j][0]).toByte()
            map[k++] = (network[j][1]).toByte()
            map[k++] = (network[j][2]).toByte()
        }
        return map
    }

    /* Insertion sort of network and building of netindex[0..255] (to do after unbias)
       ------------------------------------------------------------------------------- */
    fun inxbuild() {
        var i: Int
        var j: Int
        var smallpos: Int
        var smallval: Int
        var p: IntArray
        var q: IntArray
        var previouscol: Int
        var startpos: Int

        previouscol = 0
        startpos = 0
        i = 0
        while (i < netsize) {
            p = network[i]
            smallpos = i
            smallval = p[1] /* index on g */
            /* find smallest in i..netsize-1 */
            j = i + 1
            while (j < netsize) {
                q = network[j]
                if (q[1] < smallval) { /* index on g */
                    smallpos = j
                    smallval = q[1] /* index on g */
                }
                j++
            }
            q = network[smallpos]
            /* swap p (i) and q (smallpos) entries */
            if (i != smallpos) {
                j = q[0]
                q[0] = p[0]
                p[0] = j
                j = q[1]
                q[1] = p[1]
                p[1] = j
                j = q[2]
                q[2] = p[2]
                p[2] = j
                j = q[3]
                q[3] = p[3]
                p[3] = j
            }
            /* smallval entry is now in position i */
            if (smallval != previouscol) {
                netindex[previouscol] = (startpos + i) shr 1
                j = previouscol + 1
                while (j < smallval) {
                    netindex[j] = i
                    j++
                }
                previouscol = smallval
                startpos = i
            }
            i++
        }
        netindex[previouscol] = (startpos + maxnetpos) shr 1
        j = previouscol + 1
        while (j < 256) {
            netindex[j] = maxnetpos /* really 256 */
            j++
        }
    }

    /* Main Learning Loop
       ------------------ */
    fun learn() {
        var i: Int
        var j: Int
        var b: Int
        var g: Int
        var r: Int
        var radius: Int
        var rad: Int
        var alpha: Int
        val step: Int
        var delta: Int
        val samplepixels: Int
        val p: ByteArray
        var pix: Int
        val lim: Int

        if (lengthcount < minpicturebytes) samplefac = 1
        alphadec = 30 + ((samplefac - 1) / 3)
        p = thepicture
        pix = 0
        lim = lengthcount
        samplepixels = lengthcount / (3 * samplefac)
        delta = samplepixels / ncycles
        alpha = initalpha
        radius = initradius

        rad = radius shr radiusbiasshift
        if (rad <= 1) rad = 0
        i = 0
        while (i < rad) {
            radpower[i] =
                alpha * (((rad * rad - i * i) * radbias) / (rad * rad))
            i++
        }

        //fprintf(stderr,"beginning 1D learning: initial radius=%d\n", rad);
        if (lengthcount < minpicturebytes) step = 3
        else if ((lengthcount % prime1) != 0) step = 3 * prime1
        else {
            if ((lengthcount % prime2) != 0) step = 3 * prime2
            else {
                if ((lengthcount % prime3) != 0) step = 3 * prime3
                else step = 3 * prime4
            }
        }

        i = 0
        while (i < samplepixels) {
            b = (p[pix + 0].toInt() and 0xff) shl netbiasshift
            g = (p[pix + 1].toInt() and 0xff) shl netbiasshift
            r = (p[pix + 2].toInt() and 0xff) shl netbiasshift
            j = contest(b, g, r)

            altersingle(alpha, j, b, g, r)
            if (rad != 0) alterneigh(rad, j, b, g, r) /* alter neighbours */

            pix += step
            if (pix >= lim) pix -= lengthcount

            i++
            if (delta == 0) delta = 1
            if (i % delta == 0) {
                alpha -= alpha / alphadec
                radius -= radius / radiusdec
                rad = radius shr radiusbiasshift
                if (rad <= 1) rad = 0
                j = 0
                while (j < rad) {
                    radpower[j] =
                        alpha * (((rad * rad - j * j) * radbias) / (rad * rad))
                    j++
                }
            }
        }
        //fprintf(stderr,"finished 1D learning: final alpha=%f !\n",((float)alpha)/initalpha);
    }

    /* Search for BGR values 0..255 (after net is unbiased) and return colour index
       ---------------------------------------------------------------------------- */
    fun map(b: Int, g: Int, r: Int): Int {
        var i: Int
        var j: Int
        var dist: Int
        var a: Int
        var bestd: Int
        var p: IntArray
        var best: Int

        bestd = 1000 /* biggest possible dist is 256*3 */
        best = -1
        i = netindex[g] /* index on g */
        j = i - 1 /* start at netindex[g] and work outwards */

        while ((i < netsize) || (j >= 0)) {
            if (i < netsize) {
                p = network[i]
                dist = p[1] - g /* inx key */
                if (dist >= bestd) i = netsize /* stop iter */
                else {
                    i++
                    if (dist < 0) dist = -dist
                    a = p[0] - b
                    if (a < 0) a = -a
                    dist += a
                    if (dist < bestd) {
                        a = p[2] - r
                        if (a < 0) a = -a
                        dist += a
                        if (dist < bestd) {
                            bestd = dist
                            best = p[3]
                        }
                    }
                }
            }
            if (j >= 0) {
                p = network[j]
                dist = g - p[1] /* inx key - reverse dif */
                if (dist >= bestd) j = -1 /* stop iter */
                else {
                    j--
                    if (dist < 0) dist = -dist
                    a = p[0] - b
                    if (a < 0) a = -a
                    dist += a
                    if (dist < bestd) {
                        a = p[2] - r
                        if (a < 0) a = -a
                        dist += a
                        if (dist < bestd) {
                            bestd = dist
                            best = p[3]
                        }
                    }
                }
            }
        }
        return (best)
    }

    fun process(): ByteArray {
        learn()
        unbiasnet()
        inxbuild()
        return colorMap()
    }

    /* Unbias network to give byte values 0..255 and record position i to prepare for sort
       ----------------------------------------------------------------------------------- */
    fun unbiasnet() {
        var i = 0
        while (i < netsize) {
            network[i][0] = network[i][0] shr netbiasshift
            network[i][1] = network[i][1] shr netbiasshift
            network[i][2] = network[i][2] shr netbiasshift
            network[i][3] = i /* record colour no */
            i++
        }
    }

    /* Move adjacent neurons by precomputed alpha*(1-((i-j)^2/[r]^2)) in radpower[|i-j|]
       --------------------------------------------------------------------------------- */
    protected fun alterneigh(rad: Int, i: Int, b: Int, g: Int, r: Int) {
        var j: Int
        var k: Int
        var lo: Int
        var hi: Int
        var a: Int
        var m: Int
        var p: IntArray

        lo = i - rad
        if (lo < -1) lo = -1
        hi = i + rad
        if (hi > netsize) hi = netsize

        j = i + 1
        k = i - 1
        m = 1
        while ((j < hi) || (k > lo)) {
            a = radpower[m++]
            if (j < hi) {
                p = network[j++]
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias
                    p[1] -= (a * (p[1] - g)) / alpharadbias
                    p[2] -= (a * (p[2] - r)) / alpharadbias
                } catch (e: Exception) {
                } // prevents 1.3 miscompilation
            }
            if (k > lo) {
                p = network[k--]
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias
                    p[1] -= (a * (p[1] - g)) / alpharadbias
                    p[2] -= (a * (p[2] - r)) / alpharadbias
                } catch (_: Exception) {
                }
            }
        }
    }

    /* Move neuron i towards biased (b,g,r) by factor alpha
       ---------------------------------------------------- */
    protected fun altersingle(alpha: Int, i: Int, b: Int, g: Int, r: Int) {
        /* alter hit neuron */

        val n = network[i]
        n[0] -= (alpha * (n[0] - b)) / initalpha
        n[1] -= (alpha * (n[1] - g)) / initalpha
        n[2] -= (alpha * (n[2] - r)) / initalpha
    }

    /* Search for biased BGR values
       ---------------------------- */
    protected fun contest(b: Int, g: Int, r: Int): Int {
        /* finds closest neuron (min dist) and updates freq */
        /* finds best neuron (min dist-bias) and returns position */
        /* for frequently chosen neurons, freq[i] is high and bias[i] is negative */
        /* bias[i] = gamma*((1/netsize)-freq[i]) */

        var i: Int
        var dist: Int
        var a: Int
        var biasdist: Int
        var betafreq: Int
        var bestpos: Int
        var bestbiaspos: Int
        var bestd: Int
        var bestbiasd: Int
        var n: IntArray

        bestd = (1 shl 31).inv()
        bestbiasd = bestd
        bestpos = -1
        bestbiaspos = bestpos

        i = 0
        while (i < netsize) {
            n = network[i]
            dist = n[0] - b
            if (dist < 0) dist = -dist
            a = n[1] - g
            if (a < 0) a = -a
            dist += a
            a = n[2] - r
            if (a < 0) a = -a
            dist += a
            if (dist < bestd) {
                bestd = dist
                bestpos = i
            }
            biasdist = dist - ((bias[i]) shr (intbiasshift - netbiasshift))
            if (biasdist < bestbiasd) {
                bestbiasd = biasdist
                bestbiaspos = i
            }
            betafreq = (freq[i] shr betashift)
            freq[i] -= betafreq
            bias[i] += (betafreq shl gammashift)
            i++
        }
        freq[bestpos] += beta
        bias[bestpos] -= betagamma
        return (bestbiaspos)
    }

    companion object {
        protected const val netsize: Int = 256 /* number of colours used */

        /* four primes near 500 - assume no image has a length so large */ /* that it is divisible by all four primes */
        protected const val prime1: Int = 499
        protected const val prime2: Int = 491
        protected const val prime3: Int = 487
        protected const val prime4: Int = 503

        protected val minpicturebytes: Int = (3 * prime4)

        /* minimum size for input image */ /* Program Skeleton
	   ----------------
	   [select samplefac in range 1..30]
	   [read image from input file]
	   pic = (unsigned char*) malloc(3*width*height);
	   initnet(pic,3*width*height,samplefac);
	   learn();
	   unbiasnet();
	   [write output image header, using writecolourmap(f)]
	   inxbuild();
	   write output image using inxsearch(b,g,r)      */
        /* Network Definitions
	   ------------------- */
        protected val maxnetpos: Int = (netsize - 1)
        protected const val netbiasshift: Int = 4 /* bias for colour values */
        protected const val ncycles: Int = 100 /* no. of learning cycles */

        /* defs for freq and bias */
        protected const val intbiasshift: Int = 16 /* bias for fractions */
        protected val intbias: Int = (1 shl intbiasshift)
        protected const val gammashift: Int = 10 /* gamma = 1024 */
        protected val gamma: Int = (1 shl gammashift)
        protected const val betashift: Int = 10
        protected val beta: Int = (intbias shr betashift) /* beta = 1/1024 */
        protected val betagamma: Int = (intbias shl (gammashift - betashift))

        /* defs for decreasing radius factor */
        protected val initrad: Int = (netsize shr 3) /* for 256 cols, radius starts */
        protected const val radiusbiasshift: Int = 6 /* at 32.0 biased by 6 bits */
        protected val radiusbias: Int = (1 shl radiusbiasshift)
        protected val initradius: Int = (initrad * radiusbias) /* and decreases by a */
        protected const val radiusdec: Int = 30 /* factor of 1/30 each cycle */

        /* defs for decreasing alpha factor */
        protected const val alphabiasshift: Int = 10 /* alpha starts at 1.0 */
        protected val initalpha: Int = (1 shl alphabiasshift)

        /* radbias and alpharadbias used for radpower calculation */
        protected const val radbiasshift: Int = 8
        protected val radbias: Int = (1 shl radbiasshift)
        protected val alpharadbshift: Int = (alphabiasshift + radbiasshift)
        protected val alpharadbias: Int = (1 shl alpharadbshift)
    }
}