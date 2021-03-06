/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.index.gbptree;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.neo4j.io.pagecache.CursorException;
import org.neo4j.io.pagecache.PageCursor;

/**
 * TODO: Adding another implementation of PageCursor (this implementation)
 * TODO: will mean Neo as a whole have three implementations in total.
 * TODO: This results in a slower method dispatch for ALL PageCursors. Is it worth it?
 * <p>
 * Wraps a byte array and present it as a PageCursor.
 * <p>
 * This class is bridging something which would otherwise make {@link InternalTreeLogic} code slightly more
 * complicated. Currently when splitting nodes keys/values/children are read into temporary arrays
 * and manipulating that data by standard means (which are designed to work on {@link PageCursor}
 * can stay the same if wrapping the byte array as such. If splitting code later changes to not
 * do this temporary copy then this class won't be needed anymore.
 * <p>
 * All the accessor methods (getXXX, putXXX) are implemented and delegates calls to its internal {@link ByteBuffer}.
 * {@link #setOffset(int)}, {@link #getOffset()} and {@link #rewind()} positions the internal {@link ByteBuffer}.
 * {@link #shouldRetry()} always returns {@code false}. No other methods should be used and will throw
 * {@link UnsupportedOperationException}.
 */
class ByteArrayPageCursor extends PageCursor
{
    private final ByteBuffer buffer;

    static PageCursor wrap( byte[] array, int offset, int length )
    {
        return new ByteArrayPageCursor( array, offset, length );
    }

    static PageCursor wrap( byte[] array )
    {
        return wrap( array, 0, array.length );
    }

    static PageCursor wrap( int length )
    {
        return wrap( new byte[length] );
    }

    private ByteArrayPageCursor( byte[] array, int offset, int length )
    {
        this.buffer = ByteBuffer.wrap( array, offset, length );
    }

    @Override
    public byte getByte()
    {
        return buffer.get();
    }

    @Override
    public byte getByte( int offset )
    {
        return buffer.get( offset );
    }

    @Override
    public void putByte( byte value )
    {
        buffer.put( value );
    }

    @Override
    public void putByte( int offset, byte value )
    {
        buffer.put( offset, value );
    }

    @Override
    public long getLong()
    {
        return buffer.getLong();
    }

    @Override
    public long getLong( int offset )
    {
        return buffer.getLong( offset );
    }

    @Override
    public void putLong( long value )
    {
        buffer.putLong( value );
    }

    @Override
    public void putLong( int offset, long value )
    {
        buffer.putLong( offset, value );
    }

    @Override
    public int getInt()
    {
        return buffer.getInt();
    }

    @Override
    public int getInt( int offset )
    {
        return buffer.getInt( offset );
    }

    @Override
    public void putInt( int value )
    {
        buffer.putInt( value );
    }

    @Override
    public void putInt( int offset, int value )
    {
        buffer.putInt( offset, value );
    }

    @Override
    public void getBytes( byte[] data )
    {
        buffer.get( data );
    }

    @Override
    public void getBytes( byte[] data, int arrayOffset, int length )
    {
        buffer.get( data, arrayOffset, length );
    }

    @Override
    public void putBytes( byte[] data )
    {
        buffer.put( data );
    }

    @Override
    public void putBytes( byte[] data, int arrayOffset, int length )
    {
        buffer.put( data, arrayOffset, length );
    }

    @Override
    public short getShort()
    {
        return buffer.getShort();
    }

    @Override
    public short getShort( int offset )
    {
        return buffer.getShort( offset );
    }

    @Override
    public void putShort( short value )
    {
        buffer.putShort( value );
    }

    @Override
    public void putShort( int offset, short value )
    {
        buffer.putShort( offset, value );
    }

    @Override
    public void setOffset( int offset )
    {
        buffer.position( offset );
    }

    @Override
    public int getOffset()
    {
        return buffer.position();
    }

    @Override
    public long getCurrentPageId()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCurrentPageSize()
    {
        return buffer.capacity();
    }

    @Override
    public File getCurrentFile()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void rewind()
    {
        setOffset( 0 );
    }

    @Override
    public boolean next() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean next( long pageId ) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close()
    {   // Nothing to close
    }

    @Override
    public boolean shouldRetry() throws IOException
    {
        return false;
    }

    @Override
    public int copyTo( int sourceOffset, PageCursor targetCursor, int targetOffset, int lengthInBytes )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkAndClearBoundsFlag()
    {
        return false;
    }

    @Override
    public void checkAndClearCursorException() throws CursorException
    {   // Don't check
    }

    @Override
    public void raiseOutOfBounds()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCursorException( String message )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearCursorException()
    {   // Don't check
    }

    @Override
    public PageCursor openLinkedCursor( long pageId )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void zapPage()
    {
        Arrays.fill( buffer.array(), (byte) 0 );
    }
}
