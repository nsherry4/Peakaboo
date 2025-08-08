# Peakaboo
XRF Visualization Software

[![Java CI with Maven](https://github.com/nsherry4/Peakaboo/actions/workflows/maven.yml/badge.svg)](https://github.com/nsherry4/Peakaboo/actions/workflows/maven.yml)

Peakaboo allows users to identify the spectral origins of the XRF spectrum using a technique that fits all components of the K, L, or M spectrum including escape peaks and pileup peaks, and then plots their spatial intensity distributions as maps.

![Peakaboo](https://raw.githubusercontent.com/nsherry4/Peakaboo/master/Documentation/github/screenshot.png)

[Downloads for Windows, Mac, Linux](https://peakaboo.org/download)

[Learn how to use Peakaboo on our Wiki](https://github.com/nsherry4/Peakaboo/wiki)

[Ask a question in our Discussions](https://github.com/nsherry4/Peakaboo/discussions)

## Peak Fitting

The spectra produced are fitted with several K, L, or M lines for each element. The line positions and relative intensities for each line series were taken from several tabulated sources, including [Xraylib](https://github.com/tschoonj/xraylib). For fitting of the spectral peaks, a Pseudo-Voigt function is used by default. Thus, the identification of a particular element involves a close fit of multiple lines in the spectrum, each with its own shape.

### Noise Reduction 

Noise reduction is essential for spectra taken at very brief intervals while the sample is scanned in the X-Ray beam. The software provides a number of mathematical filters that are used in noise reduction or in background attenuation or removal. Noise filters include moving average, fast Fourier transform (FFT) low pass, Savitsky-Golay, and others.

Where applicable, noise removal filter parameters can be adjusted to suit the data in question. Best noise reduction with minimum change in peak shape is often achieved using a Savitsky-Golay filter.

### Background Removal 

Background removal or reduction is particularly important for spectra acquired using white radiation. Peakaboo has a number of background removal filters of varying sophistication and performance. Varying levels of background can be removed.

## Results Mapping

Once peak fitting is complete, one or two dimentional data sets can be mapped to show the distribution of the selected elements. Peakaboo can show individual elements, composites of several elements, ratios of sets of elements, overlays of up to four sets of elements, as well as two dimensional histograms to compare element concentrations.

## User Extendable 

Peakaboo allows users who are comfortable with Java programming to extend Peakaboo in three ways:
- Filters
- Data Sources
- Data Sinks

To get started creating your own filters or file format support, all you need is the Peakaboo JAR to build against. For more information on creating plugins, see the user manual.

# Library and Plugins

Peakaboo can also be used as a library. This is also the easiest target to build custom plugins against. [Read more about Peakaboo as a library without the GUI](Documentation/github/LibPeakaboo.md).


# Building

Peakaboo builds and manages its dependencies with maven. It also has a dependency on [xraylib](https://github.com/tschoonj/xraylib). At the time of writing, xraylib was not available in maven, so a prebuilt version is included in a local maven repository. To build Peakaboo, run `mvn package` and it will produce a runnable jar file you can invoke with the `java -jar <JARNAME>` command.
