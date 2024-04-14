package pygmy.handlers.jython;

import pygmy.core.*;

import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Properties;
import java.util.List;
import java.net.HttpURLConnection;

import org.python.util.PythonInterpreter;
import org.python.core.PyException;
import org.python.core.PySystemState;

public class JythonHandler extends AbstractHandler {

    private static final Logger log = Logger.getLogger( JythonHandler.class.getName() );

    File scriptsDir;
    File pythonHome;
    List pythonPaths;
    PythonInterpreter interpreter;
    private static final String PYTHON_HOME = "python.home";
    private static final String PYTHON_PATH = "python.path";

    public JythonHandler(File pythonHome, File scriptsDir) {
        this.pythonHome = pythonHome;
        this.scriptsDir = scriptsDir;
    }

    public JythonHandler addPath( String path ) {
        pythonPaths.add( path );
        return this;
    }

    public boolean start(Server server) {
        super.start(server);

        Properties props = new Properties();

        props.put( PYTHON_HOME, pythonHome.getAbsolutePath() );
        props.put( PYTHON_PATH, join( pythonPaths, ":") );
        PythonInterpreter.initialize( System.getProperties(), props, new String[0] );

        interpreter = new PythonInterpreter(null, new PySystemState());
        interpreter.setErr( new LogWriter( Level.SEVERE ) );
        interpreter.setOut( new LogWriter( Level.INFO ) );
//        PySystemState sys = Py.getSystemState();
//        sys.path.append(new PyString(rootPath));
        return true;
    }

    private String join(List list, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for( int i = 0; i < list.size(); i++ ) {
            builder.append( list.get(0) );
            if( i + 1 < list.size() ) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        if( request.getUrl().endsWith(".py") ) {
            try {
                if( log.isLoggable( Level.INFO ) ) {
                    log.log( Level.INFO, "Executing script: " + request.getUrl() );
                }
                interpreter.set("request", request);
                interpreter.set("response", response);
                interpreter.execfile( Http.translatePath(scriptsDir, request.getUrl() ).getAbsolutePath() );
            } catch( PyException e ) {
                log.error( e.getMessage(), e );
                response.sendError( HttpURLConnection.HTTP_INTERNAL_ERROR, "Script error", e);
            }
            return true;
        }
        return false;
    }

    public class LogWriter extends Writer {
        Level level;
        StringBuffer buf = new StringBuffer();

        public LogWriter(Level level) {
            this.level = level;
        }

        public synchronized void write(char cbuf[], int off, int len) throws IOException {
            buf.append( cbuf, off, len );
        }

        public synchronized void flush() throws IOException {
            log.log( level, buf.toString()  );
            buf.delete( 0, buf.length() - 1 );
        }

        public void close() throws IOException {
            flush();
        }
    }
}