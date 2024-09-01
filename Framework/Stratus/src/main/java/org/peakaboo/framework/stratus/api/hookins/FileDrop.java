package org.peakaboo.framework.stratus.api.hookins;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.api.StratusLog;

/**
 * This class makes it easy to drag and drop files from the operating system to
 * a Java program. Any <tt>Component</tt> can be dropped onto, but only
 * <tt>javax.swing.JComponent</tt>s will indicate the drop event with a changed
 * border.
 * <p/>
 * To use this class, construct a new <tt>FileDrop</tt> by passing it the target
 * component and a <tt>Listener</tt> to receive notification when file(s) have
 * been dropped. Here is an example:
 * <p/>
 * <code><pre>
 *      JPanel myPanel = new JPanel();
 *      new FileDrop( myPanel, new FileDrop.Listener()
 *      {   public void filesDropped( File[] files )
 *          {   
 *              // handle file drop
 *              ...
 *          }   // end filesDropped
 *      }); // end FileDrop.Listener
 * </pre></code>
 * <p/>
 * You can specify the border that will appear when files are being dragged by
 * calling the constructor with a <tt>Border</tt>. Only
 * <tt>JComponent</tt>s will show any indication with a border.
 * <p/>
 * You can turn on some debugging features by passing a <tt>PrintStream</tt>
 * object (such as <tt>System.out</tt>) into the full constructor. A
 * <tt>null</tt> value will result in no extra debugging information being
 * output.
 * <p/>
 *
 * <p>
 * I'm releasing this code into the Public Domain. Enjoy.
 * </p>
 * <p>
 * Original author: Robert Harder, rob@iharder.net
 * </p>
 * <p>
 * Additional support:
 * </p>
 * <ul>
 * <li>September 2007, Nathan Blomquist -- Linux (KDE/Gnome) support added.</li>
 * <li>December 2010, Joshua Gerth</li>
 * </ul>
 *
 * @author Robert Harder
 * @author rharder@users.sf.net
 * @version 1.1.1
 */
public class FileDrop {
	private transient Border normalBorder;
	private transient DropTargetListener dropListener;

	/** Discover if the running JVM is modern enough to have drag and drop. */
	private static Boolean supportsDnD;

	// Default border color
	private static Color defaultBorderColor = StratusColour.moreTransparent(Stratus.getTheme().getHighlight(), 0.5f);
	private static Color rejectBorderColor = StratusColour.moreTransparent(Stratus.getTheme().getPalette().getColour("Red", "3"), 0.5f);
	private static int border = 3;
	
	private enum DropType {
		DROP_FILELIST,
		DROP_LINUX,
		DROP_URL, 
		DROP_FAIL,
		DROP_NONE,
	}
	DropType dropType = DropType.DROP_NONE;
	
	/**
	 * Constructs a {@link FileDrop} with a default light-blue border and, if
	 * <var>c</var> is a {@link java.awt.Container}, recursively sets all elements
	 * contained within as drop targets, though only the top level container will
	 * change borders.
	 *
	 * @param c        Component on which files will be dropped.
	 * @param listener Listens for <tt>filesDropped</tt>.
	 * @since 1.0
	 */
	public FileDrop(final Component c, final Listener listener) {
		this(	c, // Drop target
				BorderFactory.createMatteBorder(border, border, border, border, defaultBorderColor), // Drag border
				BorderFactory.createMatteBorder(border, border, border, border, rejectBorderColor), // Drag reject border
				true, // Recursive
				listener);
	} // end constructor

	
	/**
	 * Constructor with a default border and the option to recursively set drop
	 * targets. If your component is a <tt>java.awt.Container</tt>, then each of its
	 * children components will also listen for drops, though only the parent will
	 * change borders.
	 *
	 * @param c         Component on which files will be dropped.
	 * @param recursive Recursively set children as drop targets.
	 * @param listener  Listens for <tt>filesDropped</tt>.
	 * @since 1.0
	 */
	public FileDrop(final Component c, final boolean recursive, final Listener listener) {
		this(	c, // Drop target
				BorderFactory.createMatteBorder(border, border, border, border, defaultBorderColor), // Drag border
				BorderFactory.createMatteBorder(border, border, border, border, rejectBorderColor), // Drag reject border
				recursive, // Recursive
				listener);
	} // end constructor


	/**
	 * Constructor with a specified border
	 *
	 * @param c          Component on which files will be dropped.
	 * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
	 * @param listener   Listens for <tt>filesDropped</tt>.
	 * @since 1.0
	 */
	public FileDrop(final Component c, final Border dragBorder,
			final Border rejectBorder, final Listener listener) {
		this(	c, // Drop target
				dragBorder, // Drag border
				rejectBorder, // Drag reject border
				false, // Recursive
				listener);
	} // end constructor



	/**
	 * Full constructor with a specified border and debugging optionally turned on.
	 * With Debugging turned on, more status messages will be displayed to
	 * <tt>out</tt>. A common way to use this constructor is with
	 * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for the
	 * parameter <tt>out</tt> will result in no debugging output.
	 *
	 * @param out        PrintStream to record debugging info or null for no
	 *                   debugging.
	 * @param c          Component on which files will be dropped.
	 * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
	 * @param recursive  Recursively set children as drop targets.
	 * @param listener   Listens for <tt>filesDropped</tt>.
	 * @since 1.0
	 */
	public FileDrop(final Component c,
			final Border dragBorder, final Border rejectBorder, final boolean recursive,
			final Listener listener) {

		if (supportsDnD()) { // Make a drop listener
			dropListener = new DropTargetListener() {
				
				public void dragEnter(java.awt.dnd.DropTargetDragEvent evt) {
					log("FileDrop: dragEnter event.");

					// Is this an acceptable drag event?
					dropType = isDragOk(evt);
					if (dropType != DropType.DROP_FAIL && dropType != DropType.DROP_NONE && c.isEnabled()) {
						// If it's a Swing component, set its border
						if (c instanceof javax.swing.JComponent) {
							javax.swing.JComponent jc = (javax.swing.JComponent) c;
							if (normalBorder == null) {
								normalBorder = jc.getBorder();
							} // end if: border not yet saved
							log("FileDrop: normal border saved.");
							jc.setBorder(dragBorder);
							log("FileDrop: drag border set.");
						} // end if: JComponent

						// Acknowledge that it's okay to enter
						// evt.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
						evt.acceptDrag(DnDConstants.ACTION_COPY);
						log("FileDrop: event accepted.");
					} // end if: drag ok
					else { // Reject the drag event
						evt.rejectDrag();
						log("FileDrop: event rejected.");

						javax.swing.JComponent jc = (javax.swing.JComponent) c;
						if (normalBorder == null) {
							normalBorder = jc.getBorder();
						} // end if: border not yet saved
						log("FileDrop: normal border saved.");
						jc.setBorder(rejectBorder);
						log("FileDrop: drag border set.");
					} // end else: drag not ok
				} // end dragEnter

				public void dragOver(java.awt.dnd.DropTargetDragEvent evt) { // This is called continually as long as
																				// the mouse is
																				// over the drag target.
				} // end dragOver

				public void drop(java.awt.dnd.DropTargetDropEvent evt) {
					log("FileDrop: drop event.");
					try { // Get whatever was dropped
						Transferable tr = evt.getTransferable();

						switch(dropType) {
						
						default:
						case DROP_FAIL:
						case DROP_NONE:
							break;
							
							
						case DROP_FILELIST:
							acceptDropFilelist(evt, listener);
							break;
							
							
						case DROP_LINUX:
							acceptDropLinux(evt, listener);
							break;

						case DROP_URL:
							acceptDropUrl(evt, listener);
							break;
						
						}//end switch on dropType
						
						

					} // end try
					catch (java.io.IOException io) {
						log("FileDrop: IOException - abort:");
						log(io);
						evt.rejectDrop();
					} // end catch IOException
					catch (java.awt.datatransfer.UnsupportedFlavorException ufe) {
						log("FileDrop: UnsupportedFlavorException - abort:");
						log(ufe);
						evt.rejectDrop();
					} // end catch: UnsupportedFlavorException
					finally {
						// If it's a Swing component, reset its border
						if (c instanceof javax.swing.JComponent) {
							javax.swing.JComponent jc = (javax.swing.JComponent) c;
							jc.setBorder(normalBorder);
							log("FileDrop: normal border restored.");
						} // end if: JComponent
					} // end finally
				} // end drop

				public void dragExit(java.awt.dnd.DropTargetEvent evt) {
					log("FileDrop: dragExit event.");
					dropType = DropType.DROP_NONE;
					// If it's a Swing component, reset its border
					if (c instanceof javax.swing.JComponent) {
						javax.swing.JComponent jc = (javax.swing.JComponent) c;
						jc.setBorder(normalBorder);
						log("FileDrop: normal border restored.");
					} // end if: JComponent
				} // end dragExit

				public void dropActionChanged(java.awt.dnd.DropTargetDragEvent evt) {
					log("FileDrop: dropActionChanged event.");
					// Is this an acceptable drag event?
					dropType = isDragOk(evt);
					if (dropType != DropType.DROP_FAIL && dropType != DropType.DROP_NONE) {
						evt.acceptDrag(DnDConstants.ACTION_COPY);
						log("FileDrop: event accepted.");
					} // end if: drag ok
					else {
						evt.rejectDrag();
						log("FileDrop: event rejected.");
					} // end else: drag not ok
				} // end dropActionChanged
			}; // end DropTargetListener

			// Make the component (and possibly children) drop targets
			makeDropTarget(c, recursive);
		} // end if: supports dnd
		else {
			log("FileDrop: Drag and drop is not supported with this JVM");
		} // end else: does not support DnD
	} // end constructor

	private static boolean supportsDnD() { // Static Boolean
		if (supportsDnD == null) {
			boolean support = false;
			try {
				Class arbitraryDndClass = Class.forName("java.awt.dnd.DnDConstants");
				support = true;
			} // end try
			catch (Exception e) {
				support = false;
			} // end catch
			supportsDnD = new Boolean(support);
		} // end if: first time through
		return supportsDnD.booleanValue();
	} // end supportsDnD

	// BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
	private static String ZERO_CHAR_STRING = "" + (char) 0;

	private static File[] createFileArray(BufferedReader bReader) {
		try {
			List list = new java.util.ArrayList();
			java.lang.String line = null;
			while ((line = bReader.readLine()) != null) {
				try {
					// kde seems to append a 0 char to the end of the reader
					if (ZERO_CHAR_STRING.equals(line))
						continue;

					File file = new File(new URI(line));
					list.add(file);
				} catch (Exception ex) {
					log("Error with " + line + ": " + ex.getMessage());
				}
			}

			return (File[]) list.toArray(new File[list.size()]);
		} catch (IOException ex) {
			log("FileDrop: IOException");
		}
		return new File[0];
	}
	// END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.

	private void makeDropTarget(final Component c, boolean recursive) {
		// Make drop target
		final java.awt.dnd.DropTarget dt = new java.awt.dnd.DropTarget();
		try {
			dt.addDropTargetListener(dropListener);
		} // end try
		catch (java.util.TooManyListenersException e) {
			e.printStackTrace();
			log("FileDrop: Drop will not work due to previous error. Do you have another listener attached?");
		} // end catch

		// Listen for hierarchy changes and remove the drop target when the parent gets
		// cleared out.
		c.addHierarchyListener(new java.awt.event.HierarchyListener() {
			public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
				log("FileDrop: Hierarchy changed.");
				Component parent = c.getParent();
				if (parent == null) {
					c.setDropTarget(null);
					log("FileDrop: Drop target cleared from component.");
				} // end if: null parent
				else {
					new java.awt.dnd.DropTarget(c, dropListener);
					log("FileDrop: Drop target added to component.");
				} // end else: parent not null
			} // end hierarchyChanged
		}); // end hierarchy listener
		if (c.getParent() != null)
			new java.awt.dnd.DropTarget(c, dropListener);

		if (recursive && (c instanceof java.awt.Container)) {
			// Get the container
			java.awt.Container cont = (java.awt.Container) c;

			// Get it's components
			Component[] comps = cont.getComponents();

			// Set it's components as listeners also
			for (int i = 0; i < comps.length; i++)
				makeDropTarget(comps[i], recursive);
		} // end if: recursively set components as listener
	} // end dropListener

	/** Determine if the dragged data is a file list. */
	private DropType isDragOk(final java.awt.dnd.DropTargetDragEvent evt) {

		// Get data flavors being dragged
		java.awt.datatransfer.DataFlavor[] flavors = evt.getCurrentDataFlavors();

		// Show data flavors
		if (flavors.length == 0)
			log("FileDrop: no data flavors.");
		for (DataFlavor f : flavors)
			log("FileDrop: Found DataFlavor " + f.toString());
		
		// See if any of the flavors match
		for (DataFlavor curFlavor : flavors) {
			
			// If it's a file list flavour, accept it
			if (curFlavor.equals(DataFlavor.javaFileListFlavor)) {
				return DropType.DROP_FILELIST;
			}

			// if the mime-type is a uri-list, accept it
			if (curFlavor.getSubType().equals("uri-list") && curFlavor.isRepresentationClassReader()) {
				return DropType.DROP_LINUX;
			}

			// if the String payload is a URL, accept it
			if (isDragUrl(evt)) {
				return DropType.DROP_URL;
			}

		}

		return DropType.DROP_FAIL;


	} // end isDragOk

	private boolean isDragUrl(DropTargetDragEvent evt) {

		DataFlavor flavour = DataFlavor.stringFlavor;
		if (!evt.isDataFlavorSupported(flavour)) {
			return false;
		}

		String data;
		try {
			data = (String) evt.getTransferable().getTransferData(flavour);
		} catch (UnsupportedFlavorException | IOException e) {
			return false;
		}

		if (data == null) {
			return false;
		}
		try {
			URL url = new URL(data);
		} catch (Exception e) {
			return false;
		}
		return true;
	}



	
	private void acceptDropFilelist(DropTargetDropEvent evt, Listener listener) throws UnsupportedFlavorException, IOException {
		Transferable tr = evt.getTransferable();
		// Is it a file list?
		if (tr.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
			// Say we'll take it.
			// evt.acceptDrop ( DnDConstants.ACTION_COPY_OR_MOVE );
			evt.acceptDrop(DnDConstants.ACTION_COPY);
			log("FileDrop: file list accepted.");

			// Get a useful list
			List fileList = (List) tr.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
			java.util.Iterator iterator = fileList.iterator();

			// Convert list to array
			File[] filesTemp = new File[fileList.size()];
			fileList.toArray(filesTemp);
			final File[] files = filesTemp;

			// Alert listener to drop.
			if (listener != null)
				listener.filesDropped(files);

			// Mark that drop is completed.
			evt.getDropTargetContext().dropComplete(true);
			log("FileDrop: drop complete.");
		} // end if: file list
	}
	
	private void acceptDropLinux(DropTargetDropEvent evt, Listener listener) throws UnsupportedFlavorException, IOException {
		Transferable tr = evt.getTransferable();
		// Thanks, Nathan!
		// BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
		DataFlavor[] flavors = tr.getTransferDataFlavors();
		boolean handled = false;
		for (int zz = 0; zz < flavors.length; zz++) {

			if (flavors[zz].equals(DataFlavor.stringFlavor)) {

			}

			if (flavors[zz].isRepresentationClassReader()) {
				// Say we'll take it.
				// evt.acceptDrop ( DnDConstants.ACTION_COPY_OR_MOVE );
				evt.acceptDrop(DnDConstants.ACTION_COPY);
				log("FileDrop: reader accepted.");

				Reader reader = flavors[zz].getReaderForText(tr);

				BufferedReader br = new BufferedReader(reader);

				if (listener != null)
					listener.filesDropped(createFileArray(br));

				// Mark that drop is completed.
				evt.getDropTargetContext().dropComplete(true);
				log("FileDrop: drop complete.");
				handled = true;
				break;
			}
		}
		if (!handled) {
			log("FileDrop: not a file list or reader - abort.");
			evt.rejectDrop();
		}
		// END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
	}
	
	private void acceptDropUrl(DropTargetDropEvent evt, Listener listener) throws UnsupportedFlavorException, IOException {
		evt.acceptDrop(DnDConstants.ACTION_COPY);
		URL url = getDropUrl(evt);
		if (url == null) {
			evt.getDropTargetContext().dropComplete(false);
			return; 
		}
		
		if (listener == null) {
			evt.getDropTargetContext().dropComplete(false);
			return;
		}
		
		listener.urlsDropped(new URL[] { url });
		evt.getDropTargetContext().dropComplete(true);
		
	}
	
	private URL getDropUrl(DropTargetDropEvent evt) {
		
		//make sure we can get the data
		DataFlavor flavour = DataFlavor.stringFlavor;
		if (!evt.isDataFlavorSupported(flavour)) {
			return null;
		}


		String data;
		try {
			//accept the drop to attemt to read it
			evt.acceptDrop(DnDConstants.ACTION_COPY);
			data = (String) evt.getTransferable().getTransferData(flavour);
		} catch (UnsupportedFlavorException | IOException e) {
			evt.getDropTargetContext().dropComplete(false);
			return null;
		}

		if (!(data.startsWith("http://") || data.startsWith("https://"))) {
			return null;
		}
		try {
			URL url = new URL(data);
			evt.getDropTargetContext().dropComplete(true);
			return url;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Downloads the URL to a temp file, preserving file extension. Note that this
	 * is a blocking call, and may take some time for larger files.
	 * 
	 * @param url the URL to downlolad
	 * @return a File representing the downloaded file
	 * @throws IOException
	 */
	public static File getUrlAsFile(URL url) throws IOException {
		return getUrlAsFile(url, null);
	}
	
	public static File getUrlAsFile(URL url, Consumer<Float> progressCallback) throws IOException {
		String[] parts = url.toString().split("\\.");
		String ext = "";
		if (parts.length > 1) {
			ext = "." + parts[parts.length-1];
		}
		String filename = "";
		if (parts.length > 2) {
			String[] urlpathparts = parts[parts.length-2].split("/");
			filename = urlpathparts[urlpathparts.length-1] + "." + ext;
		}
		Path tempdir = Files.createTempDirectory("Peakaboo");
		Path tempfile = tempdir.resolve(filename);
		File file = tempfile.toFile();
		file.deleteOnExit();
		
		int expectedSize = contentLength(url);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		if (progressCallback != null) {
			rbc = new CallbackByteChannel(rbc, expectedSize, progressCallback);
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
		return file;
	}
	
	private static int contentLength(URL url) {
		HttpURLConnection connection;
		int contentLength = -1;
		try {
			connection = (HttpURLConnection) url.openConnection();
			contentLength = connection.getContentLength();
		} catch (Exception e) {
		}
		return contentLength;
	}
	
	private static void log(String message) {
		StratusLog.get().log(Level.FINE, message);
	}

	private static void log(Throwable t) {
		StratusLog.get().log(Level.FINE, t.getMessage(), t);
	}

	
	/**
	 * Removes the drag-and-drop hooks from the component and optionally from the
	 * all children. You should call this if you add and remove components after
	 * you've set up the drag-and-drop. This will recursively unregister all
	 * components contained within <var>c</var> if <var>c</var> is a
	 * {@link java.awt.Container}.
	 *
	 * @param c The component to unregister as a drop target
	 * @since 1.0
	 */
	public static boolean remove(Component c) {
		return remove(null, c, true);
	} // end remove

	/**
	 * Removes the drag-and-drop hooks from the component and optionally from the
	 * all children. You should call this if you add and remove components after
	 * you've set up the drag-and-drop.
	 *
	 * @param out       Optional {@link java.io.PrintStream} for logging drag and
	 *                  drop messages
	 * @param c         The component to unregister
	 * @param recursive Recursively unregister components within a container
	 * @since 1.0
	 */
	public static boolean remove(java.io.PrintStream out, Component c, boolean recursive) { // Make sure we
																										// support dnd.
		if (supportsDnD()) {
			log("FileDrop: Removing drag-and-drop hooks.");
			c.setDropTarget(null);
			if (recursive && (c instanceof java.awt.Container)) {
				Component[] comps = ((java.awt.Container) c).getComponents();
				for (int i = 0; i < comps.length; i++)
					remove(out, comps[i], recursive);
				return true;
			} // end if: recursive
			else
				return false;
		} // end if: supports DnD
		else
			return false;
	} // end remove

	/* ******** I N N E R I N T E R F A C E L I S T E N E R ******** */

	/**
	 * Implement this inner interface to listen for when files are dropped. For
	 * example your class declaration may begin like this: <code><pre>
	 *      public class MyClass implements FileDrop.Listener
	 *      ...
	 *      public void filesDropped( File[] files )
	 *      {
	 *          ...
	 *      }   // end filesDropped
	 *      ...
	 * </pre></code>
	 *
	 * @since 1.1
	 */
	public static interface Listener {

		/**
		 * This method is called when files have been successfully dropped.
		 *
		 * @param files An array of <tt>File</tt>s that were dropped.
		 * @since 1.0
		 */
		public abstract void filesDropped(File[] files);
		
		public abstract void urlsDropped(URL[] urls);

	} // end inner-interface Listener

	/* ******** I N N E R C L A S S ******** */

	/**
	 * This is the event that is passed to the {@link FileDrop.Listener#filesDropped
	 * filesDropped(...)} method in your {@link FileDrop.Listener} when files are
	 * dropped onto a registered drop target.
	 *
	 * <p>
	 * I'm releasing this code into the Public Domain. Enjoy.
	 * </p>
	 * 
	 * @author Robert Harder
	 * @author rob@iharder.net
	 * @version 1.2
	 */
	public static class Event extends java.util.EventObject {

		private File[] files;

		/**
		 * Constructs an {@link Event} with the array of files that were dropped and the
		 * {@link FileDrop} that initiated the event.
		 *
		 * @param files  The array of files that were dropped
		 * @param source The event source
		 * @since 1.1
		 */
		public Event(File[] files, Object source) {
			super(source);
			this.files = files;
		} // end constructor

		/**
		 * Returns an array of files that were dropped on a registered drop target.
		 *
		 * @return array of files that were dropped
		 * @since 1.1
		 */
		public File[] getFiles() {
			return files;
		} // end getFiles

	} // end inner class Event

	/* ******** I N N E R C L A S S ******** */

	/**
	 * At last an easy way to encapsulate your custom objects for dragging and
	 * dropping in your Java programs! When you need to create a
	 * {@link Transferable} object, use this class to wrap
	 * your object. For example:
	 * 
	 * <pre>
	 * <code>
	 *      ...
	 *      MyCoolClass myObj = new MyCoolClass();
	 *      Transferable xfer = new TransferableObject( myObj );
	 *      ...
	 * </code>
	 * </pre>
	 * 
	 * Or if you need to know when the data was actually dropped, like when you're
	 * moving data out of a list, say, you can use the
	 * {@link TransferableObject.Fetcher} inner class to return your object Just in
	 * Time. For example:
	 * 
	 * <pre>
	 * <code>
	 *      ...
	 *      final MyCoolClass myObj = new MyCoolClass();
	 *
	 *      TransferableObject.Fetcher fetcher = new TransferableObject.Fetcher()
	 *      {   public Object getObject(){ return myObj; }
	 *      }; // end fetcher
	 *
	 *      Transferable xfer = new TransferableObject( fetcher );
	 *      ...
	 * </code>
	 * </pre>
	 *
	 * The {@link java.awt.datatransfer.DataFlavor} associated with
	 * {@link TransferableObject} has the representation class
	 * <tt>net.iharder.dnd.TransferableObject.class</tt> and MIME type
	 * <tt>application/x-net.iharder.dnd.TransferableObject</tt>. This data flavor
	 * is accessible via the static {@link #DATA_FLAVOR} property.
	 *
	 *
	 * <p>
	 * I'm releasing this code into the Public Domain. Enjoy.
	 * </p>
	 * 
	 * @author Robert Harder
	 * @author rob@iharder.net
	 * @version 1.2
	 */
	public static class TransferableObject implements Transferable {
		/**
		 * The MIME type for {@link #DATA_FLAVOR} is
		 * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
		 *
		 * @since 1.1
		 */
		public final static String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";

		/**
		 * The default {@link java.awt.datatransfer.DataFlavor} for
		 * {@link TransferableObject} has the representation class
		 * <tt>net.iharder.dnd.TransferableObject.class</tt> and the MIME type
		 * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
		 *
		 * @since 1.1
		 */
		public final static java.awt.datatransfer.DataFlavor DATA_FLAVOR = new java.awt.datatransfer.DataFlavor(
				FileDrop.TransferableObject.class, MIME_TYPE);

		private Fetcher fetcher;
		private Object data;

		private java.awt.datatransfer.DataFlavor customFlavor;

		/**
		 * Creates a new {@link TransferableObject} that wraps <var>data</var>. Along
		 * with the {@link #DATA_FLAVOR} associated with this class, this creates a
		 * custom data flavor with a representation class determined from
		 * <code>data.getClass()</code> and the MIME type
		 * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
		 *
		 * @param data The data to transfer
		 * @since 1.1
		 */
		public TransferableObject(Object data) {
			this.data = data;
			this.customFlavor = new java.awt.datatransfer.DataFlavor(data.getClass(), MIME_TYPE);
		} // end constructor

		/**
		 * Creates a new {@link TransferableObject} that will return the object that is
		 * returned by <var>fetcher</var>. No custom data flavor is set other than the
		 * default {@link #DATA_FLAVOR}.
		 *
		 * @see Fetcher
		 * @param fetcher The {@link Fetcher} that will return the data object
		 * @since 1.1
		 */
		public TransferableObject(Fetcher fetcher) {
			this.fetcher = fetcher;
		} // end constructor

		/**
		 * Creates a new {@link TransferableObject} that will return the object that is
		 * returned by <var>fetcher</var>. Along with the {@link #DATA_FLAVOR}
		 * associated with this class, this creates a custom data flavor with a
		 * representation class <var>dataClass</var> and the MIME type
		 * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
		 *
		 * @see Fetcher
		 * @param dataClass The {@link java.lang.Class} to use in the custom data flavor
		 * @param fetcher   The {@link Fetcher} that will return the data object
		 * @since 1.1
		 */
		public TransferableObject(Class dataClass, Fetcher fetcher) {
			this.fetcher = fetcher;
			this.customFlavor = new java.awt.datatransfer.DataFlavor(dataClass, MIME_TYPE);
		} // end constructor

		/**
		 * Returns the custom {@link java.awt.datatransfer.DataFlavor} associated with
		 * the encapsulated object or <tt>null</tt> if the {@link Fetcher} constructor
		 * was used without passing a {@link java.lang.Class}.
		 *
		 * @return The custom data flavor for the encapsulated object
		 * @since 1.1
		 */
		public java.awt.datatransfer.DataFlavor getCustomDataFlavor() {
			return customFlavor;
		} // end getCustomDataFlavor

		/* ******** T R A N S F E R A B L E M E T H O D S ******** */

		/**
		 * Returns a two- or three-element array containing first the custom data
		 * flavor, if one was created in the constructors, second the default
		 * {@link #DATA_FLAVOR} associated with {@link TransferableObject}, and third
		 * the {@link java.awt.datatransfer.DataFlavor#stringFlavor}.
		 *
		 * @return An array of supported data flavors
		 * @since 1.1
		 */
		public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
			if (customFlavor != null)
				return new java.awt.datatransfer.DataFlavor[] { customFlavor, DATA_FLAVOR,
						java.awt.datatransfer.DataFlavor.stringFlavor }; // end flavors array
			else
				return new java.awt.datatransfer.DataFlavor[] { DATA_FLAVOR,
						java.awt.datatransfer.DataFlavor.stringFlavor }; // end flavors array
		} // end getTransferDataFlavors

		/**
		 * Returns the data encapsulated in this {@link TransferableObject}. If the
		 * {@link Fetcher} constructor was used, then this is when the
		 * {@link Fetcher#getObject getObject()} method will be called. If the requested
		 * data flavor is not supported, then the {@link Fetcher#getObject getObject()}
		 * method will not be called.
		 *
		 * @param flavor The data flavor for the data to return
		 * @return The dropped data
		 * @since 1.1
		 */
		public Object getTransferData(java.awt.datatransfer.DataFlavor flavor)
				throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
			// Native object
			if (flavor.equals(DATA_FLAVOR))
				return fetcher == null ? data : fetcher.getObject();

			// String
			if (flavor.equals(java.awt.datatransfer.DataFlavor.stringFlavor))
				return fetcher == null ? data.toString() : fetcher.getObject().toString();

			// We can't do anything else
			throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
		} // end getTransferData

		/**
		 * Returns <tt>true</tt> if <var>flavor</var> is one of the supported flavors.
		 * Flavors are supported using the <code>equals(...)</code> method.
		 *
		 * @param flavor The data flavor to check
		 * @return Whether or not the flavor is supported
		 * @since 1.1
		 */
		public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor flavor) {
			// Native object
			if (flavor.equals(DATA_FLAVOR))
				return true;

			// String
			if (flavor.equals(java.awt.datatransfer.DataFlavor.stringFlavor))
				return true;

			// We can't do anything else
			return false;
		} // end isDataFlavorSupported

		/* ******** I N N E R I N T E R F A C E F E T C H E R ******** */

		/**
		 * Instead of passing your data directly to the {@link TransferableObject}
		 * constructor, you may want to know exactly when your data was received in case
		 * you need to remove it from its source (or do anyting else to it). When the
		 * {@link #getTransferData getTransferData(...)} method is called on the
		 * {@link TransferableObject}, the {@link Fetcher}'s {@link #getObject
		 * getObject()} method will be called.
		 *
		 * @author Robert Harder
		 * @version 1.1
		 * @since 1.1
		 */
		public static interface Fetcher {
			/**
			 * Return the object being encapsulated in the {@link TransferableObject}.
			 *
			 * @return The dropped object
			 * @since 1.1
			 */
			public abstract Object getObject();
		} // end inner interface Fetcher

	} // end class TransferableObject

	static class CallbackByteChannel implements ReadableByteChannel {
		Consumer<Float> progressCallback;
		long size;
		ReadableByteChannel rbc;
		long sizeRead;

		CallbackByteChannel(ReadableByteChannel rbc, long expectedSize, Consumer<Float> progressCallback) {
			this.progressCallback = progressCallback;
			this.size = expectedSize;
			this.rbc = rbc;
		}

		public void close() throws IOException {
			rbc.close();
		}

		public long getReadSoFar() {
			return sizeRead;
		}

		public boolean isOpen() {
			return rbc.isOpen();
		}

		public int read(ByteBuffer bb) throws IOException {
			int n;
			float progress;
			if ((n = rbc.read(bb)) > 0) {
				sizeRead += n;
				progress = size > 0 ? (float) sizeRead / (float) size : 0f;
				progressCallback.accept(progress);
			}
			return n;
		}
	}
	
} // end class FileDrop
