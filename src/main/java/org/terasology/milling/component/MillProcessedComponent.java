// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.milling.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

public class MillProcessedComponent implements Component {
    @Replicate
    public long millLength;
    @Replicate
    public String blockResult;
    @Replicate
    public String itemResult;
}
