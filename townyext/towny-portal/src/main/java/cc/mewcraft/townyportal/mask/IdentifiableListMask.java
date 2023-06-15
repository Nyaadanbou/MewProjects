/*
   Copyright 2023-2023 Huynh Tien

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package cc.mewcraft.townyportal.mask;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.lib.core.common.CollectionUtils;
import me.hsgamer.bettergui.lib.core.minecraft.gui.button.Button;
import me.hsgamer.bettergui.lib.core.minecraft.gui.mask.impl.ButtonPaginatedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.mask.WrappedPaginatedMask;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.bettergui.requirement.type.ConditionRequirement;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * To implement a List Mask that display all the towns/nations from Towny, we can just make a small change in the
 * PlayerListMask. Instead of fetching all online players as targets, we fetch all the mayors/kings as targets, from
 * which we can get the UUIDs of them, then we can get the OfflinePlayer instances. Since the PAPI can parse offline
 * players, it of course works with offline mayors/kings as well. So finally, we can create unique buttons for each town
 * and nation, with the name/lore parsed with PAPI.
 * <p>
 * The subclass only needs to provide a specific list of UUIDs.
 */
public abstract class IdentifiableListMask extends WrappedPaginatedMask<ButtonPaginatedMask> {
    private static final Pattern pattern = Pattern.compile("\\{current_player(_([^{}]+))?}");

    private final Map<UUID, ResidentEntry> identifiableEntryMap = new ConcurrentHashMap<>();
    private final String variablePrefix;
    private Map<String, Object> templateButton = Collections.emptyMap();
    private ConditionRequirement playerCondition;
    private List<String> viewerConditionTemplate = Collections.emptyList();
    private boolean viewSelf = true;

    public IdentifiableListMask(MaskBuilder.Input input) {
        super(input);
        this.variablePrefix = getName() + "_current_";
        input.menu.getVariableManager().register(this.variablePrefix, (original, uuid) -> {
            String[] split = original.split(";", 3);
            if (split.length < 2) {
                return null;
            }
            UUID targetId;
            try {
                targetId = UUID.fromString(split[0]);
            } catch (IllegalArgumentException e) {
                return null;
            }
            String variable = split[1];
            boolean isPAPI = split.length == 3 && Boolean.parseBoolean(split[2]);
            String finalVariable;
            if (isPAPI) {
                finalVariable = "%" + variable + "%";
            } else {
                finalVariable = "{" + variable + "}";
            }
            return StringReplacerApplier.replace(finalVariable, targetId, true);
        });
    }

    private String replaceShortcut(String string, UUID targetId) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String variable = matcher.group(2);
            String replacement;
            if (variable == null) {
                replacement = "{" + this.variablePrefix + targetId.toString() + ";player}";
            } else {
                boolean isPAPI = variable.startsWith("papi_");
                if (isPAPI) {
                    variable = variable.substring(5);
                }
                replacement = "{" + this.variablePrefix + targetId.toString() + ";" + variable + ";" + isPAPI + "}";
            }
            string = string.replace(matcher.group(), replacement);
        }
        return string;
    }

    private Object replaceShortcut(Object obj, UUID targetId) {
        if (obj instanceof String) {
            return replaceShortcut((String) obj, targetId);
        } else if (obj instanceof Collection) {
            List<Object> replaceList = new ArrayList<>();
            ((Collection<?>) obj).forEach(o -> replaceList.add(replaceShortcut(o, targetId)));
            return replaceList;
        } else if (obj instanceof Map) {
            // noinspection unchecked, rawtypes
            ((Map) obj).replaceAll((k, v) -> replaceShortcut(v, targetId));
        }
        return obj;
    }

    private Map<String, Object> replaceShortcut(Map<String, Object> map, UUID targetId) {
        Map<String, Object> newMap = new LinkedHashMap<>();
        map.forEach((k, v) -> newMap.put(k, replaceShortcut(v, targetId)));
        return newMap;
    }

    private List<String> replaceShortcut(List<String> list, UUID targetId) {
        List<String> newList = new ArrayList<>();
        list.forEach(s -> newList.add(replaceShortcut(s, targetId)));
        return newList;
    }

    private boolean canView(UUID viewer, ResidentEntry targetResidentEntry) {
        if (!this.viewSelf && viewer.equals(targetResidentEntry.uuid)) {
            return false;
        }
        if (!targetResidentEntry.activated.get()) {
            return false;
        }
        return targetResidentEntry.viewerCondition.test(viewer);
    }

    private ResidentEntry newTownyObjectEntry(UUID identifiable) {
        Map<String, Object> replacedButtonSettings = replaceShortcut(this.templateButton, identifiable);
        Button button = ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_player_" + identifiable + "_button", replacedButtonSettings))
            .map(Button.class::cast)
            .orElse(Button.EMPTY);
        button.init();

        List<String> replacedViewerConditions = replaceShortcut(this.viewerConditionTemplate, identifiable);
        ConditionRequirement viewerCondition = new ConditionRequirement(new RequirementBuilder.Input(getMenu(), "condition", getName() + "_player_" + identifiable + "_condition", replacedViewerConditions));
        return new ResidentEntry(identifiable, button, viewer -> viewerCondition.check(viewer).isSuccess);
    }

    /**
     * @return uuids of target players
     */
    public abstract Stream<UUID> getIdentifiable();

    private List<Button> getTownyObjectButtons(UUID viewer) {
        return getIdentifiable()
            .map(this.identifiableEntryMap::get)
            .filter(Objects::nonNull)
            .filter(entry -> canView(viewer, entry))
            .map(entry -> entry.button)
            .collect(Collectors.toList());
    }

    @Override
    protected ButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        this.templateButton = Optional.ofNullable(MapUtil.getIfFound(section, "template", "button"))
            .flatMap(MapUtil::castOptionalStringObjectMap)
            .orElse(Collections.emptyMap());
        this.viewSelf = Optional.ofNullable(MapUtil.getIfFound(section, "view-self", "self"))
            .map(String::valueOf)
            .map(Boolean::parseBoolean)
            .orElse(true);
        this.playerCondition = Optional.ofNullable(MapUtil.getIfFound(section, "player-condition"))
            .map(o -> new ConditionRequirement(new RequirementBuilder.Input(getMenu(), "condition", getName() + "_player_condition", o)))
            .orElse(null);
        this.viewerConditionTemplate = Optional.ofNullable(MapUtil.getIfFound(section, "viewer-condition"))
            .map(CollectionUtils::createStringListFromObject)
            .orElse(Collections.emptyList());
        return new ButtonPaginatedMask(getName(), MultiSlotUtil.getSlots(section)) {
            @Override
            public @NotNull List<@NotNull Button> getButtons(@NotNull UUID uuid) {
                return getTownyObjectButtons(uuid);
            }
        };
    }

    @Override public void init() {
        super.init();

        getIdentifiable().forEach(identifiable -> this.identifiableEntryMap.compute(identifiable, (currentId, currentEntry) -> {
            if (currentEntry == null) {
                currentEntry = newTownyObjectEntry(currentId);
            }
            currentEntry.activated.lazySet(this.playerCondition == null || this.playerCondition.check(currentId).isSuccess);
            return currentEntry;
        }));
    }

    @Override
    public void stop() {
        super.stop();
        this.identifiableEntryMap.values().forEach(identifiableEntry -> identifiableEntry.button.stop());
        this.identifiableEntryMap.clear();
    }

    private static class ResidentEntry {
        final UUID uuid; // target uuid
        final Button button;
        final Predicate<UUID> viewerCondition;
        final AtomicBoolean activated = new AtomicBoolean();

        private ResidentEntry(UUID uuid, Button button, Predicate<UUID> viewerCondition) {
            this.uuid = uuid;
            this.button = button;
            this.viewerCondition = viewerCondition;
        }
    }
}
