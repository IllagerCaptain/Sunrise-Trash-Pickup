#region UICode
DoubleSliderControl Amount1 = 210.0 / 208.0;
DoubleSliderControl Amount2 = 239.0 / 209.0;
DoubleSliderControl Amount3 = 236.0 / 193.0;
#endregion

void Render(Surface dst, Surface src, Rectangle rect) {
	ColorBgra currentPixel;
	for (int y = rect.Top; y < rect.Bottom; y++)
	{
		if (IsCancelRequested) return;
		for (int x = rect.Left; x < rect.Right; x++)
		{
			currentPixel = src[x, y];
			dst[x, y] = ColorBgra.FromBgraClamped(
				(float)(currentPixel.B * Amount3),
				(float)(currentPixel.G * Amount2),
				(float)(currentPixel.R * Amount1),
				currentPixel.A
			);
		}
	}
}