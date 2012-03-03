package peakaboo.datasource.plugin.plugins.sciencestudio.vespers.data.converter.factory;

import peakaboo.datasource.plugin.plugins.sciencestudio.vespers.data.converter.MapXYVespersToPDSConverter;
import ca.sciencestudio.data.converter.Converter;
import ca.sciencestudio.data.converter.ConverterMap;
import ca.sciencestudio.data.support.ConverterFactoryException;
import ca.sciencestudio.vespers.data.converter.factory.AbstractMapXYVespersConverterFactory;

/**
 * @author maxweld
 *
 */
public class MapXYVespersToPDSConverterFactory extends AbstractMapXYVespersConverterFactory {

	private static final String SUPPORTED_FROM_FORMAT = "DAF";
	private static final String SUPPORTED_TO_FORMAT = "PDS";
	
	@Override
	public Converter getConverter(ConverterMap request) throws ConverterFactoryException {
		
		ConverterMap validRequest = validateRequest(request);
		boolean forceUpdate = validRequest.isForceUpdate();
		String fromFormat = validRequest.getFromFormat();
		String toFormat = validRequest.getToFormat();
		
		MapXYVespersToPDSConverter converter = new MapXYVespersToPDSConverter(fromFormat, toFormat, forceUpdate);
		prepareConverter(converter, validRequest);
		return converter;
	}

	@Override
	protected ConverterMap validateRequest(ConverterMap request) throws ConverterFactoryException {
		
		if(!SUPPORTED_FROM_FORMAT.equals(request.getFromFormat())) {
			throw new ConverterFactoryException("Convert FROM format, " + request.getFromFormat() + ", not supported.");
		}
		
		if(!SUPPORTED_TO_FORMAT.equals(request.getToFormat())) {
			throw new ConverterFactoryException("Convert TO format, " + request.getToFormat() + ", not supported.");
		}
		
		return super.validateRequest(request);
	}
}
