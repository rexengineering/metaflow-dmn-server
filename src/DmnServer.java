import java.util.logging.*;

import com.rexhomes.dmn.server.Server;
import com.rexhomes.dmn.server.ServerImpl;

public class DmnServer {
    private static final Logger logger = Logger.getLogger(DmnServer.class.getName());

    public static void main(String[] args) {
        Server server = new ServerImpl();
    }
}
