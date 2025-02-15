/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.l2jmobius.gameserver.model.captcha;

/**
 * @author JoeAlisson
 */
public class TextureBlock
{
	private final ARGB[] _colors = new ARGB[16];
	private final ARGB[] _palette = new ARGB[4];
	private int _minColorIndex;
	private int _maxColorIndex;
	private short _minColor;
	private short _maxColor;
	
	public TextureBlock()
	{
		for (int i = 0; i < 16; i++)
		{
			_colors[i] = new ARGB();
		}
		
		for (int i = 0; i < 4; i++)
		{
			_palette[i] = new ARGB();
		}
	}
	
	public void of(int[] buffer)
	{
		int maxDistance = -1;
		
		for (int i = 0; i < 16; i++)
		{
			_colors[i].a = (0xFF & (buffer[i] >> 24));
			_colors[i].r = (0xFF & (buffer[i] >> 16));
			_colors[i].g = (0xFF & (buffer[i] >> 8));
			_colors[i].b = (0xFF & (buffer[i]));
			
			for (int j = i - 1; j >= 0; j--)
			{
				final int distance = euclidianDistance(_colors[i], _colors[j]);
				if (distance > maxDistance)
				{
					maxDistance = distance;
					_minColorIndex = j;
					_maxColorIndex = i;
				}
			}
		}
		
		computMinMaxColor();
		computePalette();
	}
	
	private void computePalette()
	{
		_palette[0] = colorAt(_maxColorIndex);
		_palette[1] = colorAt(_minColorIndex);
		
		_palette[2].a = 255;
		_palette[2].r = ((2 * _palette[0].r) + _palette[1].r) / 3;
		_palette[2].g = ((2 * _palette[0].g) + _palette[1].g) / 3;
		_palette[2].b = ((2 * _palette[0].b) + _palette[1].b) / 3;
		
		_palette[3].a = 255;
		_palette[3].r = ((2 * _palette[1].r) + _palette[0].r) / 3;
		_palette[3].g = ((2 * _palette[1].g) + _palette[0].g) / 3;
		_palette[3].b = ((2 * _palette[1].b) + _palette[0].b) / 3;
	}
	
	private void computMinMaxColor()
	{
		_maxColor = _colors[_maxColorIndex].toShortRGB565();
		_minColor = _colors[_minColorIndex].toShortRGB565();
		
		if (_maxColor < _minColor)
		{
			final short tmp = _maxColor;
			_maxColor = _minColor;
			_minColor = tmp;
			
			final int tmp2 = _maxColorIndex;
			_maxColorIndex = _minColorIndex;
			_minColorIndex = tmp2;
		}
	}
	
	private int euclidianDistance(ARGB c1, ARGB c2)
	{
		return ((c1.r - c2.r) * (c1.r - c2.r)) + ((c1.g - c2.g) * (c1.g - c2.g)) + ((c1.b - c2.b) * (c1.b - c2.b));
	}
	
	public short getMaxColor()
	{
		return _maxColor;
	}
	
	public short getMinColor()
	{
		return _minColor;
	}
	
	public ARGB colorAt(int index)
	{
		return _colors[index];
	}
	
	public ARGB[] getPalette()
	{
		return _palette;
	}
	
	public static class ARGB
	{
		public int a;
		public int r;
		public int g;
		public int b;
		
		public short toShortRGB565()
		{
			return (short) (((0xF8 & r) << 8) | ((0xFC & g) << 3) | (b >> 3));
		}
	}
}
