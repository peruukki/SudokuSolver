package main;

import static java.awt.event.ActionEvent.CTRL_MASK;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_O;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class SudokuSolver
{
    private static final class SudokuFileFilter
        extends FileFilter
    {
        private static final String extension = ".sudoku";
        
        @Override
        public final boolean accept( File pFile )
        {
            return    pFile.isDirectory()
                   || pFile.getName().endsWith( extension );
        }

        @Override
        public final String getDescription()
        {
            return "Sudoku files (*" + extension + ")";
        }
    }
    public static void main( String[] args )
    {
        try
        {
            // Set System L&F
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName() );
        }
        catch ( Exception e )
        {
            log( "Failed to set system L&F", e );
        }
        
        invokeLater( new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        } );
    }
    
    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        // Create and set up the window
        final JFrame frame = new JFrame( "Sudoku Solver" );
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        
        // Add a panel
        final JPanel panel = new JPanel();
        frame.getContentPane()
             .add( panel );
        
        // Initialize
        final BigGrid grid = new BigGrid();
        panel.add( grid.getGridPanel() );
        
        // Add buttons
        final JButton nextButton = new JButton( "Next" );
        nextButton.addActionListener( new ActionListener()
        {
            @Override
            public final void actionPerformed( ActionEvent pEvent )
            {
                if ( grid.isSolvingInProgress() )
                {
                    grid.continueSolving();
                }
                else
                {
                    startSolving( frame, grid, false );
                }
            }
        } );
        panel.add( nextButton );
        
        final JButton solveButton = new JButton( "Solve" );
        solveButton.addActionListener( new ActionListener()
        {
            @Override
            public final void actionPerformed( ActionEvent pEvent )
            {
                if ( grid.isSolvingInProgress() )
                {
                    grid.setWaitBeforeContinuing( false );
                    grid.continueSolving();
                }
                else
                {
                    startSolving( frame, grid, true );
                }
            }
        } );
        panel.add( solveButton );
        
        // Add a menu bar
        final JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar( menuBar );
        
        final JMenu fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( VK_F );
        menuBar.add( fileMenu );
        
        final JMenuItem fileOpenItem = new JMenuItem( "Open sudoku..." );
        fileOpenItem.setMnemonic( VK_O );
        fileOpenItem.setAccelerator( KeyStroke.getKeyStroke( VK_O, CTRL_MASK ) );
        fileOpenItem.addActionListener( new ActionListener()
        {
            @Override
            public final void actionPerformed( ActionEvent pEvent )
            {
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter( new SudokuFileFilter() );
                chooser.showOpenDialog( frame );
                
                final File sudokuFile = chooser.getSelectedFile();
                if ( sudokuFile != null )
                {
                    try
                    {
                        final FileInputStream inputStream = new FileInputStream( sudokuFile );
                        final byte[] content = new byte[ (int) sudokuFile.length() ];
                        inputStream.read( content );
                        grid.setInitialState( new String( content ) );
                    }
                    catch ( Exception e )
                    {
                        log( "Failed to read Sudoku file content", e );
                    }
                }
            }
        } );
        fileMenu.add( fileOpenItem );

        // Display the window
        frame.pack();
        frame.setVisible( true );
    }
    
    private static final void startSolving( final JFrame pFrame, final BigGrid pGrid,
                                            final boolean pFinish )
    {
      new Thread( new Runnable()
      {
          public void run()
          {
              pGrid.setWaitBeforeContinuing( !pFinish );
              if ( pGrid.solve() )
              {
                  pFrame.setTitle( "Solved!" );
              }
              else
              {
                  pFrame.setTitle( "There is no solution!" );
              }
          }
      } ).start();
    }
    
    public static final void log( String pMsg, Exception e )
    {
        System.out.println( pMsg + ": " + e );
        e.printStackTrace();
    }
}
