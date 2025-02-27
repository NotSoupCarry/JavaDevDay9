import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EsercizioZ2 {

    private static final int LUNGHEZZA_STRINGA_COUNTRYCODE = 3;
    private static final int LUNGHEZZA_STRINGA_COUNTRYCODE2 = 2;

    // Metodo per la connessione
    public static Connection connessioneDatabase() {
        // Dati di connessione
        String DB_URL = "jdbc:mysql://localhost:3306/world";
        String DB_USERNAME = "root";
        String DB_PASSWORD = "root";

        Connection conn = null;

        try {
            // Carica il driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Crea la connessione
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // Il metodo getCatalog() ritorna il nome dello schema a cui si è collegati
            String databaseName = conn.getCatalog();
            System.out.println("Connessione riuscita al database: " + databaseName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static void main(String[] args) {
        Connection conn = connessioneDatabase();
        if (conn != null) {
            Scanner scanner = new Scanner(System.in);
            try {
                mostraMenuPrincipale(scanner, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connessione al database fallita.");
        }
    }

    // #region METODI PER CREAZIONE DEI SET

    // Metodo per prendere lo Statement
    public static Statement getStatement(Connection conn) {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per la creazione degli statement scrollable e updatable
    public static PreparedStatement createPreparedStatement(Connection conn, String query) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            return pstmt;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per prendere i metadata
    public static ResultSetMetaData getResultSetMetaData(ResultSet rs) {
        try {
            return rs.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    // #endregion

    // #region METODI PER IL CONTROLLO DEGLI INPUT

    // Metodo per controllare l'input intero, con possibilità di inserire null
    public static Integer controlloInputInteri(Scanner scanner, boolean allowNull) {
        Integer valore = null; // Valore che ritorneremo (può essere null)
        do {
            if (allowNull) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    // Se è permesso null e l'input è vuoto, ritorna null
                    return null;
                }
                try {
                    valore = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.print("Devi inserire un numero intero. Riprova: ");
                }
            } else {
                while (!scanner.hasNextInt()) {
                    System.out.print("Devi inserire un numero intero. Riprova: ");
                    scanner.next();
                }
                valore = scanner.nextInt();
            }

            if (valore != null && valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore != null && valore < 0);

        return valore;
    }

    // Metodo per controllare l'input double, con possibilità di inserire null
    public static Double controlloInputDouble(Scanner scanner, boolean allowNull) {
        Double valore = null; // Valore che ritorneremo (può essere null)
        do {
            if (allowNull) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    // Se è permesso null e l'input è vuoto, ritorna null
                    return null;
                }
                try {
                    valore = Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    System.out.print("Devi inserire un numero decimale valido. Riprova: ");
                }
            } else {
                while (!scanner.hasNextDouble()) {
                    System.out.print("Devi inserire un numero decimale valido. Riprova: ");
                    scanner.next(); // Consuma l'input errato
                }
                valore = scanner.nextDouble();
            }

            if (valore != null && valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore != null && valore < 0);

        return valore;
    }

    // Metodo per controllare che l'input stringa non sia vuoto
    public static String controlloInputStringhe(Scanner scanner) {
        String valore;
        do {
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.print("Input non valido. Inserisci un testo: ");
            }
        } while (valore.isEmpty());
        return valore;
    }

    // Metodo per controllare che l'input stringa void e lenght
    public static String controlloInputStringheConLunghezza(Scanner scanner, int lunghezzaStringa) {
        String valore;
        do {
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.print("Input non valido. Inserisci un testo (non vuoto): ");
            } else if (valore.length() != lunghezzaStringa) {
                System.out.print("Input non valido. La lunghezza deve essere esattamente " + lunghezzaStringa
                        + " caratteri. Riprova: ");
            }
        } while (valore.isEmpty() || valore.length() != lunghezzaStringa);
        return valore;
    }

    // #endregion

    // #region METODI PER LA GESTIONE DELLE MODIFICHE E VISUALIZZAZIONE SUL DB

    // Metodo per recuperare le info di city
    public static void getInfoCity() {
        try {
            String queryCity = "SELECT * FROM city LIMIT 20";
            PreparedStatement pstmt = createPreparedStatement(connessioneDatabase(), queryCity);
            ResultSet rs = pstmt.executeQuery();
            stampaDati(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo per recuperare le info di country
    public static void getInfoCountry() {
        try {
            // Query per selezionare i primi 20 record dalla tabella country
            String queryCountry = "SELECT * FROM country LIMIT 20";

            // Creiamo un PreparedStatement aggiornabile e recuperiamo il ResultSet
            PreparedStatement pstmt = createPreparedStatement(connessioneDatabase(), queryCountry);
            ResultSet rs = pstmt.executeQuery(); // Otteniamo il ResultSet

            stampaDati(rs);

            // Chiudere le risorse
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per la stampa dei dati
    public static void stampaDati(ResultSet rs) {
        try {
            // Otteniamo i metadati per conoscere le colonne
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Stampare i nomi delle colonne
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsmd.getColumnName(i) + "\t"); // \t per separare con tabulazione
            }
            System.out.println(); // Vai a capo dopo i nomi delle colonne

            // Stampare i dati riga per riga
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t"); // Stampa i dati allineati con tabulazione
                }
                System.out.println(); // Vai a capo dopo ogni riga
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per scambiare due reord in country
    public static void scambiaRecordCountry(Connection conn, Scanner input) throws SQLException {
        System.out.print("Inserisci il codice del primo paese: ");
        String country1 = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE);

        System.out.print("Inserisci il codice del secondo paese: ");
        String country2 = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE);

        // Interrogare i nomi dei paesi per scambiarli
        String selectQuery = "SELECT Name FROM country WHERE Code = ?";
        String tempCountry1Name = null, tempCountry2Name = null;

        try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
            // Recupera il nome del primo paese
            pstmt.setString(1, country1);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tempCountry1Name = rs.getString("Name");
            }

            // Recupera il nome del secondo paese
            pstmt.setString(1, country2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                tempCountry2Name = rs.getString("Name");
            }
        }

        // Usa un PreparedStatement per aggiornare i record nella tabella 'country'
        String updateQuery1 = "UPDATE country SET Name = ? WHERE Code = ?";
        String updateQuery2 = "UPDATE country SET Name = ? WHERE Code = ?";

        try (PreparedStatement pstmt1 = conn.prepareStatement(updateQuery1);
                PreparedStatement pstmt2 = conn.prepareStatement(updateQuery2)) {

            // 1. Aggiorna il primo paese con un nome temporaneo
            pstmt1.setString(1, "TEMP");
            pstmt1.setString(2, country1);
            pstmt1.executeUpdate();

            // 2. Aggiorna il secondo paese con il nome del primo paese
            pstmt2.setString(1, tempCountry1Name);
            pstmt2.setString(2, country2);
            pstmt2.executeUpdate();

            // 3. Aggiorna il primo paese con il nome del secondo paese
            pstmt1.setString(1, tempCountry2Name);
            pstmt1.setString(2, country1);
            pstmt1.executeUpdate();

            System.out.println("Scambio tra i paesi con codice " + country1 + " e " + country2 + " completato.");
        }
    }

    // Creare un Trigger per salvare i record eliminati
    public static void creaTrigger(Connection conn) throws SQLException {
        String cityTrigger = "CREATE TABLE IF NOT EXISTS city_log (ID INT, Name VARCHAR(255), CountryCode CHAR(3), District VARCHAR(255), Population INT)";
        Statement stmt = getStatement(conn);
        stmt.executeUpdate(cityTrigger);

        stmt.executeUpdate("DROP TRIGGER IF EXISTS before_city_delete");
        stmt.executeUpdate("CREATE TRIGGER before_city_delete BEFORE DELETE ON city " +
                "FOR EACH ROW INSERT INTO city_log VALUES (OLD.ID, OLD.Name, OLD.CountryCode, OLD.District, OLD.Population)");

        String countryTrigger = "CREATE TABLE IF NOT EXISTS country_log (Code CHAR(3), Name VARCHAR(255), Population INT)";
        stmt.executeUpdate(countryTrigger);

        stmt.executeUpdate("DROP TRIGGER IF EXISTS before_country_delete");
        stmt.executeUpdate("CREATE TRIGGER before_country_delete BEFORE DELETE ON country " +
                "FOR EACH ROW INSERT INTO country_log VALUES (OLD.Code, OLD.Name, OLD.Population)");
    }

    // Metodo per aggiungere un nuovo paese
    public static void aggiungiNuovoPaese(Connection conn, Scanner input) throws SQLException {

        System.out.print("Inserisci il code della città: ");
        String code = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE);

        System.out.print("Inserisci il nome della città: ");
        String name = controlloInputStringhe(input);

        System.out.print("Inserisci il continente: ");
        String continent = controlloInputStringhe(input);

        System.out.print("Inserisci la regione: ");
        String region = controlloInputStringhe(input);

        System.out.print("Inserisci la superficie: ");
        Double surfaceArea = controlloInputDouble(input, false);

        System.out.print("Inserisci l'anno di indipendenza (può essere null): ");
        int indepYear = controlloInputInteri(input, true);

        System.out.print("Inserisci il numero della popolazione: ");
        int population = controlloInputInteri(input, false);

        System.out.print("Inserisci l'aspettativa di vita (può essere null): ");
        Double lifeExpectancy = controlloInputDouble(input, true);

        System.out.print("Inserisci GNP (può essere null): ");
        Double gnp = controlloInputDouble(input, true);

        System.out.print("Inserisci GNP-OLD (può essere null): ");
        Double gnpOld = controlloInputDouble(input, true);

        System.out.print("Inserisci il Local Name: ");
        String localName = controlloInputStringhe(input);

        System.out.print("Inserisci il governament: ");
        String governmentForm = controlloInputStringhe(input);

        System.out.print("Inserisci headOfState (può essere null): ");
        String headOfState = input.nextLine(); // nessun controllo perchè permette campi null

        System.out.print("Inserisci capital (può essere null): ");
        int capital = controlloInputInteri(input, true);

        System.out.print("Inserisci code2: ");
        String code2 = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE2);

        // SQL per inserire un nuovo paese nella tabella "country"
        String sql = "INSERT INTO country (code, name, continent, region, surfacearea, indepyear, population, " +
                "lifeexpectancy, gnp, gnpold, localname, governmentform, headofstate, capital, code2) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = createPreparedStatement(conn, sql)) {
            // Impostiamo i parametri nella query
            pstmt.setString(1, code);
            pstmt.setString(2, name);
            pstmt.setString(3, continent);
            pstmt.setString(4, region);
            pstmt.setDouble(5, surfaceArea);
            pstmt.setInt(6, indepYear);
            pstmt.setLong(7, population);
            pstmt.setDouble(8, lifeExpectancy);
            pstmt.setDouble(9, gnp);
            pstmt.setDouble(10, gnpOld);
            pstmt.setString(11, localName);
            pstmt.setString(12, governmentForm);
            pstmt.setString(13, headOfState);
            pstmt.setInt(14, capital);
            pstmt.setString(15, code2);

            // Eseguiamo l'inserimento
            int rowsAffected = pstmt.executeUpdate();

            // Controllo se l'inserimento è andato a buon fine
            if (rowsAffected > 0) {
                System.out.println("Nuovo paese aggiunto con successo.");
            } else {
                System.out.println("Errore nell'inserimento del nuovo paese.");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento del paese: " + e.getMessage());
            throw e;
        }
    }

    // Metodo per aggiornare il numero di abitanti di una città
    public static void aggiornaNumeroAbitanti(Connection conn, Scanner input) throws SQLException {
        // SQL per aggiornare il numero di abitanti di una città in base al nome
        String sql = "UPDATE city SET population = ? WHERE name = ?";

        String name = "";
        boolean cityExists = false;
        // Continuare a chiedere il nome finché non viene trovata una città
        // corrispondente
        while (!cityExists) {
            System.out.print("Inserisci il nome della città: ");
            name = controlloInputStringhe(input);

            // Verifica se la città esiste nel database
            String checkCityQuery = "SELECT COUNT(*) FROM city WHERE name = ?";
            try (PreparedStatement pstmtCheck = createPreparedStatement(conn, checkCityQuery)) {
                pstmtCheck.setString(1, name);
                ResultSet rs = pstmtCheck.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    cityExists = true; // Se esiste almeno una città con quel nome, esci dal ciclo
                } else {
                    System.out.println("La città con nome \"" + name + "\" non esiste nel database. Riprova.");
                }
            }
        }

        System.out.print("Inserisci la popolazione della città: ");
        int popolazione = controlloInputInteri(input, false);

        try (PreparedStatement pstmt = createPreparedStatement(conn, sql)) {
            // Impostiamo i parametri: il nuovo numero di abitanti e il nome della città
            pstmt.setInt(1, popolazione);
            pstmt.setString(2, name);

            // Eseguiamo l'aggiornamento
            int rowsAffected = pstmt.executeUpdate();

            // Controlliamo se è stato aggiornato almeno un record
            if (rowsAffected > 0) {
                System.out.println("Numero di abitanti aggiornato con successo.");
            } else {
                System.out.println("Nessuna città trovata con l'ID specificato.");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento del numero di abitanti: " + e.getMessage());
            throw e;
        }
    }

    // Metodo per eliminare una città specifica in base all'ID
    public static void eliminazioneCittaSpecifica(Connection conn, Scanner input) throws SQLException {
        // SQL per eliminare una città specifica in base all'ID
        String sql = "DELETE FROM city WHERE id = ?";

        int cityId = -1;
        boolean idExists = false;
        while (!idExists) {
            System.out.print("Inserisci l'ID della città: ");
            cityId = controlloInputInteri(input, false);

            // Verifica se la città esiste nel database
            String checkCityQuery = "SELECT COUNT(*) FROM city WHERE ID = ?";
            try (PreparedStatement pstmtCheck = createPreparedStatement(conn, checkCityQuery)) {
                pstmtCheck.setInt(1, cityId);
                ResultSet rs = pstmtCheck.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    idExists = true; // Se esiste almeno una città con quel ID, esci dal ciclo
                } else {
                    System.out.println("La città con ID \"" + cityId + "\" non esiste nel database. Riprova.");
                }
            }
        }

        try (PreparedStatement pstmt = createPreparedStatement(conn, sql)) {
            // Imposta il parametro (cityId) per il PreparedStatement
            pstmt.setInt(1, cityId);

            // Esegui l'operazione di eliminazione
            int rowsAffected = pstmt.executeUpdate();

            // Controlla se è stata eliminata qualche riga
            if (rowsAffected > 0) {
                System.out.println("Città eliminata con successo.");
            } else {
                System.out.println("Nessuna città trovata con l'ID specificato.");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della città: " + e.getMessage());
            throw e;
        }
    }

    // #endregion

    // #region METODI DEI MENU

    // Metodo per il menu principale
    public static void mostraMenuPrincipale(Scanner scanner, Connection conn) throws SQLException {
        int scelta;
        boolean exitMainMenu = false;
        while (!exitMainMenu) {
            System.out.println("\n==== Menu Principale ====");
            System.out.println("1. Vedi le città");
            System.out.println("2. Vedi i paesi");
            System.out.println("3. Sotto-menu gestione database");
            System.out.println("4. Esci");
            System.out.print("Scegli un'opzione (1-4): ");
            scelta = controlloInputInteri(scanner, false);
            scanner.nextLine();

            switch (scelta) {
                case 1:
                    getInfoCity();
                    break;
                case 2:
                    getInfoCountry();
                    break;
                case 3:
                    mostraSottoMenuGestioneDB(conn, scanner);
                    break;
                case 4:
                    System.out.println("CIAOOOO");
                    exitMainMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }

    }

    // Metodo per il sotto-menu per eliminare una città
    public static void mostraSottoMenuGestioneDB(Connection conn, Scanner scanner) throws SQLException {
        int sceltaSottomenu;
        boolean exitEditDatabaseMenu = false;
        while (!exitEditDatabaseMenu) {
            System.out.println("\n==== Sotto-menu Eliminazione Città ====");
            System.out.println("1. Aggiornare la popolazione di una città specifica.");
            System.out.println("2. Aggiungere una nuova città nel database.");
            System.out.println("3. Eliminare una città specifica.");
            System.out.println("4. Aggiornare il numero di abitanti di un paese.");
            System.out.println("5. Aggiungere un nuovo paese nel database.");
            System.out.println("6. Creare una tabella backup per city.");
            System.out.println("7. Creare una tabella backup per country.");
            System.out.println("8. Scambiare di posto due record nelle tabella country.");
            System.out.println("9. Scambiare di posto due record nelle tabella city.");
            System.out.println("10. Aggiungere un Trigger per quando viene eliminata una city o un country.");
            System.out.println("0. Torna al menu principale :v ");
            System.out.print("Scegli un'opzione (0-9): ");
            sceltaSottomenu = controlloInputInteri(scanner, false);
            scanner.nextLine();

            switch (sceltaSottomenu) {
                case 1:
                    aggiornaNumeroAbitanti(conn, scanner);
                    break;
                case 2:
                    // TODO
                    break;
                case 3:
                    eliminazioneCittaSpecifica(conn, scanner);
                    break;
                case 4:
                    // TODO
                    break;
                case 5:
                    aggiungiNuovoPaese(conn, scanner);
                    break;
                case 6:
                    // TODO
                    break;
                case 7:
                    // TODO
                    break;
                case 8:
                    scambiaRecordCountry(conn, scanner);
                    break;
                case 9:
                    scambiaRecordCountry(conn, scanner);
                    break;
                case 10:
                    creaTrigger(conn);
                    break;
                case 0:
                    System.out.println("Tornando al menu principale...");
                    exitEditDatabaseMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }

    // #endregion

}