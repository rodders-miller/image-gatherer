package imageGather;

import java.io.*;
import java.util.zip.CheckedInputStream;
import java.util.zip.CRC32;
import java.util.HashMap;
import java.util.Calendar;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/** 
 * Unique File Application
 */
public class App
{
    public static void main( String[] args )
    {
        try{
		
			System.out.println( "Hello World!" );
			
			String inputDirStr = args[0];
			String outputDirStr = args[1];

			String[] extentions = {"JPG","jpg"};
			
			// Get the input and output path directories
			Path inputPath = Paths.get(inputDirStr);
			Path outputPath = Paths.get(outputDirStr);
			
			// walk the file tree looking for unique imgaes
			UniqueFileVisitor visitor = new UniqueFileVisitor(extentions);
			
			Files.walkFileTree(inputPath,visitor);

			// Iterate over the unique files and copy them to the output
			// Todo - ensure no file name clashes and create directory with
			// file year in
				
			for(Path path : visitor.getUnqiueFiles()) {

				BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
				long lastModifiedLong = attrs.lastModifiedTime().toMillis();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(lastModifiedLong);
				int year = cal.get(Calendar.YEAR);
				
				Path outputDir = outputPath.resolve(""+year);

				// make the directory structure to hold the file				
				outputDir.toFile().mkdirs();
				
				// create the path to the new file and copy from exsiting location
				Path outputFile = outputDir.resolve(path.getFileName());
				Files.copy(path, outputFile, StandardCopyOption.COPY_ATTRIBUTES);
			}
		} 
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
  }
	
}
