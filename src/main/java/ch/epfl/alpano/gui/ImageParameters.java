package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Preconditions.checkArgument;

record ImageParameters(int width, int height, int superSamplingExponent) {

	public ImageParameters {
		checkArgument(width > 0, "width must be strictly positive");
		checkArgument(height > 0, "height must be strictly positive");
		checkArgument(superSamplingExponent >= 0, "superSamplingExponent must be positive");
	}
}
