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
package org.l2jmobius.commons.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPSubnet
{
	private final byte[] _addr;
	private final byte[] _mask;
	private final boolean _isIPv4;
	
	public IPSubnet(String input) throws UnknownHostException
	{
		final int idx = input.indexOf('/');
		if (idx > 0)
		{
			_addr = InetAddress.getByName(input.substring(0, idx)).getAddress();
			_mask = getMask(Integer.parseInt(input.substring(idx + 1)), _addr.length);
			_isIPv4 = _addr.length == 4;
			if (!applyMask(_addr))
			{
				throw new UnknownHostException(input);
			}
		}
		else
		{
			_addr = InetAddress.getByName(input).getAddress();
			_mask = getMask(_addr.length * 8, _addr.length); // host, no need to check mask
			_isIPv4 = _addr.length == 4;
		}
	}
	
	public byte[] getAddress()
	{
		return _addr;
	}
	
	private boolean applyMask(byte[] addr)
	{
		// V4 vs V4 or V6 vs V6 checks
		if (_isIPv4 == (addr.length == 4))
		{
			for (int i = 0; i < _addr.length; i++)
			{
				if ((addr[i] & _mask[i]) != _addr[i])
				{
					return false;
				}
			}
		}
		else
		{
			// check for embedded v4 in v6 addr (not done !)
			if (_isIPv4)
			{
				// my V4 vs V6
				for (int i = 0; i < _addr.length; i++)
				{
					if ((addr[i + 12] & _mask[i]) != _addr[i])
					{
						return false;
					}
				}
			}
			else
			{
				// my V6 vs V4
				for (int i = 0; i < _addr.length; i++)
				{
					if ((addr[i] & _mask[i + 12]) != _addr[i + 12])
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public String toString()
	{
		int size = 0;
		for (byte element : _mask)
		{
			size += Integer.bitCount(element & 0xFF);
		}
		
		try
		{
			return InetAddress.getByAddress(_addr) + "/" + size;
		}
		catch (UnknownHostException e)
		{
			return "Invalid";
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o instanceof IPSubnet)
		{
			return applyMask(((IPSubnet) o).getAddress());
		}
		else if (o instanceof InetAddress)
		{
			return applyMask(((InetAddress) o).getAddress());
		}
		return false;
	}
	
	private static byte[] getMask(int n, int maxLength) throws UnknownHostException
	{
		if ((n > (maxLength << 3)) || (n < 0))
		{
			throw new UnknownHostException("Invalid netmask: " + n);
		}
		
		final byte[] result = new byte[maxLength];
		for (int i = 0; i < maxLength; i++)
		{
			result[i] = (byte) 0xFF;
		}
		
		for (int i = (maxLength << 3) - 1; i >= n; i--)
		{
			result[i >> 3] = (byte) (result[i >> 3] << 1);
		}
		
		return result;
	}
}