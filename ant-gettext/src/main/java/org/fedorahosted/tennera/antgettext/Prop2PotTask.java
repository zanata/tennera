/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.fedorahosted.tennera.antgettext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.fedorahosted.openprops.Properties;
import org.jboss.jgettext.Catalog;
import org.jboss.jgettext.Message;
import org.jboss.jgettext.Occurence;
import org.jboss.jgettext.catalog.write.CatalogWriter;

/**
 * Converts Java Properties files into gettext template files (POT).
 * 
 * @author <a href="sflaniga@redhat.com">Sean Flanigan</a>
 * @version $Revision: $
 */
public class Prop2PotTask extends MatchingTask
{
   private File srcDir;
   private File dstDir;
   // In future, we might use the original English properties as a template 
   // as Translate Toolkit's po2prop does.   po2prop may use templates to 
   // (a) preserve ordering, and (b) find the ResourceBundle key (in case 
   // the #: location comments have been removed).  
//   private File tmpDir;

   public void setSrcDir(File srcDir)
   {
      this.srcDir = srcDir;
   }

//   public void setTmpDir(File tmpDir)
//   {
//      this.tmpDir = tmpDir;
//   }
   
   public void setDstDir(File dstDir)
   {
      this.dstDir = dstDir;
   }
   
   @Override
   public void execute() throws BuildException
   {
      DirUtil.checkDir(srcDir, "srcDir", false);
      DirUtil.checkDir(dstDir, "dstDir", true);
//      DirUtil.checkDir(tmpDir, "tmpDir", false);

      try
      {
         DirectoryScanner ds = super.getDirectoryScanner(srcDir);
         ds.scan();
         String[] files = ds.getIncludedFiles();

         for (int i = 0; i < files.length; i++)
         {
            String propFilename = files[i];
            File propFile = new File(srcDir, propFilename);
            String potFilename = propFilename.substring(0, propFilename.length()-"properties".length())+"pot";
            File potFile = new File(dstDir, potFilename);
            Properties props = new Properties();
            BufferedReader in = new BufferedReader(new FileReader(propFile));
            props.load(in);
            System.out.println("Generating "+potFile+" from "+propFile);
            potFile.getParentFile().mkdirs();
            BufferedWriter out = new BufferedWriter(new FileWriter(potFile));
            try
            {
//               String genComment = potFilename+" generated by "+Prop2PotTask.class.getName()+" from "+propFilename;
//               PotWritingUtil.writePotHeader(out);
        	Catalog cat = new Catalog();
        	CatalogWriter writer = new CatalogWriter(cat);

        	for (String key : props.stringPropertyNames())
        	{
        	    String englishString = props.getProperty(key);
        	    // NB java.util.Properties throws away comments...
        	    String comment = props.getComment(key);
//String debugMsg = "key="+key+" comment='"+comment+"'";
//debugMsg = debugMsg.replace("\n", "\\n").replace("\r", "\\r");
//System.err.println(debugMsg);
//        	    PotWritingUtil.writePotEntry(
//        		    out, comment, key, true, key, englishString);
        	       /*
        	           BufferedWriter out, 
        	           String extractedComment, 
        	           String locationReference, 
        	           boolean isJavaFormat, 
        	           String context, 
        	           String key) throws IOException
        	        */
        	    Message message = new Message();
        	    message.addExtractedComment(comment);
        	    message.addOccurence(new Occurence(key));
        	    message.addFormat("java-format"); //  FIXME check this
        	    message.setMsgctxt(key);
        	    message.setMsgid(englishString);
        	    cat.addMessage(message);
        	}
        	writer.writeTo(out);
            }
            finally
            {
               in.close();
               out.close();
            }
         }
      }
      catch (Exception e)
      {
         throw new BuildException(e);
      }
   }

}
