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
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM city",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet rs = pstmt.executeQuery();

            // Lista delle nuove città da inserire
            String[][] nuoveCitta = {
                {"Pompei Nord", "ITA", "Campania", "195000"},
                {"Pompei Est", "ITA", "Campania", "120000"},
                {"Pompei Sud", "ITA", "Campania", "117000"},
                {"Pompei Ovest", "ITA", "Campania", "150000"},
                {"Pompei Centro", "ITA", "Campania", "166000"},
                {"Pompei Bassa", "ITA", "Campania", "200000"},
                {"Pompei Alta", "ITA", "Campania", "155000"},
                {"Pompei Scavi", "ITA", "Campania", "154000"},
                {"Pompei nuova", "ITA", "Campania", "183000"},
                {"Pompei vecchia", "ITA", "Campaniaa", "204000"}
            };

            // Inserimento delle nuove città
            for (String[] citta : nuoveCitta) {
                rs.moveToInsertRow();
                rs.updateString("Name", citta[0]);
                rs.updateString("CountryCode", citta[1]);
                rs.updateString("District", citta[2]);
                rs.updateInt("Population", Integer.parseInt(citta[3]));
                rs.insertRow();
            }

            System.out.println("10 città italiane aggiunte con successo!");
        }
    }
}
