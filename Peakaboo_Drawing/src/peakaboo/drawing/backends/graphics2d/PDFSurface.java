package peakaboo.drawing.backends.graphics2d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

import peakaboo.drawing.backends.SaveableSurface;


class PDFSurface extends SVGSurface implements SaveableSurface
{

	public PDFSurface(SVGGraphics2D g)
	{
		super(g);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void write(OutputStream out) throws IOException
	{

		PDFTranscoder t = new PDFTranscoder();

		// Transcoder: write svg file to bytearray output stream, create input stream
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		super.write(bos);
		ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());

		// Transcoder Input
		InputStreamReader reader = new InputStreamReader(bin);
		TranscoderInput input = new TranscoderInput(reader);

		// Transcoder Output
		TranscoderOutput output = new TranscoderOutput(out);
		
		try {
			t.transcode(input, output);
		} catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
