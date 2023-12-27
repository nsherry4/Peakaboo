package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormatCompatibility;
import org.peakaboo.dataset.source.model.components.fileformat.SimpleFileFormat;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.model.classinfo.EnumClassInfo;
import org.peakaboo.framework.autodialog.model.style.editors.BooleanStyle;
import org.peakaboo.framework.autodialog.model.style.editors.DropDownStyle;
import org.peakaboo.framework.autodialog.model.style.editors.LabelStyle;
import org.peakaboo.framework.autodialog.model.style.layouts.FramedLayoutStyle;

import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;

public class UniversalHDF5DataSource extends FloatMatrixHDF5DataSource {

	public enum Axis {
		X, Y, Z, None;
	}
	
	private SelectionParameter<String> path;
	private Parameter<String> pathinfo;
	private SelectionParameter<Axis> widthAxis;
	private SelectionParameter<Axis> heightAxis;
	private SelectionParameter<Axis> spectrumAxis;
	private Group pathGroup, axisGroup, topGroup;
	private Parameter<Boolean> optNoDims;
	
	private static final String DS_NAME = "Universal HDF5 Datasource";
	private static final String DS_DESC = "A (nearly) universal HDF5 datasource that allows users to specify how to read the data";
	
	public UniversalHDF5DataSource() {
		super(DS_NAME, DS_DESC);
	}
	
	@Override
	public Optional<DataSize> getDataSize() {
		if (optNoDims.getValue()) {
			return Optional.empty();
		} else {
			return Optional.of(dataSize);
		}
	}
	
	
	private void initialize(List<DataInputAdapter> paths) throws IOException {
		IHDF5SimpleReader mdreader = super.getMetadataReader(paths);
		List<String> datasetPaths = listDatasets(mdreader);
		
		path = new SelectionParameter<String>("Path", new DropDownStyle<String>(), datasetPaths.get(0));
		path.setPossibleValues(datasetPaths);
		pathinfo = new Parameter<String>("Path Information", new LabelStyle(), getPathInfo(mdreader, datasetPaths.get(0)));
		path.getValueHook().addListener(newpath -> {
			pathinfo.setValue(getPathInfo(mdreader, newpath));
			Axis[] axesGuess = matchAxes(mdreader, newpath);
			spectrumAxis.setValue(axesGuess[0]);
			widthAxis.setValue(axesGuess[1]);
			heightAxis.setValue(axesGuess[2]);
		});
		
		Axis[] axesGuess = matchAxes(mdreader, datasetPaths.get(0));
		spectrumAxis = new SelectionParameter<UniversalHDF5DataSource.Axis>("Spectrum Axis", new DropDownStyle<>(), axesGuess[0], new EnumClassInfo<>(Axis.class), this::validator);
		widthAxis = new SelectionParameter<UniversalHDF5DataSource.Axis>("Width Axis", new DropDownStyle<>(), axesGuess[1], new EnumClassInfo<>(Axis.class), this::validator);
		heightAxis = new SelectionParameter<UniversalHDF5DataSource.Axis>("Height Axis", new DropDownStyle<>(), axesGuess[2], new EnumClassInfo<>(Axis.class), this::validator);
		
		spectrumAxis.setPossibleValues(Axis.values());
		widthAxis.setPossibleValues(Axis.values());
		heightAxis.setPossibleValues(Axis.values());
		
		optNoDims = new Parameter<Boolean>("Don't infer map dimensions", new BooleanStyle(), false);
		
		pathGroup = new Group("Dataset Path", Arrays.asList(path, pathinfo), new FramedLayoutStyle());
		axisGroup = new Group("Data Matrix Layout", Arrays.asList(spectrumAxis, widthAxis, heightAxis, optNoDims), new FramedLayoutStyle());
		topGroup = new Group("HDF5 Parameters", pathGroup, axisGroup);
	}
	
	private boolean validator(Parameter<?> param) {
		//if (widthAxis.getValue() == heightAxis.getValue() && widthAxis.getValue() != Axis.None) { return false; }
		//if (widthAxis.getValue() == spectrumAxis.getValue() && widthAxis.getValue() != Axis.None) { return false; }
		//if (heightAxis.getValue() == spectrumAxis.getValue() && widthAxis.getValue() != Axis.None) { return false; }
		if (spectrumAxis.getValue() == Axis.None) { return false; }
		return true;
	}
	
	private String getPathInfo(IHDF5SimpleReader mdreader, String path) {
		HDF5DataSetInformation info = mdreader.getDataSetInformation(path);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Dimensions: ");
		
		int count = 0;
		for (long dim : info.getDimensions()) {
			if (count > 0) { sb.append(", "); }
			
			sb.append(Axis.values()[count]);
			sb.append("=");
			sb.append(dim);
			
			count++;
		}
		
		return sb.toString();
	}
	
	private List<String> listDatasets(IHDF5SimpleReader mdreader) {
		List<String> datasets = listDatasets(mdreader, "/");
		
		Comparator<String> scorer = (String o1, String o2) -> {
			float s1 = scoreDataset(mdreader, o1);
			float s2 = scoreDataset(mdreader, o2);
			//backwards comparison to higher scores are first
			return Float.compare(s2, s1);
		};
		datasets.sort(scorer);
		return datasets;
		
	}
	
	private List<String> listDatasets(IHDF5SimpleReader mdreader, String path) {
		if (!mdreader.isGroup(path)) {
			//remove trailing slash from path, as this is not a group (folder)
			path = path.substring(0, path.length()-1);
			//leaf nodes return themselves if they're a good candidate for containing data
			if (scoreDataset(mdreader, path) < 0.2f) {
				return Collections.emptyList();
			} else {
				return Collections.singletonList(path);
			}
		} else {
			//non-leaf nodes return all child leaves
			List<String> leaves = new ArrayList<>();
			for (String subpath : mdreader.getGroupMembers(path)) {
				String fullpath = path + subpath + "/";
				leaves.addAll(listDatasets(mdreader, fullpath));
			}
			return leaves;
		}
		
	}
	
	private Axis[] matchAxes(IHDF5SimpleReader mdreader, String path) {
		HDF5DataSetInformation info = mdreader.getDataSetInformation(path);
		List<Axis> axes = new ArrayList<>();
		
		//best guess for spectrum axis goes first
		Axis spectrum = bestSpectrumAxis(mdreader, path);
		axes.add(spectrum);
		
		for (Axis a : Axis.values()) {
			if (axes.size() >= 3) { break; }
			if (axes.size() >= info.getRank()) { axes.add(Axis.None); }
			//skip axes already in the list
			if (axes.contains(a)) { continue; }
			axes.add(a);
		}
		
		return axes.toArray(new Axis[] {});
	}
	
	//scores each axis as the potential spectrum axis, returns the best 
	private Axis bestSpectrumAxis(IHDF5SimpleReader mdreader, String path) {
		List<Axis> axes = new ArrayList<>(Arrays.asList(Axis.values()));
		Comparator<Axis> scorer = (Axis a1, Axis a2) -> {
			float s1 = scoreSpectrumAxis(mdreader, path, a1.ordinal());
			float s2 = scoreSpectrumAxis(mdreader, path, a2.ordinal());
			//backwards comparison to higher scores are first
			return Float.compare(s2, s1);
		};
		axes.sort(scorer);
		return axes.get(0);
	}
	
	//scores an axis based on the likelihood that it is the spectrum axis 
	private float scoreSpectrumAxis(IHDF5SimpleReader mdreader, String path, int axis) {
		HDF5DataSetInformation info = mdreader.getDataSetInformation(path);
		float score = 1f;
		
		
		long[] dims = info.getDimensions();
		if (axis >= info.getRank()) { return 0f; }
		
		//penalty for not being a power of 2
		long dim = dims[axis];
		if (!isPowerOfTwo(dim)) {
			score *= 0.1f;
		}
		
		
		
		// view on wolfram alpha with
		// f(x) = ((ln(x)/ln(2))^12/2^41) * exp(-(x-2048)^2/(2*2048^2)) from -1024 to 8192
		float power = 0;
		power = (float) (Math.log(dim)/Math.log(2));			
		power = (float) Math.pow(power, 12f); //scaling factor
		power /= Math.pow(2, 42); //gets dim=2048 back down to around 1
		
		float gaussian = 0f;
		gaussian = (float) Math.exp(  -Math.pow(dim-2048, 2)  /  (2*Math.pow(2048, 2))  );
		
		score *= power * gaussian;

		return score;
	}
	
	private float scoreDataset(IHDF5SimpleReader mdreader, String path) {
		HDF5DataSetInformation info = mdreader.getDataSetInformation(path);
		float score = 1f;
		
		//Does this dataset contain at least one axis that is a power of two and largish?
		boolean hasPowerOfTwo = false;
		for (long dim : info.getDimensions()) {
			if (dim > 128) { hasPowerOfTwo |= isPowerOfTwo(dim); }
		}
		if (!hasPowerOfTwo) {
			score *= 0.5f;
		}
		
		
		//3 axes are more likely than two, more than one
		int dims = info.getRank();
		if (dims == 0 || dims > 3) {
			score = 0;
		} else if (dims == 1) {
			score *= 0.25;
		} else if (dims == 2) {
			score *= 0.5;
		} else {
			//exactly 3, no downward adjustment
		}
		
		//must have more than 255 total elements
		if (info.getNumberOfElements() <= 256) {
			score *= 0.1f;
		}
		
		return score;
		
	}
	
	@Override
	public FileFormat getFileFormat() {
		return new SimpleFileFormat(false, DS_NAME, DS_DESC, new String[] {"h5", "hdf5"}) {
			@Override
			public FileFormatCompatibility compatibility(List<DataInputAdapter> filenames) {
				FileFormatCompatibility fromSuper = super.compatibility(filenames);
				if (fromSuper == FileFormatCompatibility.NO) { return fromSuper; }
				
				try {
					//extra tests to reject HDF5 files that don't contain any promising values
					IHDF5SimpleReader mdreader = UniversalHDF5DataSource.super.getMetadataReader(filenames);
					List<String> datasetPaths = listDatasets(mdreader);
					if (datasetPaths.isEmpty()) { return FileFormatCompatibility.NO; }
				} catch (Exception e) {
					return FileFormatCompatibility.NO;
				}
				
				return fromSuper;
			}
		};
	}
	
	@Override
	public Optional<Group> getParameters(List<DataInputAdapter> paths) throws DataSourceReadException, IOException {
		initialize(paths);
		return Optional.of(topGroup);
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "3ca671cf-6bfd-4372-9aae-d36b3a19d476";
	}

	
	//During `read`, this will be called to get the final list of HDF5 paths (not files) to read
	protected List<String> getDataPaths(List<DataInputAdapter> paths) {
		return Collections.singletonList(path.getValue());
	}
	
	protected String getAxisOrder() {
		String[] order = new String[] {"", "", ""};
		Axis axis;
		List<Axis> axes = Arrays.asList(Axis.values());

		StringBuilder sb = new StringBuilder();
	
		axis = widthAxis.getValue();
		if (axis != Axis.None) {
			order[axes.indexOf(axis)] = "x";
		}
		
		axis = heightAxis.getValue();
		if (axis != Axis.None) {
			order[axes.indexOf(axis)] = "y";
		}
		
		axis = spectrumAxis.getValue();
		if (axis != Axis.None) {
			order[axes.indexOf(axis)] = "z";
		}
		
		
		for (String o : order) {
			sb.append(o);
		}
		return sb.toString();
		
	}

	
	private static boolean isPowerOfTwo(long value) {
		return (value & value-1) == 0;
	}
	
}
