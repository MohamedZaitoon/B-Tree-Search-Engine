package eg.edu.alexu.csd.filestructure.btree.cs40;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Utilities {
	
	private static final  String SEP = System.getProperty("file.separator");

	/**
	 * search for all files in the directory.
	 * @param dirPath path of directory
	 * @return list of files' paths or empty list if directory is empty,
	 * 					or null in case of dirPath is null or empty.
	 */
	public static List<String> getFilePaths(String dirPath){
		if(dirPath == null || dirPath.isEmpty())return null;
		List<String> paths = new LinkedList<>();
		Queue<String> q = new LinkedList<>();
		q.add(dirPath);
		while(!q.isEmpty()) {
			String path = q.remove();
			File dir = new File(path);
			String[] contents = dir.list();
			if(contents == null) continue;
			for(String s : contents) {
				File f = new File(path+SEP+s);
				if(f.isFile()) {
					paths.add(f.getPath());
				}else {
					q.add(f.getPath());
				}
			}
		}
		return paths;
	}
}
