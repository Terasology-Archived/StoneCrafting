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
package org.terasology.stoneCrafting.system;

import com.google.common.base.Predicate;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.multiBlock.Basic3DSizeFilter;
import org.terasology.multiBlock.MultiBlockFormRecipeRegistry;
import org.terasology.multiBlock.UniformBlockReplacementCallback;
import org.terasology.multiBlock.recipe.UniformMultiBlockFormItemRecipe;
import org.terasology.processing.system.ToolTypeEntityFilter;
import org.terasology.processing.system.UseOnTopFilter;
import org.terasology.registry.In;
import org.terasology.stoneCrafting.Stonecrafting;
import org.terasology.workstation.system.WorkstationRegistry;
import org.terasology.workstationCrafting.component.CraftingStationMaterialComponent;
import org.terasology.workstationCrafting.system.CraftInHandRecipeRegistry;
import org.terasology.workstationCrafting.system.CraftingWorkstationProcess;
import org.terasology.workstationCrafting.system.CraftingWorkstationProcessFactory;
import org.terasology.workstationCrafting.system.recipe.render.result.BlockRecipeResultFactory;
import org.terasology.workstationCrafting.system.recipe.workstation.DefaultWorkstationRecipe;
import org.terasology.world.block.BlockManager;

@RegisterSystem
public class RegisterStonecraftingRecipes extends BaseComponentSystem {
    @In
    private CraftInHandRecipeRegistry recipeRegistry;
    @In
    private WorkstationRegistry workstationRegistry;
    @In
    private MultiBlockFormRecipeRegistry multiBlockFormRecipeRegistry;
    @In
    private BlockManager blockManager;
    @In
    private PrefabManager prefabManager;
    @In
    private EntityManager entityManager;

    @Override
    public void initialise() {
        workstationRegistry.registerProcessFactory(Stonecrafting.BASIC_STONECRAFTING_PROCESS, new CraftingWorkstationProcessFactory());
        workstationRegistry.registerProcessFactory(Stonecrafting.NOVICE_STONECRAFTING_PROCESS, new CraftingWorkstationProcessFactory());
        workstationRegistry.registerProcessFactory(Stonecrafting.STANDARD_STONECRAFTING_PROCESS, new CraftingWorkstationProcessFactory());

        addWorkstationFormingRecipes();

        addBasicStoneWorkstationBlockShapeRecipes();
    }

    private void addWorkstationFormingRecipes() {
        multiBlockFormRecipeRegistry.addMultiBlockFormItemRecipe(
                new UniformMultiBlockFormItemRecipe(new ToolTypeEntityFilter("hammer"), new UseOnTopFilter(),
                        new StationTypeFilter("Stonecrafting:BasicStonecraftingStation"), new Basic3DSizeFilter(2, 1, 1, 1),
                        "Stonecrafting:BasicStonecraftingStation",
                        new UniformBlockReplacementCallback<Void>(blockManager.getBlock("Stonecrafting:BasicStoneStation"))));
    }

    private void addBasicStoneWorkstationBlockShapeRecipes() {
        addWorkstationBlockShapesRecipe(Stonecrafting.BASIC_STONECRAFTING_PROCESS, "Building|Cobble Stone|Stonecrafting:CobbleBlock",
                "Woodcrafting:stone", 2, "hammer", 1, "CoreAssets:CobbleStone", 1);
        addWorkstationBlockShapesRecipe(Stonecrafting.STANDARD_STONECRAFTING_PROCESS, "Building|Bricks|Stonecrafting:BrickBlock",
                "Stonecrafting:brick", 2, "hammer", 1, "CoreAssets:Brick", 1);
    }

    private void addShapeRecipe(String processType, String recipeNamePrefix, String ingredient, int ingredientBasicCount,
                                String tool, int toolDurability, String blockResultPrefix, int blockResultCount,
                                String shape, int ingredientMultiplier, int resultMultiplier, int toolDurabilityMultiplier) {
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount, shape,
                "engine", ingredientMultiplier, resultMultiplier, toolDurabilityMultiplier);
    }

    private void addShapeRecipe(String processType, String recipeNamePrefix, String ingredient, int ingredientBasicCount,
                                String tool, int toolDurability, String blockResultPrefix, int blockResultCount,
                                String shape, String module, int ingredientMultiplier, int resultMultiplier, int toolDurabilityMultiplier) {
        DefaultWorkstationRecipe shapeRecipe = new DefaultWorkstationRecipe();
        shapeRecipe.addIngredient(ingredient, ingredientBasicCount * ingredientMultiplier);
        shapeRecipe.addRequiredTool(tool, toolDurability * toolDurabilityMultiplier);
        shapeRecipe.setResultFactory(new BlockRecipeResultFactory(blockManager.getBlockFamily(blockResultPrefix + ":" + module + ":" + shape).getArchetypeBlock(),
                blockResultCount * resultMultiplier));

        workstationRegistry.registerProcess(processType, new CraftingWorkstationProcess(processType, recipeNamePrefix + shape, shapeRecipe, null, entityManager));
    }

    private void addWorkstationBlockShapesRecipe(String processType, String recipeNamePrefix, String ingredient, int ingredientBasicCount,
                                                 String tool, int toolDurability, String blockResultPrefix, int blockResultCount) {
        DefaultWorkstationRecipe fullBlockRecipe = new DefaultWorkstationRecipe();
        fullBlockRecipe.addIngredient(ingredient, ingredientBasicCount);
        fullBlockRecipe.addRequiredTool(tool, toolDurability);
        fullBlockRecipe.setResultFactory(new BlockRecipeResultFactory(blockManager.getBlockFamily(blockResultPrefix).getArchetypeBlock(), blockResultCount));

        workstationRegistry.registerProcess(processType, new CraftingWorkstationProcess(processType, recipeNamePrefix, fullBlockRecipe, null, entityManager));

        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "Stair", 3, 4, 2);

        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "Slope", 1, 2, 2);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "UpperHalfSlope", 1, 2, 2);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "SlopeCorner", 1, 2, 2);

        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "SteepSlope", 1, 1, 2);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "QuarterSlope", 1, 8, 2);

        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "HalfBlock", 1, 2, 1);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "EighthBlock", 1, 8, 1);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "HalfSlope", 1, 4, 2);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "HalfSlopeCorner", 1, 6, 1);

        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "PillarTop", "structuralResources", 1, 1, 2);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "Pillar", "structuralResources", 1, 1, 2);
        addShapeRecipe(processType, recipeNamePrefix, ingredient, ingredientBasicCount, tool, toolDurability, blockResultPrefix, blockResultCount,
                "PillarBase", "structuralResources", 1, 1, 2);
    }

    private final class StationTypeFilter implements Predicate<EntityRef> {
        private String stationType;

        private StationTypeFilter(String stationType) {
            this.stationType = stationType;
        }

        @Override
        public boolean apply(EntityRef entity) {
            CraftingStationMaterialComponent stationMaterial = entity.getComponent(CraftingStationMaterialComponent.class);
            return stationMaterial != null && stationMaterial.stationType.equals(stationType);
        }
    }
}
