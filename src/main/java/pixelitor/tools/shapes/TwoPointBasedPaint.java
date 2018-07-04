/*
 * Copyright 2018 Laszlo Balazs-Csiki and Contributors
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.tools.shapes;

import pixelitor.colors.FgBgColors;
import pixelitor.tools.gradient.paints.AngleGradientPaint;
import pixelitor.tools.gradient.paints.DiamondGradientPaint;
import pixelitor.tools.gradient.paints.SpiralGradientPaint;
import pixelitor.tools.util.ImDrag;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import static java.awt.AlphaComposite.DST_OUT;
import static java.awt.AlphaComposite.SRC_OVER;

/**
 * A Paint type based on two endpoints of a UserDrag.
 * Used as a fill type in the Shapes Tool.
 */
enum TwoPointBasedPaint {
    LINEAR_GRADIENT("Linear Gradient") {
        @Override
        protected Paint getPaint(ImDrag imDrag) {
            Color fgColor = FgBgColors.getFG();
            Color bgColor = FgBgColors.getBG();

            return new GradientPaint(
                    (float) imDrag.getStartXFromCenter(),
                    (float) imDrag.getStartYFromCenter(),
                    fgColor,
                    (float) imDrag.getEndX(),
                    (float) imDrag.getEndY(),
                    bgColor);
        }
    }, RADIAL_GRADIENT("Radial Gradient") {
        private final float[] FRACTIONS = {0.0f, 1.0f};
        private final AffineTransform gradientTransform = new AffineTransform();

        @Override
        protected Paint getPaint(ImDrag imDrag) {
            Color fgColor = FgBgColors.getFG();
            Color bgColor = FgBgColors.getBG();

            Point2D center = imDrag.getCenterPoint();
            float distance = (float) imDrag.getDistance();

            return new RadialGradientPaint(center, distance / 2, center, FRACTIONS, new Color[]{fgColor, bgColor},
                    MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, gradientTransform);
        }
    }, ANGLE_GRADIENT("Angle Gradient") {
        @Override
        protected Paint getPaint(ImDrag imDrag) {
            Color fgColor = FgBgColors.getFG();
            Color bgColor = FgBgColors.getBG();

            ImDrag centerUserDrag = imDrag.getCenterDrag();

            return new AngleGradientPaint(centerUserDrag, fgColor, bgColor, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        }
    }, SPIRAL_GRADIENT("Spiral Gradient") {
        @Override
        protected Paint getPaint(ImDrag imDrag) {
            Color fgColor = FgBgColors.getFG();
            Color bgColor = FgBgColors.getBG();

            ImDrag centerUserDrag = imDrag.getCenterDrag();

            return new SpiralGradientPaint(true, centerUserDrag, fgColor, bgColor, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        }
    }, DIAMOND_GRADIENT("Diamond Gradient") {
        @Override
        protected Paint getPaint(ImDrag imDrag) {
            Color fgColor = FgBgColors.getFG();
            Color bgColor = FgBgColors.getBG();

            ImDrag centerUserDrag = imDrag.getCenterDrag();

            return new DiamondGradientPaint(centerUserDrag, fgColor, bgColor, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        }
    }, FOREGROUND("Foreground") {
        @Override
        protected Paint getPaint(ImDrag imDrag) {
            return FgBgColors.getFG();
        }
    }, BACKGROUND("Background") {
        @Override
        protected Paint getPaint(ImDrag imDrag) {
            return FgBgColors.getBG();
        }
    }, TRANSPARENT("Transparent") {
        @Override
        protected Paint getPaint(ImDrag imDrag) {
            return Color.WHITE; // does not matter
        }

        @Override
        public void setupPaint(Graphics2D g, ImDrag imDrag) {
            g.setComposite(AlphaComposite.getInstance(DST_OUT));
        }

        @Override
        public void finish(Graphics2D g) {
            g.setComposite(AlphaComposite.getInstance(SRC_OVER));
        }
    };

    private final String guiName;

    TwoPointBasedPaint(String guiName) {
        this.guiName = guiName;
    }

    protected abstract Paint getPaint(ImDrag imDrag);

    /**
     * Called before the drawing/filling
     */
    public void setupPaint(Graphics2D g, ImDrag imDrag) {
        g.setPaint(getPaint(imDrag));
    }

    /**
     * Called after the drawing/filling
     */
    public void finish(Graphics2D g) {
        // by default do nothing
    }

    @Override
    public String toString() {
        return guiName;
    }
}