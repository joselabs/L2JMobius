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
package org.l2jmobius.gameserver.util;

/**
 * A class containing useful methods for constructing HTML
 * @author NosBit
 */
public class HtmlUtil
{
	/**
	 * Gets the HTML representation of CP gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getCpGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CH3.br_bar1_cp", "L2UI_CH3.br_bar1_cp", 16, -13);
	}
	
	/**
	 * Gets the HTML representation of HP gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getHpGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CH3.br_bar1_hp", "L2UI_CH3.br_bar1_hp", 16, -13);
	}
	
	/**
	 * Gets the HTML representation of MP Warn gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getMpGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CH3.br_bar1_mp", "L2UI_CH3.br_bar1_mp", 16, -13);
	}
	
	/**
	 * Gets the HTML representation of a gauge.
	 * @param width the width
	 * @param currentValue the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @param backgroundImage the background image
	 * @param image the foreground image
	 * @param imageHeight the image height
	 * @param top the top adjustment
	 * @return the HTML
	 */
	private static String getGauge(int width, long currentValue, long max, boolean displayAsPercentage, String backgroundImage, String image, long imageHeight, long top)
	{
		final long current = Math.min(currentValue, max);
		final StringBuilder sb = new StringBuilder();
		sb.append("<table width=");
		sb.append(width);
		sb.append(" cellpadding=0 cellspacing=0>");
		sb.append("<tr>");
		sb.append("<td background=\"");
		sb.append(backgroundImage);
		sb.append("\">");
		sb.append("<img src=\"");
		sb.append(image);
		sb.append("\" width=");
		sb.append((long) (((double) current / max) * width));
		sb.append(" height=");
		sb.append(imageHeight);
		sb.append(">");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td align=center>");
		sb.append("<table cellpadding=0 cellspacing=");
		sb.append(top);
		sb.append(">");
		sb.append("<tr>");
		sb.append("<td>");
		if (displayAsPercentage)
		{
			sb.append("<table cellpadding=0 cellspacing=2>");
			sb.append("<tr><td>");
			sb.append(String.format("%.2f%%", ((double) current / max) * 100));
			sb.append("</td></tr>");
			sb.append("</table>");
		}
		else
		{
			final int tdWidth = (width - 10) / 2;
			sb.append("<table cellpadding=0 cellspacing=0>");
			sb.append("<tr>");
			sb.append("<td width=");
			sb.append(tdWidth);
			sb.append(" align=right>");
			sb.append(current);
			sb.append("</td>");
			sb.append("<td width=10 align=center>/</td>");
			sb.append("<td width=");
			sb.append(tdWidth);
			sb.append(">");
			sb.append(max);
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
		}
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
}
