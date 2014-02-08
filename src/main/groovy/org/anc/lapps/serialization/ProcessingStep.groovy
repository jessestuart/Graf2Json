package org.anc.lapps.serialization

/**
 * @author Keith Suderman
 */
class ProcessingStep {
    Map metadata
    List<Annotation> annotations

    public ProcessingStep() {
        metadata = [:]
        annotations = []
    }

    public ProcessingStep(Map map) {
        metadata = map.metadata
        annotations = map.annotations
    }

    void addMetaData(String name, String value) {
        metadata[name] = value
    }

    void addAnnotation(Annotation annotation) {
        annotations << annotation
    }
}
