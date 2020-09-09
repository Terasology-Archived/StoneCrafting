// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.stoneCrafting.ui;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.rendering.nui.BaseInteractionScreen;
import org.terasology.inventory.rendering.nui.layers.ingame.InventoryGrid;
import org.terasology.nui.UIWidget;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.ActivateEventListener;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILoadBar;
import org.terasology.processing.ui.WorkstationScreenUtils;
import org.terasology.workstation.component.WorkstationProcessingComponent;

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
                        WorkstationProcessingComponent processing =
                                workstation.getComponent(WorkstationProcessingComponent.class);
                        if (processing == null) {
                            return false;
                        }
                        WorkstationProcessingComponent.ProcessDef millingProcess = processing.processes.get(
                                "Stonecrafting:Milling");
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
                        WorkstationProcessingComponent processing =
                                workstation.getComponent(WorkstationProcessingComponent.class);
                        if (processing == null) {
                            return 1f;
                        }
                        WorkstationProcessingComponent.ProcessDef millingProcess = processing.processes.get(
                                "Stonecrafting:Milling");
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
