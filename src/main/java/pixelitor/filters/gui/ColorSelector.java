/*
 * Copyright 2016 Laszlo Balazs-Csiki
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

package pixelitor.filters.gui;

import com.bric.swing.ColorPicker;
import com.bric.swing.ColorSwatch;
import pixelitor.colors.ColorHistory;
import pixelitor.colors.ColorUtils;
import pixelitor.colors.palette.ColorSwatchClickHandler;
import pixelitor.colors.palette.VariationsPanel;
import pixelitor.gui.PixelitorWindow;
import pixelitor.gui.utils.Dialogs;
import pixelitor.menus.MenuAction;

import javax.swing.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The GUI for a ColorParam
 */
public class ColorSelector extends JPanel implements ParamGUI {
    private final ColorParam model;
//    private JButton button;

    private final ColorSwatch colorSwatch;

    private static final int BUTTON_SIZE = 30;

    public ColorSelector(ColorParam model) {
        this.model = model;
        setLayout(new FlowLayout(FlowLayout.LEFT));

//        button = new JButton();
//        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
//        button.setBackground(model.getColor());
//        button.addActionListener(e -> showColorDialog());
//        add(button);

        Color color = model.getColor();
        colorSwatch = new ColorSwatch(color, BUTTON_SIZE);
        add(colorSwatch);

        colorSwatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    showColorDialog();
                }
            }
        });

        JPopupMenu popup = new JPopupMenu();

        ColorSwatchClickHandler clickHandler = (newColor, e) -> updateColor(newColor);

        popup.add(new MenuAction("Color Variations...") {
            @Override
            public void onClick() {
                Window window = SwingUtilities.windowForComponent(ColorSelector.this);
                VariationsPanel.showFilterVariationsDialog(window, model.getColor(), clickHandler);
            }
        });

        popup.add(new MenuAction("Filter Color History...") {
            @Override
            public void onClick() {
                Window window = SwingUtilities.windowForComponent(ColorSelector.this);
                ColorHistory.FILTER.showDialog(window, clickHandler);
            }
        });

        popup.addSeparator();

        popup.add(new MenuAction("Copy Color") {
            @Override
            public void onClick() {
                ColorUtils.copyColorToClipboard(model.getColor());
            }
        });

        popup.add(new MenuAction("Paste Color") {
            @Override
            public void onClick() {
                Color color = ColorUtils.getColorFromClipboard();
                if (color == null) {
                    Window window = SwingUtilities.windowForComponent(ColorSelector.this);
                    Dialogs.showNotAColorOnClipboardDialog(window);
                } else {
                    updateColor(color);
                }
            }
        });

        colorSwatch.setComponentPopupMenu(popup);
    }

    private void showColorDialog() {
//        Color color = JColorChooser.showDialog(this, "Select Color", model.getColor());

        Color color = ColorPicker.showDialog(PixelitorWindow.getInstance(), "Select " + model.getName(), model.getColor(), model.allowOpacity());

        if (color != null) { // ok was pressed
            updateColor(color);
        }
    }

    private void updateColor(Color color) {
        //            button.setBackground(color);
//            button.paintImmediately(0, 0, BUTTON_SIZE, BUTTON_SIZE);

        colorSwatch.setForeground(color);
        colorSwatch.paintImmediately(0, 0, BUTTON_SIZE, BUTTON_SIZE);

        model.setColor(color, true);
    }

    @Override
    public void updateGUI() {
//        button.setBackground(model.getColor());

        colorSwatch.setForeground(model.getColor());
    }

    @Override
    public void setToolTip(String tip) {
        colorSwatch.setToolTipText(tip);

//        button.setToolTipText(tip);
    }
}
