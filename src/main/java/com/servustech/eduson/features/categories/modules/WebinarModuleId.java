package com.servustech.eduson.features.categories.modules;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class WebinarModuleId
    implements Serializable {
 
    @Column(name = "webinar_id")
    private Long webinarId;
 
    @Column(name = "module_id")
    private Long moduleId;
 
    private WebinarModuleId() {}
 
    public WebinarModuleId(
        Long webinarId,
        Long moduleId) {
        this.webinarId = webinarId;
        this.moduleId = moduleId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        WebinarModuleId that = (WebinarModuleId) o;
        return Objects.equals(webinarId, that.webinarId) &&
               Objects.equals(moduleId, that.moduleId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(webinarId, moduleId);
    }
}