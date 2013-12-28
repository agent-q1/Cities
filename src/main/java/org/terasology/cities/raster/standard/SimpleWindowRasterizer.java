/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.cities.raster.standard;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.SimpleWindow;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;

/**
 * Converts {@link SimpleWindow} into blocks (or air actually)
 * @author Martin Steiger
 */
public class SimpleWindowRasterizer implements Rasterizer<SimpleWindow> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleWindow wnd) {
        brush.fillRect(wnd.getRect(), wnd.getBaseHeight(), wnd.getTopHeight(), BlockTypes.AIR);
    }

}