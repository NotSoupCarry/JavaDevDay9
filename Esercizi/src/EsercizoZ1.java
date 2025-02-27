import java.sql.*;

public class EsercizoZ1 {
    private static final String URL = "jdbc:mysql://localhost:3306/world";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static void main(String[] args) {
        try {
            // Carica il driver JDBC
            Class.forName(DB_DRIVER);

            // Connessione al database
            try (Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
                    Statement statement = conn.createStatement()) {

                System.out.println("Creazione della view delle città italiane...");
                creaViewCittaItaliane(statement);

                System.out.println("\nCittà italiane presenti nella view:");
                stampaViewCittaItaliane(statement);

                System.out.println("\nInserimento di 10 nuove città italiane...");
                aggiungiCittaItaliane(conn);

                System.out.println("\nFINE");

                statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Errore: Driver MySQL non trovato!");
            e.printStackTrace();
        }
    }

    // Metodo separato per creare la view
    public static void creaViewCittaItaliane(Statement statement) throws SQLException {
        String query = "CREATE OR REPLACE VIEW italian_cities_view AS " +
                "SELECT ID, Name, CountryCode, District, Population FROM city " +
                "WHERE CountryCode = 'ITA'";

        statement.executeUpdate(query);
    }

    // Metodo per stampare la view delle città italiane
    public static void stampaViewCittaItaliane(Statement statement) throws SQLException {
        String query = "SELECT * FROM italian_cities_view";

        try (ResultSet rs = statement.executeQuery(query)) {
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();

            // Stampa intestazione colonne
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metadata.getColumnName(i) + "\t");
            }
            System.out.println("\n-----------------------------------------------------");

            // Stampa i dati della view
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        }

    }

    // Metodo per aggiungere 10 nuove città italiane
    public static void aggiungiCittaItaliane(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("SELECT COUNT(*) FROM city WHERE Name = ? AND CountryCode = ?");
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO city (Name, CountryCode, District, Population) VALUES (?, ?, ?, ?)")) {

            // Lista delle nuove città da inserire
            String[][] nuoveCitta = {
                    { "Pompei Nord", "ITA", "Campania", "195000" },
                    { "Pompei Est", "ITA", "Campania", "120000" },
                    { "Pompei Sud", "ITA", "Campania", "117000" },
                    { "Pompei Ovest", "ITA", "Campania", "150000" },
                    { "Pompei Centro", "ITA", "Campania", "166000" },
                    { "Pompei Bassa", "ITA", "Campania", "200000" },
                    { "Pompei Alta", "ITA", "Campania", "155000" },
                    { "Pompei Scavi", "ITA", "Campania", "154000" },
                    { "Pompei nuova", "ITA", "Campania", "183000" },
                    { "Pompei vecchia", "ITA", "Campania", "204000" },
                    { "Pompei 2077", "ITA", "Campania", "204000" },
                    { "Pompei cyberpunk", "ITA", "Campania", "204000" }

            };

            int aggiunte = 0; // Contatore per città aggiunte

            for (String[] citta : nuoveCitta) {
                // Verifica se la città esiste già nel database
                pstmt.setString(1, citta[0]); 
                pstmt.setString(2, citta[1]); 
                try (ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        System.out.println("Città già presente: " + citta[0]);
                        continue; 
                    }
                }

                // Se la città non esiste, la inseriamo
                insertStmt.setString(1, citta[0]); // Nome
                insertStmt.setString(2, citta[1]); // CountryCode
                insertStmt.setString(3, citta[2]); // District
                insertStmt.setInt(4, Integer.parseInt(citta[3])); // Population
                insertStmt.executeUpdate();
                System.out.println("Città aggiunta: " + citta[0]);
                aggiunte++;
            }

            System.out.println("\nTotale nuove città aggiunte: " + aggiunte);
        }
    }

}
