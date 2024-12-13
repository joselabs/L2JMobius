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
package org.l2jmobius.gameserver.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Audits Game Master's actions.
 */
public class GMAudit
{
	private static final Logger LOGGER = Logger.getLogger(GMAudit.class.getName());
	static
	{
		new File("log/GMAudit").mkdirs();
	}
	
	/**
	 * Logs a Game Master's action into a file.
	 * @param gmName the Game Master's name
	 * @param action the performed action
	 * @param target the target's name
	 * @param params the parameters
	 */
	public static void auditGMAction(String gmName, String action, String target, String params)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
		final String date = formatter.format(new Date());
		String name = org.l2jmobius.commons.util.CommonUtil.replaceIllegalCharacters(gmName);
		if (!org.l2jmobius.commons.util.CommonUtil.isValidFileName(name))
		{
			name = "INVALID_GM_NAME_" + date;
		}
		
		final File file = new File("log/GMAudit/" + name + ".txt");
		try (FileWriter save = new FileWriter(file, true))
		{
			save.write(date + ">" + gmName + ">" + action + ">" + target + ">" + params + System.lineSeparator());
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "GMAudit for GM " + gmName + " could not be saved: ", e);
		}
	}
	
	/**
	 * Wrapper method.
	 * @param gmName the Game Master's name
	 * @param action the performed action
	 * @param target the target's name
	 */
	public static void auditGMAction(String gmName, String action, String target)
	{
		auditGMAction(gmName, action, target, "");
	}
}