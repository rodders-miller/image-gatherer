package uk.co.ssc.imagegather;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Collection;
import java.util.zip.CRC32;

public class UniqueFileVisitor extends SimpleFileVisitor<Path> {

	private HashMap<Long,Path> uniqueFiles = new HashMap<Long,Path>();
	private PathMatcher matcher = null;
	
	public UniqueFileVisitor(String[] extensions) {

		StringBuilder builder = new StringBuilder();
		
		// Create the matcher using a glob against all the string extensions
		// this will appear as glob:*.(ext1, ext2) to match against any of the 
		// given extensions
		builder.append("glob:**.{");

		for(int ii=0; ii < extensions.length;ii++){
			String glob = ii < (extensions.length-1) ? ",":"";
			builder.append(extensions[ii] + glob);
		} 
		
		builder.append("}");
		System.out.println(builder);
		matcher = FileSystems.getDefault().getPathMatcher(builder.toString());
	}
	
	public Collection<Path> getUnqiueFiles() {
		return uniqueFiles.values();
	}
	
    // Print information about
    // each type of file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {

		System.out.println(file);
		try{
			// check this file has the correct file extension
			if (matcher.matches(file)) {
				CRC32 crc = new CRC32();	
					
				// calculate file file's CRC
				// note this method is only suiable 
				// for relatively small files
					byte[] fileArray = Files.readAllBytes(file);
					crc.update(fileArray);
				
					// get the CRC value and hash this file against it
					uniqueFiles.put(crc.getValue(), file);
					System.out.println("matched!");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			return FileVisitResult.TERMINATE;
		}
		
        return FileVisitResult.CONTINUE;
    }

    // Print each directory visited.
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.format("Directory: %s%n", dir);
        return FileVisitResult.CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException 
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file,
                                       IOException exc) {
        System.err.println(exc);
        return FileVisitResult.CONTINUE;
    }
}