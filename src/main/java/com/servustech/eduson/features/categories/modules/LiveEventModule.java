package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "LiveEventModule")
@Table(name = "live_event_modules")
public class LiveEventModule {

    @EmbeddedId
    private LiveEventModuleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("liveEventId")
    private LiveEvent liveEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("moduleId")
    private Module module;

    private LiveEventModule() {
    }

    public LiveEventModule(LiveEvent liveEvent, Module module) {
        this.liveEvent = liveEvent;
        this.module = module;
        this.id = new LiveEventModuleId(liveEvent.getId(), module.getId());
    }

    // Getters and setters omitted for brevity

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        LiveEventModule that = (LiveEventModule) o;
        return Objects.equals(liveEvent, that.liveEvent) &&
                Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liveEvent, module);
    }
}