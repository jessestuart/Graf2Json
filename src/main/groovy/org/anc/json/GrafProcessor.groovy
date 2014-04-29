package org.anc.json

import org.anc.lapps.serialization.Annotation
import org.anc.lapps.serialization.Container
import org.anc.lapps.serialization.ProcessingStep;
import org.slf4j.Logger
import org.slf4j.LoggerFactory;
import org.xces.graf.api.IAnnotation
import org.xces.graf.api.IFeature
import org.xces.graf.api.IFeatureStructure
import org.xces.graf.api.IGraph;
import org.xces.graf.api.INode
import org.xces.graf.api.IRegion
import org.xces.graf.io.GrafParser
import org.xces.graf.io.dom.DocumentHeader
import org.xces.graf.io.dom.ResourceHeader
import org.xces.graf.util.GraphUtils;

class GrafProcessor
{
   public static final String MASC_ROOT = "/var/corpora/MASC-3.0.0"
   public static final String RESOURCE_HEADER = MASC_ROOT + "/resource-header.xml"
   public static final String OUTPUT_ROOT = MASC_ROOT + "-json"
   
   PrintWriter index = new PrintWriter(new File(OUTPUT_ROOT, "masc3-json.index"))
   
   Logger log = LoggerFactory.getLogger(GrafProcessor.class)
   
   private GrafParser parser

   void run()
   {
      File corpRoot = new File(MASC_ROOT)
      File outDir = new File(OUTPUT_ROOT)
      outDir.mkdir()
      ResourceHeader header = new ResourceHeader(new File(RESOURCE_HEADER))
      this.parser = new GrafParser(header)

      corpRoot.eachDirRecurse { File dir ->
         dir.eachFileMatch ~/.*\.hdr/, { File file ->
            process(file, outDir)
         }
      }
      index.close()
   }

   void process(File inputFile, File outputDir)
   {
      log.info "Processing file ${inputFile.name}"
      DocumentHeader docHeader = new DocumentHeader(inputFile)
      File textFile = new File(inputFile.getParent(), docHeader.getContentLocation())
      
      Container container = new Container()
      container.setText(textFile.text)
      
      docHeader.getAnnotationTypes().each { String type ->
         log.info "Processing annotation type $type"
         ProcessingStep step = new ProcessingStep()
         step.getMetadata().put("contains", type)
         
         File tokFile = new File(inputFile.parentFile, docHeader.getAnnotationLocation(type))
         IGraph graph = parser.parse(tokFile)
         graph.nodes().each { INode node ->
            IRegion span = GraphUtils.getSpan(node)
            int start = span?.getStart()?.getOffset()
            int end = span?.getEnd()?.getOffset()
            
            if (!start || !end) return            
            
            node.annotations().each { IAnnotation a ->
               if (!["f.penn", "f.ptbtok", "f.fntok"].contains(type) && a.label.equals("tok")) return
               
               Annotation ann = new Annotation()
               ann.id = a.getId()
               ann.label = a.getLabel()
               ann.start = start
               ann.end = end
               
               IFeatureStructure features = a.getFeatures()
               features.features().each { IFeature feature ->
                  ann.features.put(feature.getName(), feature.getValue())
               }
               step.addAnnotation(ann)
            }
         }
         container.getSteps().add(step)
      }
      
      String relativePath = inputFile.getPath().substring(MASC_ROOT.length()).replace(".hdr", ".json")
      File outputFile = new File(outputDir.getPath() + relativePath)
      outputFile.getParentFile().mkdirs()
      PrintWriter out = new PrintWriter(outputFile)
      out.println(container.toJson())
      out.close()
      
      index.println("${docHeader.getDocId()} ${outputFile.getPath()}")
   }
   
   static void main(String[] args) {
      GrafProcessor proc = new GrafProcessor()
      proc.run()
   }
}
