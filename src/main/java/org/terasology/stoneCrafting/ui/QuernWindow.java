/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.stoneCrafting.ui;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.heat.component.HeatProcessedComponent;
import org.terasology.heat.component.HeatProducerComponent;
import org.terasology.heat.ui.ThermometerWidget;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.processing.ui.VerticalTextureProgressWidget;
import org.terasology.processing.ui.WorkstationScreenUtils;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryGrid;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILoadBar;
import org.terasology.workstation.component.WorkstationInventoryComponent;
import org.terasology.workstation.component.WorkstationProcessingComponent;
import org.terasology.workstation.event.WorkstationProcessRequest;
import org.terasology.workstation.process.WorkstationInventoryUtils;
import org.terasology.workstation.system.WorkstationRegistry;

import java.util.List;

public class QuernWindow extends BaseInteractionScreen {
    private InventoryGrid input;
    private InventoryGrid output;
    private UIButton millButton;
    private UILoadBar millingProgress;

    @Override
    public void initialise() {
        input = find("input", InventoryGrid.class);
        output = find("output", InventoryGrid.class);
        millButton = find("millButton", UIButton.class);
        millingProgress = find("millingProgress", UILoadBar.class);

        InventoryGrid player = find("player", InventoryGrid.class);
        player.setTargetEntity(CoreRegistry.get(LocalPlayer.class).getCharacterEntity());
        player.setCellOffset(10);
        player.setMaxCellCount(30);
    }

    @Override
    protected void initializeWithInteractionTarget(final EntityRef workstation) {
        WorkstationScreenUtils.setupInventoryGrid(workstation, input, "INPUT");
        WorkstationScreenUtils.setupInventoryGrid(workstation, output, "OUTPUT");

        millButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        EntityRef character = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                        //character.send(new WorkstationProcessRequest(workstation, matchingUpgradeRecipe));
                    }
                });
        millButton.setVisible(false);

        millingProgress.bindVisible(
                new Binding<Boolean>() {
                    @Override
                    public Boolean get() {
                        WorkstationProcessingComponent processing = workstation.getComponent(WorkstationProcessingComponent.class);
                        if (processing == null) {
                            return false;
                        }
                        WorkstationProcessingComponent.ProcessDef millingProcess = processing.processes.get("Stonecrafting:Milling");
                        return millingProcess != null;
                    }

                    @Override
                    public void set(Boolean value) {
                    }
                }
        );
        millingProgress.bindValue(
                new Binding<Float>() {
                    @Override
                    public Float get() {
                        WorkstationProcessingComponent processing = workstation.getComponent(WorkstationProcessingComponent.class);
                        if (processing == null) {
                            return 1f;
                        }
                        WorkstationProcessingComponent.ProcessDef millingProcess = processing.processes.get("Stonecrafting:Milling");
                        if (millingProcess == null) {
                            return 1f;
                        }

                        long gameTime = CoreRegistry.get(Time.class).getGameTimeInMs();

                        return 1f * (gameTime - millingProcess.processingStartTime) / (millingProcess.processingFinishTime - millingProcess.processingStartTime);
                    }

                    @Override
                    public void set(Float value) {
                    }
                }
        );

        // Bind the tooltip strings for all the widgets.
        input.bindTooltipString(
                new ReadOnlyBinding<String>() {
                    @Override
                    public String get() {
                        return "Place item to be milled here.";
                    }
                }
        );

        // Bind the tooltip strings for all the widgets.
        output.bindTooltipString(
                new ReadOnlyBinding<String>() {
                    @Override
                    public String get() {
                        return "Milled item will be returned here.";
                    }
                }
        );
    }


    @Override
    public boolean isModal() {
        return false;
    }
}
