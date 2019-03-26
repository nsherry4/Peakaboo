package org.peakaboo.framework.cyclops.visualization.palette;

import java.util.List;


public enum Palette
{

		THERMAL {
			@Override
			public int[] getPaletteData()
			{
				return thermal;
			}
		},
		MONOCHROME {
			@Override
			public int[] getPaletteData()
			{
				return monochrome;
			}
		},
		MONOCHROME_INVERTED {
			@Override
			public int[] getPaletteData()
			{
				return inv_monochrome;
			}
		},
		THOUGHTFUL {
			@Override
			public int[] getPaletteData()
			{
				return thoughtful;
			}
		},
		TERRA {
			@Override
			public int[] getPaletteData()
			{
				return terra;
			}
		},
		OLIVE {
			@Override
			public int[] getPaletteData()
			{
				return olive;
			}
		},
		VINTAGE {
			@Override
			public int[] getPaletteData()
			{
				return vintage;
			}
		},
		GOLDFISH {
			@Override
			public int[] getPaletteData()
			{
				return goldfish;
			}
		},
		SUGAR {
			@Override
			public int[] getPaletteData()
			{
				return sugar;
			}
		},
		BROWN_SUGAR {
			@Override
			public int[] getPaletteData()
			{
				return brownsugar;
			}
		},
		BLACKBODY {
			@Override
			public int[] getPaletteData()
			{
				return blackbody;
			}
		},
		GEORGIA {
			@Override
			public int[] getPaletteData()
			{
				return georgia;
			}
		}
		;
		public abstract int[] getPaletteData();

		
		public String toString()
		{
			String name = this.name();
			name = name.replace('_', ' ');
			
			
			
			return name;			
		}
		
		private final static int[] thermal = { 
				0xff10154D, 0,
				0xff0D47A1, 60,
				0xff388E3C, 40,
				0xffFBC02D, 55,
				0xffEF6C00, 40,
				0xffB71C1C, 60
		};
		

		private final static int[] monochrome = {
				0xff000000, 0,
				0xffffffff, 255
		};
		

		private final static int[] inv_monochrome = {
				0xffffffff, 255,
				0xff000000, 0
		};
		
		//nice
		private final static int[] thoughtful = {
				0xffecd078, 0,
				0xffd95b43, 85,
				0xffc02942, 85,
				0xff542437, 85
		};
		
		//nice
		private final static int[] terra = {
				0xff031634, 0,
				0xff033649, 64,
				0xff036564, 64,
				0xffcdb380, 64,
				0xffe8ddcb, 63
		};
		
		//good on unsubtracted, okay on subtracted
		private final static int[] olive = {
				0xff300018, 0,
				0xff5a3d31, 64,
				0xff837b47, 64,
				0xffadb85f, 64,
				0xffe5edb8, 63
		};
		
		private final static int[] vintage = {
				0xff8c2318, 0,
				0xff5e8c6a, 64,
				0xff88a65e, 64,
				0xffbfb35a, 64,
				0xfff2c45a, 63
		};
		
		private final static int[] goldfish = {
				0xff69d2e7, 0,
				0xffa7dbd8, 80,
				0xffe0e4cc, 64,
				0xffd9d6b8, 24,
				0xfff39449, 16,
				0xfffa6900, 48,
				0xffff4000, 31
		};
		
		// based off of "sugar is three"
		private final static int[] sugar = {
				0xff2c8b9a, 0,
				0xff6ac3ae, 64,
				0xffd9c8a7, 48,
				0xfff9cdad, 48,
				0xfffc9d9a, 48,
				0xfffe4365, 32,
				0xfffe264d, 15
		};


		private final static int[] brownsugar = {
				0xff490a3d, 0,
				0xffbd1550, 64,
				0xffe97f02, 64,
				0xfff8ca00, 64,
				0xff8a9b0f, 63
		};


		private final static int[] blackbody = {
				0xfff82600, 0,
				0xffffb12f, 64,
				0xffffe7bf, 64,
				0xffdfe6ff, 64,
				0xffa6baff, 63
		};
		
		private final static int[] georgia = {
				0xffFFFFFF, 0,
				0xffE6B350, 64,
				0xffF27C55, 64,
				0xffD94C58, 64,
				0xff993D6B, 63
		};
		
//		public static void main(String[] args) {
//			
//			int[] spectrum = blackbody;
//			for (int i = 0; i+3 < spectrum.length; i+=4) {
//				PaletteColour c = new PaletteColour(255, spectrum[i], spectrum[i+1], spectrum[i+2]);
//				int steps = spectrum[i+3];
//				System.out.println("0x" + Integer.toHexString(c.getARGB()) + ", " + steps + ",");
//			}
//			
//			
//		}
//		
	
}
