package peakaboo.datasource.components.fileformat;


/**
 * Describes how certain a FileFormat & DataSource are that the given file(s) 
 * are compatible with the DataSource.
 * 
 * @author NAS
 *
 */
public enum FileFormatCompatibility {

	/**
	 * Indicates that the file(s) are not a match, and that the FileFormat, 
	 * and any DataSource providing it, should not be considered as a
	 * candidate for reading the given data.
	 */
	NO,
	
	/**
	 * Indicates that the file(s) may be compatible based on an inspection of 
	 * the filenames, but not the contents. This is usually used when a file
	 * format has a distinct filename, but contents generic enough that they
	 * cannot be verified by a cursory examination. 
	 */
	MAYBE_BY_FILENAME,
	
	/**
	 * Indicates that the file(s) may be compatible based on an inspection of 
	 * the contents. This is usually used when the contents of a file do not 
	 * contain any specifically identifying strings or magic numbers allowing 
	 * the code to be certain.
	 */
	MAYBE_BY_CONTENTS,
	
	/**
	 * Indicated that the file(s) are definitely compatible based on an 
	 * examination of the contents. This conclusion may also be based on an 
	 * examination of the filenames, but this value should not be returned 
	 * unless an inspection of the contents also yields an *obvious* match
	 * such as a specific string or magic number uniquely identifying the 
	 * format.
	 */
	YES_BY_CONTENTS
	
}
