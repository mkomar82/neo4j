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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import org.neo4j.io.pagecache.PageCursor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TreeStateTest
{
    private final int pageSize = 256;
    private final PageAwareByteArrayCursor cursor = new PageAwareByteArrayCursor( pageSize );

    @Before
    public void initiateCursor() throws IOException
    {
        cursor.next();
    }

    @Test
    public void readEmptyStateShouldThrow() throws Exception
    {
        // GIVEN empty state

        // WHEN
        TreeState state = TreeState.read( cursor );

        // THEN
        assertFalse( state.isValid() );
    }

    @Test
    public void shouldReadValidPage() throws Exception
    {
        // GIVEN valid state
        TreeState.write( cursor, 1, 2, 3, 4 );
        cursor.rewind();

        // WHEN
        boolean valid = TreeState.read( cursor ).isValid();

        // THEN
        assertTrue( valid );
    }

    @Test
    public void readBrokenStateShouldFail() throws Exception
    {
        // GIVEN broken state
        TreeState.write( cursor, 1, 2, 3, 4 );
        cursor.rewind();
        assertTrue( TreeState.read( cursor ).isValid() );
        cursor.rewind();
        breakChecksum( cursor );

        // WHEN
        TreeState state = TreeState.read( cursor );

        // THEN
        assertFalse( state.isValid() );
    }

    @Test
    public void shouldNotWriteInvalidStableGeneration() throws Exception
    {
        // GIVEN
        long generation = GenSafePointer.MAX_GENERATION + 1;

        // WHEN
        try
        {
            TreeState.write( cursor, generation, 2, 3, 4 );
            fail( "Should have failed" );
        }
        catch ( IllegalArgumentException e )
        {
            // THEN good
        }
    }

    @Test
    public void shouldNotWriteInvalidUnstableGeneration() throws Exception
    {
        // GIVEN
        long generation = GenSafePointer.MAX_GENERATION + 1;

        // WHEN
        try
        {
            TreeState.write( cursor, 1, generation, 3, 4 );
            fail( "Should have failed" );
        }
        catch ( IllegalArgumentException e )
        {
            // THEN good
        }
    }

    private void breakChecksum( PageCursor cursor )
    {
        // Doesn't matter which bits we destroy actually. Destroying the first ones requires
        // no additional knowledge about where checksum is stored
        long existing = cursor.getLong( cursor.getOffset() );
        cursor.putLong( cursor.getOffset(), ~existing );
    }
}
