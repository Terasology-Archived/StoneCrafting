// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.stoneCrafting.milling.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;

public class MillProgressComponent implements Component {
    @Replicate
    public EntityRef processedItem;
    @Replicate
    public int processedStep;
}
