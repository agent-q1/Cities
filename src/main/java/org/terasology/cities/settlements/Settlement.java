/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.settlements;

import java.util.ArrayList;
import java.util.Collection;

import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.roads.Road;
import org.terasology.cities.sites.Site;

/**
 * Provides information on a settlement.
  */
public class Settlement {

    private final Site site;
    private String name;

    private final Collection<Road> roads = new ArrayList<>();
    private final Collection<Parcel> parcels = new ArrayList<>();

    /**
     * @param site the site of the settlement
     * @param name the name of the settlement
     */
    public Settlement(Site site, String name) {
        this.site = site;
        this.name = name;
    }

    /**
     * @return the site of the settlement
     */
    public Site getSite() {
        return site;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + site.getPos() + ")";
    }

    /**
     * @param road the road to add
     */
    public void addRoad(Road road) {
        roads.add(road);
    }

    /**
     * @param parcel the parcel to add
     */
    public void addParcel(Parcel parcel) {
        parcels.add(parcel);
    }
}