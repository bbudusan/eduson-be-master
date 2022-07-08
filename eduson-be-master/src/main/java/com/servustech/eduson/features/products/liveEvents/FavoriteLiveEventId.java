package com.servustech.eduson.features.products.liveEvents;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class FavoriteLiveEventId
    implements Serializable {
 
    @Column(name = "live_event_id")
    private Long liveEventId;
 
    @Column(name = "user_id")
    private Long userId;
 
    private FavoriteLiveEventId() {}
 
    public FavoriteLiveEventId(
        Long liveEventId,
        Long userId) {
        this.liveEventId = liveEventId;
        this.userId = userId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        FavoriteLiveEventId that = (FavoriteLiveEventId) o;
        return Objects.equals(liveEventId, that.liveEventId) &&
               Objects.equals(userId, that.userId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(liveEventId, userId);
    }
}