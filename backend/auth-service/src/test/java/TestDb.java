import java.sql.*;

public class TestDb {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/skillverse_auth";
        String user = "postgres";
        String password = "admin123";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Connexion réussie !");
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT version()");
                if (rs.next()) {
                    System.out.println("Version: " + rs.getString(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }
}