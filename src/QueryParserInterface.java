import java.io.IOException;
import java.nio.file.Path;

public interface QueryParserInterface {

	public void stemQuery(Path queryFile, boolean exact) throws IOException;
	
	public void toSearchResult(Path resultPath) throws IOException;
}
