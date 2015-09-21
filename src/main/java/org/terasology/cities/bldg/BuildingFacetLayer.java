/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.bldg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import org.terasology.cities.AwtConverter;
import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.door.SimpleDoorRasterizer;
import org.terasology.cities.door.WingDoorRasterizer;
import org.terasology.cities.raster.ImageRasterTarget;
import org.terasology.cities.raster.standard.RectPartRasterizer;
import org.terasology.cities.raster.standard.RoundPartRasterizer;
import org.terasology.cities.roof.ConicRoofRasterizer;
import org.terasology.cities.roof.DomeRoofRasterizer;
import org.terasology.cities.roof.FlatRoofRasterizer;
import org.terasology.cities.roof.HipRoofRasterizer;
import org.terasology.cities.roof.PentRoofRasterizer;
import org.terasology.cities.roof.SaddleRoofRasterizer;
import org.terasology.cities.window.RectWindowRasterizer;
import org.terasology.cities.window.SimpleWindowRasterizer;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.TeraMath;
import org.terasology.rendering.nui.properties.Checkbox;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.FacetLayerConfig;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * Draws buildings area in a given image
 */
@Renders(value = BuildingFacet.class, order = ZOrder.BIOME + 3)
public class BuildingFacetLayer extends AbstractFacetLayer {

    private final BufferedImage bufferImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);

    private final Map<BlockTypes, Color> blockColors = ImmutableMap.<BlockTypes, Color>builder()
            .put(BlockTypes.AIR, new Color(0, 0, 0, 0))
            .put(BlockTypes.ROAD_SURFACE, new Color(160, 40, 40))
            .put(BlockTypes.LOT_EMPTY, new Color(224, 224, 64))
            .put(BlockTypes.BUILDING_WALL, new Color(158, 158, 158))
            .put(BlockTypes.BUILDING_FLOOR, new Color(100, 100, 100))
            .put(BlockTypes.BUILDING_FOUNDATION, new Color(90, 60, 60))
            .put(BlockTypes.ROOF_FLAT, new Color(255, 60, 60))
            .put(BlockTypes.ROOF_HIP, new Color(255, 60, 60))
            .put(BlockTypes.ROOF_SADDLE, new Color(224, 120, 100))
            .put(BlockTypes.ROOF_DOME, new Color(160, 190, 190))
            .put(BlockTypes.ROOF_GABLE, new Color(180, 120, 100))
            .put(BlockTypes.TOWER_WALL, new Color(200, 100, 200))
            .put(BlockTypes.WING_DOOR, new Color(110, 110, 10))
            .put(BlockTypes.SIMPLE_DOOR, new Color(210, 110, 210))
            .put(BlockTypes.WINDOW_GLASS, new Color(110, 210, 110))
            .build();

    private enum RasterizerType {
        BASE,
        WNDW,
        DOOR,
        ROOF
    }

    private final ListMultimap<RasterizerType, AbstractBuildingRasterizer<?>> rasterizers = Multimaps.newListMultimap(
            new EnumMap<>(RasterizerType.class), ArrayList::new);

    private Config config = new Config();

    public BuildingFacetLayer() {
        setVisible(true);

        BlockTheme theme = null;
        rasterizers.put(RasterizerType.BASE, new RectPartRasterizer(theme));
        rasterizers.put(RasterizerType.BASE, new RoundPartRasterizer(theme));
//        rasterizers.put(RasterizerType.ROOF, new FlatRoofRasterizer(theme));
//        rasterizers.put(RasterizerType.ROOF, new SaddleRoofRasterizer(theme));
//        rasterizers.put(RasterizerType.ROOF, new PentRoofRasterizer(theme));
//        rasterizers.put(RasterizerType.ROOF, new HipRoofRasterizer(theme));
//        rasterizers.put(RasterizerType.ROOF, new ConicRoofRasterizer(theme));
//        rasterizers.put(RasterizerType.ROOF, new DomeRoofRasterizer(theme));
//        rasterizers.put(RasterizerType.DOOR, new SimpleDoorRasterizer(theme));
//        rasterizers.put(RasterizerType.DOOR, new WingDoorRasterizer(theme));
//        rasterizers.put(RasterizerType.WNDW, new SimpleWindowRasterizer(theme));
//        rasterizers.put(RasterizerType.WNDW, new RectWindowRasterizer(theme));
    }

    /**
     * This can be called only through reflection since Config is private
     * @param config the layer configuration info
     */
    public BuildingFacetLayer(Config config) {
        this();
        this.config = config;
    }

    @Override
    public void render(BufferedImage img, Region region) {

        if (config.showFloorPlan) {
            renderFloorPlan(img, region);
        }

        int wx = region.getRegion().minX();
        int wz = region.getRegion().minZ();
        ImageRasterTarget brush = new ImageRasterTarget(wx, wz, img, blockColors::get);
        render(brush, region);
    }

    private void renderFloorPlan(BufferedImage img, Region region) {
        BuildingFacet buildingFacet = region.getFacet(BuildingFacet.class);
        int wx = region.getRegion().minX();
        int wz = region.getRegion().minZ();

        Graphics2D g = img.createGraphics();
        g.translate(-wx, -wz);

        Color fillColor = new Color(32, 32, 192, 64);
        Color frameColor = new Color(32, 32, 192, 128);
        for (Building bldg : buildingFacet.getBuildings()) {
            for (BuildingPart part : bldg.getParts()) {
                Shape shape = AwtConverter.toAwt(part.getShape());
                g.setColor(fillColor);
                g.fill(shape);
                g.setColor(frameColor);
                g.draw(shape);
            }
        }

        g.dispose();
    }

    private void render(ImageRasterTarget brush, Region chunkRegion) {
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        HeightMap hm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(heightFacet.getWorld(x, z));
            }
        };

        BuildingFacet buildingFacet = chunkRegion.getFacet(BuildingFacet.class);
        for (Building bldg : buildingFacet.getBuildings()) {
            if (config.showBase) {
                for (AbstractBuildingRasterizer<?> rasterizer : rasterizers.get(RasterizerType.BASE)) {
                    rasterizer.raster(brush, bldg, hm);
                }
            }
            if (config.showRoofs) {
                for (AbstractBuildingRasterizer<?> rasterizer : rasterizers.get(RasterizerType.ROOF)) {
                    rasterizer.raster(brush, bldg, hm);
                }
            }
            if (config.showDoors) {
                for (AbstractBuildingRasterizer<?> rasterizer : rasterizers.get(RasterizerType.DOOR)) {
                    rasterizer.raster(brush, bldg, hm);
                }
            }
            if (config.showWindows) {
                for (AbstractBuildingRasterizer<?> rasterizer : rasterizers.get(RasterizerType.WNDW)) {
                    rasterizer.raster(brush, bldg, hm);
                }
            }
        }
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        int dx = bufferImage.getWidth() / 2;
        int dy = bufferImage.getHeight() / 2;
        ImageRasterTarget brush = new ImageRasterTarget(wx - dx, wy - dy, bufferImage, blockColors::get);
        render(brush, region);

        int height = brush.getHeight(wx, wy);
        BlockTypes type = brush.getBlockType(wx, wy);
        return type == null ? null : type.toString() + "(" + height + ")";
    }

    @Override
    public FacetLayerConfig getConfig() {
        return config;
    }

    /**
     * Persistent data
     */
    private static class Config implements FacetLayerConfig {
        @Checkbox private boolean showFloorPlan;
        @Checkbox private boolean showBase;
        @Checkbox private boolean showRoofs = true;
        @Checkbox private boolean showWindows;
        @Checkbox private boolean showDoors;
    }
}
