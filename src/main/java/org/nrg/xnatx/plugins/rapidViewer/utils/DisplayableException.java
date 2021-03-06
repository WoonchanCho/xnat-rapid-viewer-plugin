/*********************************************************************
 * Copyright (c) 2017, Institute of Cancer Research
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *
 * (3) Neither the name of the Institute of Cancer Research nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************/
package org.nrg.xnatx.plugins.rapidViewer.utils;

import java.io.PrintStream;

/**
 * Abstract base class for exceptions that implements Displayable and provides
 * a default implementation.
 * @author jamesd
 */
public abstract class DisplayableException extends Exception
	implements Displayable
{
	/**
	 * Creates a new instance of
	 * <code>DisplayableException</code> without detail message.
	 */
	public DisplayableException()
	{
	}

	/**
	 * Constructs an instance of
	 * <code>DisplayableException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public DisplayableException(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs an instance of
	 * <code>DisplayableException</code> with the specified detail message and
	 * cause.
	 *
	 * @param msg the detail message.
	 * @param cause the cause.
	 */
	public DisplayableException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	/**
	 * Constructs an instance of
	 * <code>DisplayableException</code> with the specified cause.
	 *
	 * @param cause the cause.
	 */
	public DisplayableException(Throwable cause)
	{
		super(cause);
	}

	@Override
	public void display()
	{
		display(System.out, "", false);
	}

	@Override
	public void display(boolean recurse)
	{
		display(System.out, "", recurse);
	}

	@Override
	public void display(String indent)
	{
		display(System.out, indent, false);
	}
	
	@Override
	public void display(String indent, boolean recurse)
	{
		display(System.out, indent, recurse);
	}

	@Override
	public void display(PrintStream ps)
	{
		display(ps, "", false);
	}

	@Override
	public void display(PrintStream ps, boolean recurse)
	{
		display(ps, "", recurse);
	}

	@Override
	public void display(PrintStream ps, String indent)
	{
		display(ps, indent, false);
	}

	@Override
	public void display(PrintStream ps, String indent, boolean recurse)
	{
		ps.println(indent+getClass().getName());
		String pad = indent+"  * ";
		ps.println(pad+"Message: "+getMessage());
		Throwable cause = getCause();
		if (cause != null)
		{
			ps.println(pad+"Cause: "+cause.getClass().getName());
		}
		StackTraceElement[] stackTrace = getStackTrace();
		for (StackTraceElement element : stackTrace)
		{
			ps.println(indent+"      "+element.toString());
		}
	}
}
