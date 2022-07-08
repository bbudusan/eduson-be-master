package com.servustech.eduson.features.categories.tags;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class LiveEventTagId
    implements Serializable {
 
    @Column(name = "live_event_id")
    private Long liveEventId;
 
    @Column(name = "tag_id")
    private Long tagId;
 
    private LiveEventTagId() {}
 
    public LiveEventTagId(
        Long liveEventId,
        Long tagId) {
        this.liveEventId = liveEventId;
        this.tagId = tagId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        LiveEventTagId that = (LiveEventTagId) o;
        return Objects.equals(liveEventId, that.liveEventId) &&
               Objects.equals(tagId, that.tagId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(liveEventId, tagId);
    }
}