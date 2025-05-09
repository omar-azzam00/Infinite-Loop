import Auth.User;
import Auth.*;

import java.sql.*;

public class App {
    public static void main(String[] args) throws Exception {
        Auth auth = new Auth();   

        User user = auth.run();   

        // Now you got the user with his information, you can continue from her
    }
}
