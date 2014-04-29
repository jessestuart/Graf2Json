package org.anc.json

File index = new File("masc3-json.index")
PrintWriter out = new PrintWriter(index)

String corpRoot = "/var/corpora/MASC-3.0.0-json"
File root = new File(corpRoot)

root.eachDirRecurse { File dir ->
   dir.eachFileMatch ~/.*\.json/, { File file ->
      index.println("${file.getName()} ${file.getPath()}")
   }
}

