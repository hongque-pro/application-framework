package com.labijie.application.captcha.utils

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.*

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
/**
 * Gif生成工具
 * Class AnimatedGifEncoder - Encodes a GIF file consisting of one or
 * more frames.
 * <pre>
 * Example:
 * AnimatedGifEncoder e = new AnimatedGifEncoder();
 * e.start(outputFileName);
 * e.setDelay(1000);   // 1 frame per sec
 * e.addFrame(image1);
 * e.addFrame(image2);
 * e.finish();
</pre> *
 * No copyright asserted on the source code of this class.  May be used
 * for any purpose, however, refer to the Unisys LZW patent for restrictions
 * on use of the associated Encoder class.  Please forward any corrections
 * to questions at fmsware.com.
 */
open class GifEncoder {
    protected var width: Int = 0 // image size
    protected var height: Int = 0
    @JvmField
    protected var transparent: Color? = null // transparent color if given
    protected var transIndex: Int = 0 // transparent index in color table
    @JvmField
    protected var repeat: Int = -1 // no repeat
    @JvmField
    protected var delay: Int = 0 // frame delay (hundredths)
    protected var started: Boolean = false // ready to output frames
    protected var out: OutputStream? = null
    protected var image: BufferedImage? = null // current frame
    protected var pixels: ByteArray? = null // BGR byte array from frame
    protected var indexedPixels: ByteArray? = null // converted frame indexed to palette
    protected var colorDepth: Int = 0 // number of bit planes
    protected var colorTab: ByteArray? = null // RGB palette
    protected var usedEntry: BooleanArray = BooleanArray(256) // active palette entries
    protected var palSize: Int = 7 // color table size (bits-1)
    @JvmField
    protected var dispose: Int = -1 // disposal code (-1 = use default)
    protected var closeStream: Boolean = false // close stream when finished
    protected var firstFrame: Boolean = true
    protected var sizeSet: Boolean = false // if false, get size from first frame
    protected var sample: Int = 10 // default sample interval for quantizer

    /**
     * Sets the delay time between each frame, or changes it
     * for subsequent frames (applies to last frame added).
     *
     * @param ms int delay time in milliseconds
     */
    fun setDelay(ms: Int) {
        delay = Math.round(ms / 10.0f)
    }

    /**
     * Sets the GIF frame disposal code for the last added frame
     * and any subsequent frames.  Default is 0 if no transparent
     * color has been set, otherwise 2.
     *
     * @param code int disposal code.
     */
    fun setDispose(code: Int) {
        if (code >= 0) {
            dispose = code
        }
    }

    /**
     * Sets the number of times the set of GIF frames
     * should be played.  Default is 1; 0 means play
     * indefinitely.  Must be invoked before the first
     * image is added.
     *
     * @param iter int number of iterations.
     */
    fun setRepeat(iter: Int) {
        if (iter >= 0) {
            repeat = iter
        }
    }

    /**
     * Sets the transparent color for the last added frame
     * and any subsequent frames.
     * Since all colors are subject to modification
     * in the quantization process, the color in the final
     * palette for each frame closest to the given color
     * becomes the transparent color for that frame.
     * May be set to null to indicate no transparent color.
     *
     * @param c Color to be treated as transparent on display.
     */
    fun setTransparent(c: Color?) {
        transparent = c
    }

    /**
     * Adds next GIF frame.  The frame is not written immediately, but is
     * actually deferred until the next frame is received so that timing
     * data can be inserted.  Invoking `finish()` flushes all
     * frames.  If `setSize` was not invoked, the size of the
     * first image is used for all subsequent frames.
     *
     * @param im BufferedImage containing frame to write.
     * @return true if successful.
     */
    fun addFrame(im: BufferedImage?): Boolean {
        if ((im == null) || !started) {
            return false
        }
        var ok = true
        try {
            if (!sizeSet) {
                // use first frame's size
                setSize(im.getWidth(), im.getHeight())
            }
            image = im
            this.imagePixels // convert to correct format if necessary
            analyzePixels() // build color table & map pixels
            if (firstFrame) {
                writeLSD() // logical screen descriptior
                writePalette() // global color table
                if (repeat >= 0) {
                    // use NS app extension to indicate reps
                    writeNetscapeExt()
                }
            }
            writeGraphicCtrlExt() // write graphic control extension
            writeImageDesc() // image descriptor
            if (!firstFrame) {
                writePalette() // local color table
            }
            writePixels() // encode and write pixel data
            firstFrame = false
        } catch (e: IOException) {
            ok = false
        }

        return ok
    }

    //added by alvaro
    fun outFlush(): Boolean {
        var ok = true
        try {
            out!!.flush()
            return ok
        } catch (e: IOException) {
            ok = false
        }

        return ok
    }

    val frameByteArray: ByteArray?
        get() = (out as ByteArrayOutputStream).toByteArray()

    /**
     * Flushes any pending data and closes output file.
     * If writing to an OutputStream, the stream is not
     * closed.
     *
     * @return boolean
     */
    fun finish(): Boolean {
        if (!started) return false
        var ok = true
        started = false
        try {
            out!!.write(0x3b) // gif trailer
            out!!.flush()
            if (closeStream) {
                out!!.close()
            }
        } catch (e: IOException) {
            ok = false
        }

        return ok
    }

    fun reset() {
        // reset for subsequent use
        transIndex = 0
        out = null
        image = null
        pixels = null
        indexedPixels = null
        colorTab = null
        closeStream = false
        firstFrame = true
    }

    /**
     * Sets frame rate in frames per second.  Equivalent to
     * `setDelay(1000/fps)`.
     *
     * @param fps float frame rate (frames per second)
     */
    fun setFrameRate(fps: Float) {
        if (fps != 0f) {
            delay = Math.round(100f / fps)
        }
    }

    /**
     * Sets quality of color quantization (conversion of images
     * to the maximum 256 colors allowed by the GIF specification).
     * Lower values (minimum = 1) produce better colors, but slow
     * processing significantly.  10 is the default, and produces
     * good color mapping at reasonable speeds.  Values greater
     * than 20 do not yield significant improvements in speed.
     *
     * @param quality int greater than 0.
     */
    fun setQuality(quality: Int) {
        var quality = quality
        if (quality < 1) quality = 1
        sample = quality
    }

    /**
     * Sets the GIF frame size.  The default size is the
     * size of the first frame added if this method is
     * not invoked.
     *
     * @param w int frame width.
     * @param h int frame width.
     */
    fun setSize(w: Int, h: Int) {
        if (started && !firstFrame) return
        width = w
        height = h
        if (width < 1) width = 320
        if (height < 1) height = 240
        sizeSet = true
    }

    /**
     * Initiates GIF file creation on the given stream.  The stream
     * is not closed automatically.
     *
     * @param os OutputStream on which GIF images are written.
     * @return false if initial write failed.
     */
    fun start(os: OutputStream?): Boolean {
        if (os == null) return false
        var ok = true
        closeStream = false
        out = os
        try {
            writeString("GIF89a") // header
        } catch (e: IOException) {
            ok = false
        }
        return ok.also { started = it }
    }

    /**
     * Initiates writing of a GIF file with the specified name.
     *
     * @param file String containing output file name.
     * @return false if open or initial write failed.
     */
    fun start(file: String): Boolean {
        var ok = true
        try {
            out = BufferedOutputStream(FileOutputStream(file))
            ok = start(out)
            closeStream = true
        } catch (e: IOException) {
            ok = false
        }
        return ok.also { started = it }
    }

    /**
     * Analyzes image colors and creates color map.
     */
    protected fun analyzePixels() {
        pixels?.let { pixelData ->
            val len = pixelData.size
            val nPix = len / 3
            indexedPixels = ByteArray(nPix)
            val nq: Quant = Quant(pixelData, len, sample)
            // initialize quantizer
            colorTab = nq.process() // create reduced palette
            // convert map from BGR to RGB
            run {
                var i = 0
                while (i < colorTab!!.size) {
                    val temp = colorTab!![i]
                    colorTab!![i] = colorTab!![i + 2]
                    colorTab!![i + 2] = temp
                    usedEntry[i / 3] = false
                    i += 3
                }
            }
            // map image pixels to new palette
            var k = 0
            for (i in 0..<nPix) {
                val index: Int =
                    nq.map(
                        pixelData[k++].toInt() and 0xff,
                        pixelData[k++].toInt() and 0xff,
                        pixelData[k++].toInt() and 0xff
                    )
                usedEntry[index] = true
                indexedPixels!![i] = index.toByte()
            }
        }
        pixels = null
        colorDepth = 8
        palSize = 7
        // get closest match to transparent color if specified
        transparent?.let {
            transIndex = findClosest(it)
        }
    }

    /**
     * Returns index of palette color closest to c
     *
     * @param c color
     * @return int
     */
    protected fun findClosest(c: Color): Int {
        if (colorTab == null) return -1
        val r = c.getRed()
        val g = c.getGreen()
        val b = c.getBlue()
        var minpos = 0
        var dmin = 256 * 256 * 256
        val len = colorTab!!.size
        var i = 0
        while (i < len) {
            val dr = r - (colorTab!![i++].toInt() and 0xff)
            val dg = g - (colorTab!![i++].toInt() and 0xff)
            val db = b - (colorTab!![i].toInt() and 0xff)
            val d = dr * dr + dg * dg + db * db
            val index = i / 3
            if (usedEntry[index] && (d < dmin)) {
                dmin = d
                minpos = index
            }
            i++
        }
        return minpos
    }

    protected val imagePixels: Unit
        /**
         * Extracts image pixels into byte array "pixels"
         */
        get() {
            val w = image!!.width
            val h = image!!.height
            val type = image!!.type
            if ((w != width)
                || (h != height)
                || (type != BufferedImage.TYPE_3BYTE_BGR)
            ) {
                // create new image with right size/format
                val temp =
                    BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
                val g = temp.createGraphics()
                g.drawImage(image, 0, 0, null)
                image = temp
            }
            pixels = (image!!.getRaster().getDataBuffer() as DataBufferByte).getData()
        }

    /**
     * Writes Graphic Control Extension
     *
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writeGraphicCtrlExt() {
        out!!.write(0x21) // extension introducer
        out!!.write(0xf9) // GCE label
        out!!.write(4) // data block size
        val transp: Int
        var disp: Int
        if (transparent == null) {
            transp = 0
            disp = 0 // dispose = no action
        } else {
            transp = 1
            disp = 2 // force clear if using transparent color
        }
        if (dispose >= 0) {
            disp = dispose and 7 // user override
        }
        disp = disp shl 2

        // packed fields
        out!!.write(
            0 or  // 1:3 reserved
                    disp or  // 4:6 disposal
                    0 or  // 7   user input - 0 = none
                    transp
        ) // 8   transparency flag

        writeShort(delay) // delay x 1/100 sec
        out!!.write(transIndex) // transparent color index
        out!!.write(0) // block terminator
    }

    /**
     * Writes Image Descriptor
     *
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writeImageDesc() {
        out!!.write(0x2c) // image separator
        writeShort(0) // image position x,y = 0,0
        writeShort(0)
        writeShort(width) // image size
        writeShort(height)
        // packed fields
        if (firstFrame) {
            // no LCT  - GCT is used for first (or only) frame
            out!!.write(0)
        } else {
            // specify normal LCT
            out!!.write(
                0x80 or  // 1 local color table  1=yes
                        0 or  // 2 interlace - 0=no
                        0 or  // 3 sorted - 0=no
                        0 or  // 4-5 reserved
                        palSize
            ) // 6-8 size of color table
        }
    }

    /**
     * Writes Logical Screen Descriptor
     *
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writeLSD() {
        // logical screen size
        writeShort(width)
        writeShort(height)
        // packed fields
        out!!.write(
            (0x80 or  // 1   : global color table flag = 1 (gct used)
                    0x70 or  // 2-4 : color resolution = 7
                    0x00 or  // 5   : gct sort flag = 0
                    palSize)
        ) // 6-8 : gct size

        out!!.write(0) // background color index
        out!!.write(0) // pixel aspect ratio - assume 1:1
    }

    /**
     * Writes Netscape application extension to define
     * repeat count.
     *
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writeNetscapeExt() {
        out!!.write(0x21) // extension introducer
        out!!.write(0xff) // app extension label
        out!!.write(11) // block size
        writeString("NETSCAPE" + "2.0") // app id + auth code
        out!!.write(3) // sub-block size
        out!!.write(1) // loop sub-block id
        writeShort(repeat) // loop count (extra iterations, 0=repeat forever)
        out!!.write(0) // block terminator
    }

    /**
     * Writes color table
     *
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writePalette() {
        out!!.write(colorTab, 0, colorTab!!.size)
        val n = (3 * 256) - colorTab!!.size
        for (i in 0..<n) {
            out!!.write(0)
        }
    }

    /**
     * Encodes and writes pixel data
     *
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writePixels() {
        val encoder = Encoder(width, height, indexedPixels!!, colorDepth)
        encoder.encode(out!!)
    }

    /**
     * Write 16-bit value to output stream, LSB first
     *
     * @param value int
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writeShort(value: Int) {
        out!!.write(value and 0xff)
        out!!.write((value shr 8) and 0xff)
    }

    /**
     * Writes string to output stream
     *
     * @param s string
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    protected fun writeString(s: String) {
        for (i in 0..<s.length) {
            out!!.write(s.get(i).code.toByte().toInt())
        }
    }
}