package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.products.webinars.Webinar;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "WebinarModule")
@Table(name = "webinar_modules")
public class WebinarModule {

    @EmbeddedId
    private WebinarModuleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("webinarId")
    private Webinar webinar;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("moduleId")
    private Module module;

    private WebinarModule() {
    }

    public WebinarModule(Webinar webinar, Module module) {
        this.webinar = webinar;
        this.module = module;
        this.id = new WebinarModuleId(webinar.getId(), module.getId());
    }

    // Getters and setters omitted for brevity

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        WebinarModule that = (WebinarModule) o;
        return Objects.equals(webinar, that.webinar) &&
                Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webinar, module);
    }
}