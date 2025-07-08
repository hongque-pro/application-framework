package com.labijie.application.captcha.utils

import java.io.IOException
import java.io.OutputStream
import kotlin.math.max

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
class Encoder internal constructor(width: Int, height: Int, pixels: ByteArray, colorDepth: Int) {
    // 图片的宽高
    private val imgW: Int = width
    private val imgH: Int = height
    private val pixAry: ByteArray = pixels
    private val initCodeSize: Int = max(2, colorDepth) // 验证码位数
    private var remaining = 0 // 剩余数量
    private var curPixel = 0 // 像素

    var n_bits: Int = 0 // number of bits/code
    var maxbits: Int = BITS // user settable max # bits/code
    var maxcode: Int = 0 // maximum code, given n_bits
    var maxmaxcode: Int = 1 shl BITS // should NEVER generate this code

    var htab: IntArray = IntArray(HSIZE)
    var codetab: IntArray = IntArray(HSIZE)

    var hsize: Int = HSIZE // for dynamic table sizing

    var free_ent: Int = 0 // first unused entry

    // block compression parameters -- after all codes are used up,
    // and compression rate changes, start over.
    var clear_flg: Boolean = false

    // Algorithm:  use open addressing double hashing (no chaining) on the
    // prefix code / next character combination.  We do a variant of Knuth's
    // algorithm D (vol. 3, sec. 6.4) along with G. Knott's relatively-prime
    // secondary probe.  Here, the modular division first probe is gives way
    // to a faster exclusive-or manipulation.  Also do block compression with
    // an adaptive reset, whereby the code table is cleared when the compression
    // ratio decreases, but after the table fills.  The variable-length output
    // codes are re-sized at this point, and a special CLEAR code is generated
    // for the decompressor.  Late addition:  construct the table according to
    // file size for noticeable speed improvement on small files.  Please direct
    // questions about this implementation to ames!jaw.
    var g_init_bits: Int = 0

    var ClearCode: Int = 0
    var EOFCode: Int = 0

    // output
    //
    // Output the given code.
    // Inputs:
    //      code:   A n_bits-bit integer.  If == -1, then EOF.  This assumes
    //              that n_bits =< wordsize - 1.
    // Outputs:
    //      Outputs code to the file.
    // Assumptions:
    //      Chars are 8 bits long.
    // Algorithm:
    //      Maintain a BITS character long buffer (so that 8 codes will
    // fit in it exactly).  Use the VAX insv instruction to insert each
    // code in turn.  When the buffer fills up empty it and start over.
    var cur_accum: Int = 0
    var cur_bits: Int = 0

    var masks: IntArray? = intArrayOf(
        0x0000,
        0x0001,
        0x0003,
        0x0007,
        0x000F,
        0x001F,
        0x003F,
        0x007F,
        0x00FF,
        0x01FF,
        0x03FF,
        0x07FF,
        0x0FFF,
        0x1FFF,
        0x3FFF,
        0x7FFF,
        0xFFFF
    )

    // Number of characters so far in this 'packet'
    var a_count: Int = 0

    // Define the storage for the packet accumulator
    var accum: ByteArray = ByteArray(256)

    //----------------------------------------------------------------------------

    // Add a character to the end of the current packet, and if it is 254
    // characters, flush the packet to disk.
    /**
     * @param c    字节
     * @param outs 输出流
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun char_out(c: Byte, outs: OutputStream) {
        accum[a_count++] = c
        if (a_count >= 254) flush_char(outs)
    }

    // Clear out the hash table
    // table clear for block compress
    /**
     * @param outs 输出流
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun cl_block(outs: OutputStream) {
        cl_hash(hsize)
        free_ent = ClearCode + 2
        clear_flg = true

        output(ClearCode, outs)
    }

    // reset code table
    /**
     * @param hsize int
     */
    fun cl_hash(hsize: Int) {
        for (i in 0..<hsize) htab[i] = -1
    }

    /**
     * @param init_bits int
     * @param outs      输出流
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun compress(init_bits: Int, outs: OutputStream) {
        var fcode: Int
        var i: Int /* = 0 */
        var c: Int
        var ent: Int
        var disp: Int
        val hsize_reg: Int
        var hshift: Int

        // Set up the globals:  g_init_bits - initial number of bits
        g_init_bits = init_bits

        // Set up the necessary values
        clear_flg = false
        n_bits = g_init_bits
        maxcode = MAXCODE(n_bits)

        ClearCode = 1 shl (init_bits - 1)
        EOFCode = ClearCode + 1
        free_ent = ClearCode + 2

        a_count = 0 // clear packet

        ent = nextPixel()

        hshift = 0
        fcode = hsize
        while (fcode < 65536) {
            ++hshift
            fcode *= 2
        }
        hshift = 8 - hshift // set hash code range bound

        hsize_reg = hsize
        cl_hash(hsize_reg) // clear hash table

        output(ClearCode, outs)

        outer_loop@ while ((nextPixel().also { c = it }) != EOF) {
            fcode = (c shl maxbits) + ent
            i = (c shl hshift) xor ent // xor hashing

            if (htab[i] == fcode) {
                ent = codetab[i]
                continue
            } else if (htab[i] >= 0)  // non-empty slot
            {
                disp = hsize_reg - i // secondary hash (after G. Knott)
                if (i == 0) disp = 1
                do {
                    if ((disp.let { i -= it; i }) < 0) i += hsize_reg

                    if (htab[i] == fcode) {
                        ent = codetab[i]
                        continue@outer_loop
                    }
                } while (htab[i] >= 0)
            }
            output(ent, outs)
            ent = c
            if (free_ent < maxmaxcode) {
                codetab[i] = free_ent++ // code -> hashtable
                htab[i] = fcode
            } else cl_block(outs)
        }
        // Put out the final code.
        output(ent, outs)
        output(EOFCode, outs)
    }

    //----------------------------------------------------------------------------
    /**
     * @param os 输出流
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun encode(os: OutputStream) {
        os.write(initCodeSize) // write "initial code size" byte

        remaining = imgW * imgH // reset navigation variables
        curPixel = 0

        compress(initCodeSize + 1, os) // compress and write the pixel data

        os.write(0) // write block terminator
    }

    // Flush the packet to disk, and reset the accumulator
    /**
     * @param outs 输出流
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun flush_char(outs: OutputStream) {
        if (a_count > 0) {
            outs.write(a_count)
            outs.write(accum, 0, a_count)
            a_count = 0
        }
    }

    /**
     * @param n_bits int
     * @return int
     */
    fun MAXCODE(n_bits: Int): Int {
        return (1 shl n_bits) - 1
    }

    //----------------------------------------------------------------------------
    // Return the next pixel from the image
    //----------------------------------------------------------------------------
    /**
     * @return int
     */
    private fun nextPixel(): Int {
        if (remaining == 0) return EOF

        --remaining

        val pix = pixAry[curPixel++]

        return pix.toInt() and 0xff
    }

    /**
     * @param code int
     * @param outs 输出流
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun output(code: Int, outs: OutputStream) {
        cur_accum = cur_accum and masks!![cur_bits]

        if (cur_bits > 0) cur_accum = cur_accum or (code shl cur_bits)
        else cur_accum = code

        cur_bits += n_bits

        while (cur_bits >= 8) {
            char_out((cur_accum and 0xff).toByte(), outs)
            cur_accum = cur_accum shr 8
            cur_bits -= 8
        }

        // If the next entry is going to be too big for the code size,
        // then increase it, if possible.
        if (free_ent > maxcode || clear_flg) {
            if (clear_flg) {
                maxcode = MAXCODE(g_init_bits.also { n_bits = it })
                clear_flg = false
            } else {
                ++n_bits
                if (n_bits == maxbits) maxcode = maxmaxcode
                else maxcode = MAXCODE(n_bits)
            }
        }

        if (code == EOFCode) {
            // At EOF, write the rest of the buffer.
            while (cur_bits > 0) {
                char_out((cur_accum and 0xff).toByte(), outs)
                cur_accum = cur_accum shr 8
                cur_bits -= 8
            }

            flush_char(outs)
        }
    }

    companion object {
        private val EOF = -1
        const val BITS: Int = 12

        const val HSIZE: Int = 5003 // 80% 占用率
    }
}
