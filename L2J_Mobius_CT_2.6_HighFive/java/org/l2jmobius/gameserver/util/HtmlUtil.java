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
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_CP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_CP_Center", 17, -13);
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
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HP_Center", 17, -13);
	}
	
	/**
	 * Gets the HTML representation of HP Warn gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getHpWarnGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HPWarn_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HPWarn_Center", 17, -13);
	}
	
	/**
	 * Gets the HTML representation of HP Fill gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getHpFillGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HPFill_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HPFill_Center", 17, -13);
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
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_MP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_MP_Center", 17, -13);
	}
	
	/**
	 * Gets the HTML representation of EXP Warn gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getExpGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_EXP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_EXP_Center", 17, -13);
	}
	
	/**
	 * Gets the HTML representation of Food gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getFoodGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_Food_Bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_Food_Center", 17, -13);
	}
	
	/**
	 * Gets the HTML representation of Weight gauge automatically changing level depending on current/max.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getWeightGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getWeightGauge(width, current, max, displayAsPercentage, Util.map(current, 0, max, 1, 5));
	}
	
	/**
	 * Gets the HTML representation of Weight gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @param level a number from 1 to 5 for the 5 different colors of weight gauge
	 * @return the HTML
	 */
	public static String getWeightGauge(int width, long current, long max, boolean displayAsPercentage, long level)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_Weight_bg_Center" + level, "L2UI_CT1.Gauges.Gauge_DF_Large_Weight_Center" + level, 17, -13);
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
