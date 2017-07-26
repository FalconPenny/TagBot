package me.falconpenny.tagbot.data;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TagUser {
    private final long userId;

    private Set<Tag> tags = new HashSet<>();
    private long lastTag = 0;
    private long lastAlert = 0;

    public int tagAmount() {
        return tags.stream().mapToInt(Tag::getAmount).sum();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }
}
