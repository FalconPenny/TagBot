package me.falconpenny.tagbot.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Set;

@Data
public class Tag {
    private final int amount;
    private final Set<Long> tagged;
    private final long epoch;

    @Setter(value = AccessLevel.PRIVATE)
    private int distinct = -1;

    public int getDistinct() {
        if (distinct == -1) {
            distinct = (int) tagged.stream().distinct().count();
        }
        return distinct;
    }
}
