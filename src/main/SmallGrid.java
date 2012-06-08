package main;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static javax.swing.SwingConstants.CENTER;
import static main.Value.EMPTY;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class SmallGrid
{
    public static final int SIZE_GRID = 3;
    public static final int VALUE_MAX = SIZE_GRID * SIZE_GRID;
    
    private final Value[][] mGrid = new Value[ SIZE_GRID ][ SIZE_GRID ];
    private final JPanel mPanel = createGridPanel();
    
    public SmallGrid()
    {
        clear();
    }
    
    public final void clear()
    {
        for ( int y = 0; y < SIZE_GRID; y++ )
        {
            for ( int x = 0; x < SIZE_GRID; x++ )
            {
                mGrid[ x ][ y ] = null;
                setValue( x, y, EMPTY );
            }
        }
    }
    
    public final void setValue( Position pPosition, Value pValue )
    {
        setValue( pPosition.x, pPosition.y, pValue );
    }
    
    public final void setValue( int pX, int pY, Value pValue )
    {
        final JLabel label = getLabel( pX, pY );
        final Color borderColor;
        int thickness;
        
        if (    mGrid[ pX ][ pY ] == null
             || !mGrid[ pX ][ pY ].initial )
        {
            mGrid[ pX ][ pY ] = pValue;
            label.setText( pValue.toString() );
            
            borderColor = ( pValue.initial || pValue.isEmpty() ) ? BLACK : GREEN;
        }
        else if ( !pValue.isEmpty() )
        {
            borderColor = BLUE;
        }
        else
        {
            borderColor = BLACK;
        }
        
        thickness = ( borderColor == BLACK ) ? 1 : 2;
        label.setBorder( BorderFactory.createLineBorder( borderColor, thickness ) );
    }
    
    public final void removeValue( Position pPosition )
    {
        setValue( pPosition, EMPTY );
    }
    
    public final String getStringValue( int pX, int pY )
    {
        return mGrid[ pX ][ pY ].toString();
    }
    
    private final JPanel createGridPanel()
    {
        final JPanel panel = new JPanel( new GridBagLayout() );
        final GridBagConstraints c = new GridBagConstraints();
        
        panel.setBorder( BorderFactory.createLineBorder( BLACK, 2 ) );
        
        for ( int y = 0; y < SIZE_GRID; y++ )
        {
            c.gridy = y;
            for ( int x = 0; x < SIZE_GRID; x++ )
            {
                c.gridx = x;
                final JLabel label = new JLabel();
                label.setPreferredSize( new Dimension( 30, 30 ) );
                label.setHorizontalAlignment( CENTER );
                panel.add( label, c );
            }
        }
        
        return panel;
    }
    
    private final JLabel getLabel( int pX, int pY )
    {
        return (JLabel) mPanel.getComponent( pX % SIZE_GRID + pY * SIZE_GRID );
    }
    
    public final JPanel getGridPanel()
    {
        return mPanel;
    }
    
    public final List< Position > getEmptyPositions()
    {
        final List< Position > positions = new ArrayList< Position >();
        
        for ( int y = 0; y < SIZE_GRID; y++ )
        {
            for ( int x = 0; x < SIZE_GRID; x++ )
            {
                if ( mGrid[ x ][ y ].isEmpty() )
                {
                    positions.add( new Position( x, y ) );
                }
            }
        }
        
        return positions;
    }
    
    public final Position getExistingPosition( int pValue )
    {
        Position position = null;
        
        for ( int y = 0; position == null && y < SIZE_GRID; y++ )
        {
            for ( int x = 0; position == null && x < SIZE_GRID; x++ )
            {
                if ( mGrid[ x ][ y ].value == pValue )
                {
                    position = new Position( x, y ); 
                }
            }
        }
        
        return position;
    }
}
