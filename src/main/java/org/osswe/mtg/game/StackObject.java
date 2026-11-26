package org.osswe.mtg.game;

import java.util.List;
import java.util.UUID;

/** Stack is ordered bottom-to-top; the final element is the top object. */
public record StackObject(
        UUID objectId, StackObjectKind kind, PlayerId controllerId,
        UUID cardInstanceId, UUID sourceCardInstanceId, String abilityKey,
        List<TargetReference> targets) {

    public StackObject {
        if (objectId == null || kind == null || controllerId == null) {
            throw new IllegalArgumentException("Stack object identity, kind and controller are required");
        }
        if (kind == StackObjectKind.SPELL && cardInstanceId == null) {
            throw new IllegalArgumentException("A spell stack object must reference its card instance");
        }
        if (kind != StackObjectKind.SPELL && (abilityKey == null || abilityKey.isBlank())) {
            throw new IllegalArgumentException("An ability stack object must have an ability key");
        }
        targets = List.copyOf(targets == null ? List.of() : targets);
    }

    public record TargetReference(String targetType, UUID targetId) {
        public TargetReference {
            if (targetType == null || targetType.isBlank() || targetId == null) {
                throw new IllegalArgumentException("Target type and id are required");
            }
        }
    }
}
