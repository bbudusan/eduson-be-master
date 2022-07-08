package com.servustech.eduson.features.categories.modules;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class LiveEventModuleId
    implements Serializable {
 
    @Column(name = "live_event_id")
    private Long liveEventId;
 
    @Column(name = "module_id")
    private Long moduleId;
 
    private LiveEventModuleId() {}
 
    public LiveEventModuleId(
        Long liveEventId,
        Long moduleId) {
        this.liveEventId = liveEventId;
        this.moduleId = moduleId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        LiveEventModuleId that = (LiveEventModuleId) o;
        return Objects.equals(liveEventId, that.liveEventId) &&
               Objects.equals(moduleId, that.moduleId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(liveEventId, moduleId);
    }
}