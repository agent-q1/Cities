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

package org.terasology.cities.testing.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.terasology.cities.model.City;
import org.terasology.cities.model.GateWallSegment;
import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.SimpleTower;
import org.terasology.cities.model.SolidWallSegment;
import org.terasology.cities.model.Tower;
import org.terasology.cities.model.TownWall;
import org.terasology.cities.model.WallSegment;
import org.terasology.math.Vector2i;

/**
 * Converts a city to pixels
 * @author Martin Steiger
 */
public class CityRasterizerSimple {

    /**
     * @param g the graphics object
     * @param ci the city info
     */
    public void rasterCity(Graphics2D g, City ci) {

        g.setStroke(new BasicStroke());
        g.setColor(Color.BLACK);
        
        int cx = (int) ((ci.getPos().x) * Sector.SIZE);
        int cz = (int) ((ci.getPos().y) * Sector.SIZE);

        int ccSize = (int) ci.getDiameter();

        // draw city bounding circle with a dashed blue line
        float[] dash = {8, 8};
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash, 0f));
        g.drawOval(cx - ccSize / 2, cz - ccSize / 2, ccSize, ccSize);
        
        if (ci instanceof MedievalTown) {
            MedievalTown town = (MedievalTown) ci;
            
            if (town.getTownWall().isPresent()) {
                TownWall townWall = town.getTownWall().get();

                rasterTownWall(g, townWall);
            }
        }
    }

    private void rasterTownWall(Graphics2D g, TownWall townWall) {
        for (WallSegment ws : townWall.getWalls()) {
            Vector2i from = ws.getStart();
            Vector2i to = ws.getEnd();
            Color color = Color.BLACK;
            int thickness = 1;
            
            if (ws instanceof SolidWallSegment) {
                SolidWallSegment sws = (SolidWallSegment) ws;
                thickness = sws.getWallThickness();
                color = Color.GRAY;
            }

            if (ws instanceof GateWallSegment) {
                GateWallSegment sws = (GateWallSegment) ws;
                thickness = sws.getWallThickness();
                color = new Color(224, 64, 64);
            }

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(thickness));
            g.drawLine(from.x, from.y, to.x, to.y);

            g.setColor(color);
            g.setStroke(new BasicStroke(thickness - 2)); // create a border
            g.drawLine(from.x, from.y, to.x, to.y);
        }
        
        g.setStroke(new BasicStroke());
        g.setColor(Color.BLACK);
        
        for (Tower tower : townWall.getTowers()) {
            if (tower instanceof SimpleTower) {
                SimpleTower st = (SimpleTower) tower;
                g.setColor(Color.GRAY);
                g.fill(st.getLayout());
                
                g.setColor(Color.BLACK);
                g.draw(st.getLayout());
            }
        }
    }

}
