package org.framework.annotations;

import java.lang.annotation.Annotation;

public enum SprungClassAnnotation {
    SERVICE(Service.class);

    private final Class<? extends Annotation> annotationClass;

    SprungClassAnnotation(Class<? extends Annotation>  annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Class<? extends Annotation>  value() {
        return annotationClass;
    }
}
