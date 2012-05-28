package main;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Positions
{
    private final Set< Position > mPositions = new HashSet< Position >();

    public final Collection< Position > getPositions()
    {
        return mPositions;
    }
    
    public final void addPosition( Position pPosition )
    {
        mPositions.add( pPosition );
    }
    
    public final void removePosition( Position pPosition )
    {
        mPositions.remove( pPosition );
    }
}
