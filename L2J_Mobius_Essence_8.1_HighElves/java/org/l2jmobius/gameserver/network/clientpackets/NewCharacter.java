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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.gameserver.data.xml.PlayerTemplateData;
import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.network.serverpackets.NewCharacterSuccess;

/**
 * @author Mobius
 */
public class NewCharacter extends ClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final NewCharacterSuccess ct = new NewCharacterSuccess();
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.FIGHTER)); // Human Figther
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.MAGE)); // Human Mystic
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ELVEN_FIGHTER)); // Elven Fighter
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ELVEN_MAGE)); // Elven Mystic
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.DARK_FIGHTER)); // Dark Fighter
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.DARK_MAGE)); // Dark Mystic
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ORC_FIGHTER)); // Orc Fighter
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ORC_MAGE)); // Orc Mystic
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.DWARVEN_FIGHTER)); // Dwarf Fighter
		ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.KAMAEL_SOLDIER)); // Kamael Soldier
		getClient().sendPacket(ct);
	}
}
