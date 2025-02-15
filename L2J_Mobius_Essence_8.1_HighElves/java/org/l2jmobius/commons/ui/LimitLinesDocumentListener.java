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
package org.l2jmobius.commons.ui;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

/*
 *  A class to control the maximum number of lines to be stored in a Document
 *
 *  Excess lines can be removed from the start or end of the Document
 *  depending on your requirement.
 *
 *  a) if you append text to the Document, then you would want to remove lines
 *     from the start.
 *  b) if you insert text at the beginning of the Document, then you would
 *     want to remove lines from the end.
 */
public class LimitLinesDocumentListener implements DocumentListener
{
	private int _maximumLines;
	private final boolean _isRemoveFromStart;
	
	/*
	 * Specify the number of lines to be stored in the Document. Extra lines will be removed from the start of the Document.
	 */
	public LimitLinesDocumentListener(int maximumLines)
	{
		this(maximumLines, true);
	}
	
	/*
	 * Specify the number of lines to be stored in the Document. Extra lines will be removed from the start or end of the Document, depending on the boolean value specified.
	 */
	public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart)
	{
		setLimitLines(maximumLines);
		_isRemoveFromStart = isRemoveFromStart;
	}
	
	/*
	 * Return the maximum number of lines to be stored in the Document.
	 */
	public int getLimitLines()
	{
		return _maximumLines;
	}
	
	/*
	 * Set the maximum number of lines to be stored in the Document.
	 */
	public void setLimitLines(int maximumLines)
	{
		if (maximumLines < 1)
		{
			final String message = "Maximum lines must be greater than 0";
			throw new IllegalArgumentException(message);
		}
		
		_maximumLines = maximumLines;
	}
	
	/*
	 * Handle insertion of new text into the Document.
	 */
	@Override
	public void insertUpdate(DocumentEvent e)
	{
		// Changes to the Document can not be done within the listener so we need to add the processing to the end of the EDT.
		SwingUtilities.invokeLater(() -> removeLines(e));
	}
	
	@Override
	public void removeUpdate(DocumentEvent e)
	{
		// Ignore.
	}
	
	@Override
	public void changedUpdate(DocumentEvent e)
	{
		// Ignore.
	}
	
	/*
	 * Remove lines from the Document when necessary.
	 */
	private void removeLines(DocumentEvent e)
	{
		// The root Element of the Document will tell us the total number of line in the Document.
		final Document document = e.getDocument();
		final Element root = document.getDefaultRootElement();
		
		while (root.getElementCount() > _maximumLines)
		{
			if (_isRemoveFromStart)
			{
				removeFromStart(document, root);
			}
			else
			{
				removeFromEnd(document, root);
			}
		}
	}
	
	/*
	 * Remove lines from the start of the Document
	 */
	private void removeFromStart(Document document, Element root)
	{
		final Element line = root.getElement(0);
		final int end = line.getEndOffset();
		
		try
		{
			document.remove(0, end);
		}
		catch (BadLocationException ble)
		{
			System.out.println(ble);
		}
	}
	
	/*
	 * Remove lines from the end of the Document
	 */
	private void removeFromEnd(Document document, Element root)
	{
		// We use start minus 1 to make sure we remove the newline character of the previous line.
		
		final Element line = root.getElement(root.getElementCount() - 1);
		final int start = line.getStartOffset();
		final int end = line.getEndOffset();
		
		try
		{
			document.remove(start - 1, end - start);
		}
		catch (BadLocationException ble)
		{
			System.out.println(ble);
		}
	}
}