package runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLister {

	private final String path;

	public FileLister(String path) {
		this.path = path;
	}

	private void getAllDaoFilesIn(File aStartingDir, ArrayList<File> result) {
		try {
			File[] filesAndDirs = aStartingDir.listFiles();
			for (File file : filesAndDirs) {
				if (file.isDirectory()) {
					getAllDaoFilesIn(file, result);
				} else if (file.isFile() && isDao(file)) {
					result.add(file);
				}
			}
		} catch (Throwable e) {
			System.err.println("getAllFiles error in " + aStartingDir.getPath());
		}
	}
	
	private boolean isDao(File file) {
		return file.getName().toUpperCase().endsWith("DAO.JAVA");
	}

	public List<File> getAllDaoFiles() {
		ArrayList<File> result = new ArrayList<File>();
		getAllDaoFilesIn(new File(path), result);
		return result;
	}

}
