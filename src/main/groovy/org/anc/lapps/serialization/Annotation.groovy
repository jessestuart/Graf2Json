package org.anc.lapps.serialization

/**
 * @author Keith Suderman
 */
class Annotation {
    /** A unique ID assigned to this annotation.
     * <p>
     * <em>Note:</em> This ID value is assigned to the annotation by the framework
     * and is not to be confused with an ID (xml:id, etc) value that the annotation
     * itself might contain.
     */
    String id

    /** The label used for the annotation, e.g. tok, s, etc. */
    String label

    /** The start offset of the annotation. */
    long start = -1
    /** The end offset of the annotation. */
    long end = -1

    /** Features of the annotation. */
    Map features = [:]

    /** Features assigned by the framework to the annotation. E.g. a confidence
     * score, the processor that generated the annotation etc.
     */
    Map metadata = [:]

    public Annotation() { }
    public Annotation(Map map) {
        map.each { key, value ->
            switch(key) {
                case 'label':
                    this.label = value
                    break
                case 'start':
                    this.start = value as Long
                    break
                case 'end':
                    this.end = value as Long
                    break
                case 'id':
                    this.id = value
                    break
                case 'features':
                    this.features << value
                    break
                case 'metadata':
                    this.metadata << value
                    break
                default:
                    println "${key} = ${value}"
                    features[key] = value
                    break
            }
        }
    }

    String toString() {
        return "${label} (${start}-${end}) ${features}"
    }

}
