package anonscanlations.downloader;

/**
 * 
 * @author /a/non
 */
class ExecutionException extends Exception
{
    private Exception e;
    public ExecutionException(Exception _e)
    {
        e = _e;
    }
    public Exception getException(){ return(e); }
}
