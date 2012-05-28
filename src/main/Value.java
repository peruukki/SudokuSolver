package main;

public final class Value
{
    private static final int NOT_SET = -1;
    public static final Value EMPTY = new Value( NOT_SET, false );
    
    public final int value;
    public final boolean initial;
    
    public Value( int pValue, boolean pInitial )
    {
        value = pValue;
        initial = pInitial;
    }
    
    public final boolean isEmpty()
    {
        return value == NOT_SET;
    }
    
    @Override
    public final boolean equals( Object pObj )
    {
        boolean areEqual = false;
        
        if (    pObj != null
             && getClass().equals( pObj.getClass() ) )
        {
            final Value other = (Value) pObj;
            areEqual = ( value == other.value );
        }
        
        return areEqual;
    }
    
    @Override
    public final String toString()
    {
        final String output;
        if ( isEmpty() )
        {
            output = " ";
        }
        else
        {
            output = Integer.toString( value );
        }
        return output;
    }
}
