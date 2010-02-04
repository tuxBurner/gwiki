/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.11.2006
// Copyright Micromata 19.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;

import de.micromata.genome.gwiki.page.gspt.jdkrepl.PrintWriterPatched;

/**
 * Wird im Jetty-Umfeld verwendet.
 */
public class GspJspWriter extends JspWriter implements BodyFlusher
{
  PrintWriterPatched out;

  int bufferSize = 1024;

  boolean wasOverflow = false;

  StringBuilder buffer = new StringBuilder(bufferSize);

  public GspJspWriter(PrintWriterPatched out)
  {
    super(0, true);
    this.out = out;
  }

  public PrintWriterPatched getUnderlyingWriter()
  {
    return out;
  }

  public void flushBody() throws IOException
  {
    overflow();
    out.flushBody();
  }

  public void overflow()
  {
    out.print(buffer.toString());
    // out.flush();
    buffer.setLength(0);
    wasOverflow = true;
  }

  public void write(String s) throws IOException
  {

    if (wasOverflow == true) {
      out.write(s);
      return;
    }
    buffer.append(s);
    if (buffer.length() > bufferSize)
      overflow();
  }

  @Override
  public void clear() throws IOException
  {
    if (wasOverflow == true)
      throw new IOException("Buffer already overflow");
    buffer.setLength(0);
  }

  @Override
  public void clearBuffer() throws IOException
  {
    if (wasOverflow == false)
      return;
    buffer.setLength(0);
  }

  @Override
  public void close() throws IOException
  {
    overflow();
    out.close();
  }

  @Override
  public void flush() throws IOException
  {
    overflow();
    out.flush();
  }

  @Override
  public int getRemaining()
  {
    if (wasOverflow == true)
      return 0;
    return bufferSize - buffer.length();
  }

  @Override
  public void newLine() throws IOException
  {
    write("\n");
  }

  @Override
  public void print(boolean arg0) throws IOException
  {
    write(Boolean.toString(arg0));
  }

  @Override
  public void print(char arg0) throws IOException
  {
    write(Character.toString(arg0));
  }

  @Override
  public void print(int arg0) throws IOException
  {
    write(Integer.toString(arg0));
  }

  @Override
  public void print(long arg0) throws IOException
  {
    write(Long.toString(arg0));
  }

  @Override
  public void print(float arg0) throws IOException
  {
    write(Float.toString(arg0));
  }

  @Override
  public void print(double arg0) throws IOException
  {
    write(Double.toString(arg0));
  }

  @Override
  public void print(char[] arg0) throws IOException
  {
    write(new String(arg0));
  }

  @Override
  public void print(String arg0) throws IOException
  {
    write(arg0);
  }

  @Override
  public void print(Object arg0) throws IOException
  {
    write(arg0 == null ? null : arg0.toString());
  }

  @Override
  public void println() throws IOException
  {
    write("\n");
  }

  @Override
  public void println(boolean arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(char arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(int arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(long arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(float arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(double arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(char[] arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(String arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(Object arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException
  {
    write(new String(cbuf, off, len));
  }

  @Override
  public Writer append(char c) throws IOException
  {
    write(c);
    return this;
  }

  @Override
  public Writer append(CharSequence csq, int start, int end) throws IOException
  {
    CharSequence cs = (csq == null ? "null" : csq);
    write(cs.subSequence(start, end).toString());
    return this;
  }

  @Override
  public Writer append(CharSequence csq) throws IOException
  {
    if (csq == null)
      write("null");
    else
      write(csq.toString());
    return this;
  }

  @Override
  public void write(char[] cbuf) throws IOException
  {
    write(new String(cbuf));
  }

  @Override
  public void write(int c) throws IOException
  {
    write(Integer.toString(c));
  }

  @Override
  public void write(String str, int off, int len) throws IOException
  {
    write(str.substring(off, len));
  }

}