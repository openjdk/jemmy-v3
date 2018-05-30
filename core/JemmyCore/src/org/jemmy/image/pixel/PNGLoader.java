/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.jemmy.image.pixel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.jemmy.JemmyException;
import org.jemmy.image.pixel.Raster.Component;

/**
 * Allows to load PNG graphical file.
 * @author Alexandre Iline
 */
public abstract class PNGLoader {

    InputStream in;

    /**
     * Constructs a PNGDecoder object.
     * @param in input stream to read PNG image from.
     */
    public PNGLoader(InputStream in) {
        this.in = in;
    }

    byte read() throws IOException {
        byte b = (byte)in.read();
        return(b);
    }

    int readInt() throws IOException {
        byte b[] = read(4);
        return(((b[0]&0xff)<<24) +
               ((b[1]&0xff)<<16) +
               ((b[2]&0xff)<<8) +
               ((b[3]&0xff)));
    }

    byte[] read(int count) throws IOException {
        byte[] result = new byte[count];
        for(int i = 0; i < count; i++) {
            result[i] = read();
        }
        return(result);
    }

    void checkEquality(byte[] b1, byte[] b2) {
        if(!Arrays.equals(b1, b2)) {
            throw(new JemmyException("Format error"));
        }
    }

    /**
     * Decodes image from an input stream passed into constructor.
     * @return a BufferedImage object
     * @throws IOException todo document
     */
    public Raster decode() throws IOException {
        return decode(true);
    }

    protected abstract WriteableRaster createRaster(int width, int height);

    /**
     * Decodes image from an input stream passed into constructor.
     * @return a BufferedImage object
     * @param closeStream requests method to close the stream after the image is read
     * @throws IOException todo document
     */
    public Raster decode(boolean closeStream) throws IOException {

        byte[] id = read(12);
        checkEquality(id, new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13});

        byte[] ihdr = read(4);
        checkEquality(ihdr, "IHDR".getBytes());

        int width = readInt();
        int height = readInt();

        WriteableRaster result = createRaster(width, height);

        byte[] head = read(5);
        int mode;
        if(Arrays.equals(head, new byte[]{1, 0, 0, 0, 0})) {
            mode = PNGSaver.BW_MODE;
        } else if(Arrays.equals(head, new byte[]{8, 0, 0, 0, 0})) {
            mode = PNGSaver.GREYSCALE_MODE;
        } else if(Arrays.equals(head, new byte[]{8, 2, 0, 0, 0})) {
            mode = PNGSaver.COLOR_MODE;
        } else {
            throw(new JemmyException("Format error"));
        }

        readInt();//!!crc

        int size = readInt();

        byte[] idat = read(4);
        checkEquality(idat, "IDAT".getBytes());

        byte[] data = read(size);


        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, size);

        int[] colors = new int[3];
        int[] black = new int[] {0, 0, 0};
        int[] white = new int[] {1, 1, 1};

        try {
            switch (mode) {
            case PNGSaver.BW_MODE:
                {
                    int bytes = (int)(width / 8);
                    if((width % 8) != 0) {
                        bytes++;
                    }
                    byte colorset;
                    byte[] row = new byte[bytes];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < bytes; x++) {
                            colorset = row[x];
                            for (int sh = 0; sh < 8; sh++) {
                                if(x * 8 + sh >= width) {
                                    break;
                                }
                                if((colorset & 0x80) == 0x80) {
                                    setColors(result, x * 8 + sh, y, white);
                                } else {
                                    setColors(result, x * 8 + sh, y, black);
                                }
                                colorset <<= 1;
                            }
                        }
                    }
                }
                break;
            case PNGSaver.GREYSCALE_MODE:
                {
                    byte[] row = new byte[width];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < width; x++) {
                            colors[0] = row[x];
                            colors[1] = colors[0];
                            colors[2] = colors[0];
                            setColors(result, x, y, colors);
                        }
                    }
                }
                break;
            case PNGSaver.COLOR_MODE:
                {
                    byte[] row = new byte[width * 3];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < width; x++) {
                            colors[0] = (row[x * 3 + 0]&0xff);
                            colors[1] = (row[x * 3 + 1]&0xff);
                            colors[2] = (row[x * 3 + 2]&0xff);
                            setColors(result, x, y, colors);
                        }
                    }
                }
            }
        } catch(DataFormatException e) {
            throw(new JemmyException("ZIP error", e));
        }

        readInt();//!!crc
        readInt();//0

        byte[] iend = read(4);
        checkEquality(iend, "IEND".getBytes());

        readInt();//!!crc
        if (closeStream) {
            in.close();
        }

        return(result);
    }

    private void setColors(WriteableRaster raster, int x, int y, int[] colors) {
        Component[] supported = raster.getSupported();
        double[] imageColors = new double[supported.length];
        for (int i = 0; i < supported.length; i++) {
            if(supported[i] == Component.ALPHA) {
                imageColors[i] = 1;
            } else {
                imageColors[i] = (double)colors[
                        PixelImageComparator.arrayIndexOf(PNGSaver.RGB, supported[i])]/0xFF;
            }
        }
        raster.setColors(x, y, imageColors);
    }

}
