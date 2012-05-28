package main;

import static main.SmallGrid.SIZE_GRID;
import static main.SmallGrid.VALUE_MAX;
import static main.SudokuSolver.log;
import static main.Value.EMPTY;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public final class BigGrid
{
    private static final String INITIAL_STATE = "+---+---+---+\n" +
                                                "| 2 |   |487|\n" +
                                                "| 8 |534|   |\n" +
                                                "| 4 |  8| 31|\n" +
                                                "+---+---+---+\n" +
                                                "|7  |84 |   |\n" +
                                                "| 3 |  5|9 4|\n" +
                                                "|9  |32 |7  |\n" +
                                                "+---+---+---+\n" +
                                                "|  5|  2| 1 |\n" +
                                                "|892| 67| 4 |\n" +
                                                "|  1|95 |  2|\n" +
                                                "+---+---+---+";
    
    private final SmallGrid[][] mGrid = new SmallGrid[ SIZE_GRID ][ SIZE_GRID ];
    
    private boolean mInProgress;
    private boolean mWaitSync;
    private final Object mSync = new Object();
    
    public BigGrid()
    {
        createSmallGrids();
    }
    
    public final boolean isSolvingInProgress()
    {
        return mInProgress;
    }
    
    public final void setWaitBeforeContinuing( boolean pWait )
    {
        mWaitSync = pWait;
    }
    
    public final void setInitialState( String pContent )
    {
        clearSmallGrids();
        fillSmallGrids( pContent != null ? pContent : INITIAL_STATE );
    }
    
    public final JPanel getGridPanel()
    {
        final JPanel panel = new JPanel( new GridBagLayout() );
        final GridBagConstraints c = new GridBagConstraints();
        
        for ( int y = 0; y < SIZE_GRID; y++ )
        {
            c.gridy = y;
            for ( int x = 0; x < SIZE_GRID; x++ )
            {
                c.gridx = x;
                panel.add( mGrid[ x ][ y ].getGridPanel(), c );
            }
        }
        
        return panel;
    }
    
    public final boolean solve()
    {
        mInProgress = true;
        return solve( 1, 1 );
    }
    
    private final boolean solve( int pGridIndex, int pValue )
    {
        boolean isSolved = false;
        
        final List< Position > positions = getPossiblePositions( pGridIndex, pValue );
        for ( int i = 0; !isSolved && i < positions.size(); i++ )
        {
            final Position position = positions.get( i );
            
            setValue( pGridIndex, position, pValue );
            
            synchronized ( mSync )
            {
                if ( mWaitSync )
                {
                    try
                    {
                        mSync.wait();
                    }
                    catch ( InterruptedException e )
                    {
                        log( "Interrupted", e );
                    }
                }
            }
            
            if ( pGridIndex < VALUE_MAX )
            {
                isSolved = solve( pGridIndex + 1, pValue );
            }
            else if ( pValue < VALUE_MAX )
            {
                isSolved = solve( 1, pValue + 1 );
            }
            else
            {
                isSolved = true;
            }
            
            if ( !isSolved )
            {
                removeValue( pGridIndex, position );
            }
        }
        
        return isSolved;
    }
    
    private final void createSmallGrids()
    {
        for ( int y = 0; y < SIZE_GRID; y++ )
        {
            for ( int x = 0; x < SIZE_GRID; x++ )
            {
                mGrid[ x ][ y ] = new SmallGrid();
            }
        }
    }
    
    private final void clearSmallGrids()
    {
        for ( int y = 0; y < SIZE_GRID; y++ )
        {
            for ( int x = 0; x < SIZE_GRID; x++ )
            {
                mGrid[ x ][ y ].clear();
            }
        }
    }
    
    private final void fillSmallGrids( String pContent )
    {
        int x = 0;
        int y = 0;
        
        for ( final String bigGridLine: pContent.split( "\r\n|\n" ) )
        {
            if ( bigGridLine.startsWith( "|" ) )
            {
                x = 0;
                for ( final String smallGridLine: bigGridLine.substring( 1 )
                                                             .split( "\\|" ) )
                {
                    for ( int i = 0; i < SIZE_GRID; i++ )
                    {
                        final char charValue = smallGridLine.charAt( i );
                        final Value value; 
                        if ( charValue == ' ' )
                        {
                            value = EMPTY;
                        }
                        else
                        {
                            value = new Value( Character.digit( charValue, 10 ), true );
                        }
                        setValue( x, y, value );
                        x++;
                    }
                }
                y++;
            }
        }
    }
    
    private final void setValue( int pX, int pY, Value pValue )
    {
        mGrid[ toGrid( pX ) ][ toGrid( pY ) ].setValue( toIndex( pX ), toIndex( pY ), pValue );
    }
    
    private final List< Position > getPossiblePositions( int pGridIndex, int pValue )
    {
        final List< Position > positions = new ArrayList< Position >();
        
        final int gridX = toGridX( pGridIndex );
        final int gridY = toGridY( pGridIndex );
        Position existingPosition =
            mGrid[ gridX ][ gridY ].getExistingPosition( pValue );        
        if ( existingPosition != null )
        {
            positions.add( existingPosition );
        }
        else
        {
            final List< Position > possiblePositions =
                mGrid[ gridX ][ gridY ].getEmptyPositions();
            for ( final Position position: possiblePositions )
            {
                boolean stillPossible = true;
                
                for ( int x = 0; stillPossible && x < SIZE_GRID; x++ )
                {
                    if ( x != gridX )
                    {
                        existingPosition = mGrid[ x ][ gridY ].getExistingPosition( pValue );
                        stillPossible =    existingPosition == null
                                        || existingPosition.y != position.y;
                    }
                }
                
                for ( int y = 0; stillPossible && y < SIZE_GRID; y++ )
                {
                    if ( y != gridY )
                    {
                        existingPosition = mGrid[ gridX ][ y ].getExistingPosition( pValue );
                        stillPossible =    existingPosition == null
                                        || existingPosition.x != position.x;
                    }
                }
                
                if ( stillPossible )
                {
                    positions.add( position );
                }
            }
        }
        
        return positions;
    }
    
    private final void setValue( int pGridIndex, Position pPosition, int pValue )
    {
        mGrid[ toGridX( pGridIndex ) ][ toGridY( pGridIndex ) ]
            .setValue( pPosition, new Value( pValue, false ) );
    }
    
    private final void removeValue( int pGridIndex, Position pPosition )
    {
        mGrid[ toGridX( pGridIndex ) ][ toGridY( pGridIndex ) ].removeValue( pPosition );
    }

    private static final int toGridX( int pGridIndex )
    {
        return toIndex( pGridIndex - 1 );
    }

    private static final int toGridY( int pGridIndex )
    {
        return toGrid( pGridIndex - 1 );
    }
    
    private static final int toGrid( int pIndex )
    {
        return pIndex / SIZE_GRID;
    }
    
    private static final int toIndex( int pIndex )
    {
        return pIndex % SIZE_GRID;
    }
    
    public final void continueSolving()
    {
        synchronized ( mSync )
        {
            mSync.notify();
        }
    }
}
