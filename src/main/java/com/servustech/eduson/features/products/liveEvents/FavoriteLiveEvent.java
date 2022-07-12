package com.servustech.eduson.features.products.liveEvents;

import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import com.servustech.eduson.features.account.User;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "FavoriteLiveEvent")
@Table(name = "live_event_favorites")
public class FavoriteLiveEvent {
 
    @EmbeddedId
    private FavoriteLiveEventId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("liveEventId")
    private LiveEvent liveEvent;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
 
    private FavoriteLiveEvent() {}
 
    public FavoriteLiveEvent(LiveEvent liveEvent, User user) {
        this.liveEvent = liveEvent;
        this.user = user;
        this.id = new FavoriteLiveEventId(liveEvent.getId(), user.getId());
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        FavoriteLiveEvent that = (FavoriteLiveEvent) o;
        return Objects.equals(liveEvent, that.liveEvent) &&
               Objects.equals(user, that.user);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(liveEvent, user);
    }
}