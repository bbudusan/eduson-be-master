package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "LiveEventTag")
@Table(name = "live_event_tags")
public class LiveEventTag {

    @EmbeddedId
    private LiveEventTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("liveEventId")
    private LiveEvent liveEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    private LiveEventTag() {
    }

    public LiveEventTag(LiveEvent liveEvent, Tag tag) {
        this.liveEvent = liveEvent;
        this.tag = tag;
        this.id = new LiveEventTagId(liveEvent.getId(), tag.getId());
    }

    // Getters and setters omitted for brevity

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        LiveEventTag that = (LiveEventTag) o;
        return Objects.equals(liveEvent, that.liveEvent) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liveEvent, tag);
    }
}