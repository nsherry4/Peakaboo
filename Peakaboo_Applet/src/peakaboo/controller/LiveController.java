package peakaboo.controller;



import fava.Fn;
import fava.signatures.FnMap;
import peakaboo.controller.plotter.PlotController;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.dataset.provider.implementations.OnDemandDataSetProvider;
import peakaboo.fileio.implementations.LiveDataSource;
import scitypes.Spectrum;



public class LiveController
{

	PlotController	controller;

	LiveDataSource	ds;
	DataSetProvider	dsp;


	public LiveController(PlotController c)
	{
		controller = c;

		ds = new LiveDataSource();
		dsp = new OnDemandDataSetProvider(ds);

		c.dataController.setDataSetProvider(dsp);

	}


	public void addScan(int index, String scanString)
	{


		Spectrum scan = new Spectrum(Fn.map(scanString.split(" "), new FnMap<String, Float>() {

			public Float f(String s)
			{
				return Float.parseFloat(s);
			}
		}));

		
		
		ds.setScan(index, scan);

		dsp = new OnDemandDataSetProvider(ds);
		 	
		controller.dataController.setDataSetProvider(dsp);

	}

}
