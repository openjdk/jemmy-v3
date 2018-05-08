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

/**
 * Comparator, that implements peak signal to noise ratio, popular and reliable method of measuring
 * image compression quality or image distortion.
 * PSNR is used to measure the quality of reconstruction of lossy compression codecs. The signal is
 * the reference (golden) image, and the noise is actual image. Higher PSNR indicates lesser
 * difference between images for the most cases.
 * Comparator is intended to compare images with large amount of very minor differences and one
 * has to be extremely careful with the range of validity of this metric; it is only conclusively
 * valid when it is used to compare results from the same content.
 *
 * @author andrey.rusakov@oracle.com
 */
public class PeakSignalNoiseRatioComparator implements RasterComparator {

    public static final String OUTPUT = PeakSignalNoiseRatioComparator.class.getName() + ".OUTPUT";

    private static final double MAX_CHANNEL = 1.0;
    private final double minRatio;

    private static double getMeanSquareError(Raster image, Raster original) {
        double result = 0;
        double[] colors1 = new double[image.getSupported().length];
        double[] colors2 = new double[original.getSupported().length];

        int w = Math.min(image.getSize().width, original.getSize().width);
        int h = Math.min(image.getSize().height, original.getSize().height);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                image.getColors(i, j, colors1);
                original.getColors(i, j, colors2);
                double distance = AverageDistanceComparator.distance(image.getSupported(), colors1,
                        original.getSupported(), colors2);
                result += distance * distance / (AverageDistanceComparator.DISTANCE_COMPONENTS.length * w * h);
            }
        }
        return result;
    }

    private static double getPeakSignalNoiseRatio(Raster image, Raster original) {
        double mse = getMeanSquareError(image, original);
        return 20 * Math.log10(MAX_CHANNEL) - 10 * Math.log10(mse);
    }

    public PeakSignalNoiseRatioComparator(double minRatio) {
        this.minRatio = minRatio;
    }

    @Override
    public boolean compare(Raster image1, Raster image2) {
        return getPeakSignalNoiseRatio(image1, image2) > minRatio;
    }

    @Override
    public String getID() {
        return getClass().getName() + ":" + minRatio;
    }

}
