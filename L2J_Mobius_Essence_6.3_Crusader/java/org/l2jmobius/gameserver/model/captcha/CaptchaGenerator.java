/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.model.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.util.DXT1ImageCompressor;

/**
 * @author JoeAlisson
 */
public class CaptchaGenerator
{
	private static final Map<Integer, Captcha> CAPTCHAS = new ConcurrentHashMap<>();
	private static final DXT1ImageCompressor COMPRESSOR = new DXT1ImageCompressor();
	
	private CaptchaGenerator()
	{
	}
	
	public Captcha next()
	{
		final int id = Rnd.get(CAPTCHAS.size() + 5);
		return CAPTCHAS.computeIfAbsent(id, this::generateCaptcha);
	}
	
	public Captcha next(int previousId)
	{
		int id = Rnd.get(CAPTCHAS.size() + 5);
		if (id == previousId)
		{
			id++;
		}
		return CAPTCHAS.computeIfAbsent(id, this::generateCaptcha);
	}
	
	private int generateCaptchaCode()
	{
		return Rnd.get(111111, 999999);
	}
	
	private Captcha generateCaptcha(int id)
	{
		final int height = 32;
		final int width = 128;
		
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D graphics = createGraphics(height, width, image);
		graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
		
		final int code = generateCaptchaCode();
		
		writeCode(code, graphics);
		addNoise(graphics);
		graphics.dispose();
		return new Captcha(id, code, COMPRESSOR.compress(image));
	}
	
	private Graphics2D createGraphics(int height, int width, BufferedImage image)
	{
		final Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, width, height);
		return graphics;
	}
	
	private void writeCode(int code, Graphics2D graphics)
	{
		final String text = String.valueOf(code);
		final FontMetrics metrics = graphics.getFontMetrics();
		
		final int textStart = 10;
		for (int i = 0; i < text.length(); i++)
		{
			final char character = text.charAt(i);
			final int charWidth = metrics.charWidth(character) + 5;
			graphics.setColor(getColor());
			graphics.drawString("" + character, textStart + (i * charWidth), Rnd.get(24, 32));
		}
	}
	
	private void addNoise(Graphics2D graphics)
	{
		graphics.setColor(Color.WHITE);
		for (int i = 0; i < 20; i++)
		{
			graphics.fillOval(Rnd.get(10, 122), Rnd.get(6, 20), 4, 4);
		}
		
		for (int i = 0; i < 6; i++)
		{
			graphics.drawLine(Rnd.get(30, 90), Rnd.get(6, 28), Rnd.get(80, 120), Rnd.get(10, 26));
		}
	}
	
	private Color getColor()
	{
		switch (Rnd.get(5))
		{
			case 1:
			{
				return Color.WHITE;
			}
			case 2:
			{
				return Color.RED;
			}
			case 3:
			{
				return Color.YELLOW;
			}
			case 4:
			{
				return Color.CYAN;
			}
			default:
			{
				return Color.GREEN;
			}
		}
	}
	
	public static CaptchaGenerator getInstance()
	{
		return Singleton.INSTANCE;
	}
	
	private static class Singleton
	{
		private static final CaptchaGenerator INSTANCE = new CaptchaGenerator();
	}
}
