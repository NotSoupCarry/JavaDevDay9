import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EsercizioZ2 {

    //#region CONSTANTI
    // Costanti dati di connessione
    private static final String DB_URL = "jdbc:mysql://localhost:3306/world";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    // Constati db
    private static final int LUNGHEZZA_STRINGA_COUNTRYCODE = 3;
    private static final int LUNGHEZZA_STRINGA_COUNTRYCODE2 = 2;
    //#endregion

    // Metodo per la connessione
    public static Connection connessioneDatabase() {
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

    // Metodo per controllare l'input intero
    public static Integer controlloInputInteri(Scanner scanner) {
        Integer valore;
        do {
            while (!scanner.hasNextInt()) {
                System.out.print("Devi inserire un numero intero. Riprova ");
                scanner.next();
            }
            valore = scanner.nextInt();
            if (valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore < 0);
        return valore;
    }

    // Metodo per controllare l'input double, con possibilità di inserire null
    public static Double controlloInputDouble(Scanner scanner) {
        Double valore;
        do {
            while (!scanner.hasNextDouble()) {
                System.out.print("Devi inserire un numero decimale. Riprova ");
                scanner.next();
            }
            valore = scanner.nextDouble();
            if (valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore < 0);
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

    // Metodo per verificare se un paese esiste nel database
    public static boolean esistePaese(Connection conn, String countryCode) throws SQLException {
        String query = "SELECT COUNT(*) FROM country WHERE Code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, countryCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Il paese esiste
            } else {
                System.out.println("Il codice del paese " + countryCode + " non esiste nel database.");
                return false; // Il paese non esiste
            }
        }
    }

    // Metodo per verificare se una città esiste nel database
    public static boolean esisteCitta(Connection conn, String cityName) throws SQLException {
        String query = "SELECT COUNT(*) FROM city WHERE Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cityName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // La città esiste
            } else {
                System.out.println("La città con nome " + cityName + " non esiste nel database.");
                return false; // La città non esiste
            }
        }
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

    // Metodo per scambiare due record in country
    public static void scambiaRecordCountry(Connection conn, Scanner input) throws SQLException {
        String country1;
        do {
            System.out.print("Inserisci il codice del primo paese (3 caratteri): ");
            country1 = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE);
        } while (!esistePaese(conn, country1)); // Verifica se il paese esiste nel DB

        // Chiedi il codice del secondo paese, finché non esiste nel database
        String country2;
        do {
            System.out.print("Inserisci il codice del secondo paese (3 caratteri): ");
            country2 = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE);
        } while (!esistePaese(conn, country2)); // Verifica se il paese esiste nel DB

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

    // Metodo per scambiare due record nella tabella city
    public static void scambiaRecordCity(Connection conn, Scanner input) throws SQLException {
        // Chiedi il nome della prima città, finché non esiste nel database
        String city1;
        do {
            System.out.print("Inserisci il nome della prima città: ");
            city1 = controlloInputStringhe(input);
        } while (!esisteCitta(conn, city1)); // Verifica se la città esiste nel DB

        // Chiedi il nome della seconda città, finché non esiste nel database
        String city2;
        do {
            System.out.print("Inserisci il nome della seconda città: ");
            city2 = controlloInputStringhe(input);
        } while (!esisteCitta(conn, city2)); // Verifica se la città esiste nel DB

        // Interrogare i nomi delle città per scambiarli
        String selectQuery = "SELECT Name FROM city WHERE Name = ?";
        String tempCity1Name = null, tempCity2Name = null;

        try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
            // Recupera il nome della prima città
            pstmt.setString(1, city1);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tempCity1Name = rs.getString("Name");
            }

            // Recupera il nome della seconda città
            pstmt.setString(1, city2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                tempCity2Name = rs.getString("Name");
            }
        }

        // Usa un PreparedStatement per aggiornare i record nella tabella 'city'
        String updateQuery1 = "UPDATE city SET Name = ? WHERE Name = ?";
        String updateQuery2 = "UPDATE city SET Name = ? WHERE Name = ?";

        try (PreparedStatement pstmt1 = conn.prepareStatement(updateQuery1);
                PreparedStatement pstmt2 = conn.prepareStatement(updateQuery2)) {

            // 1. Aggiorna la prima città con un nome temporaneo
            pstmt1.setString(1, "TEMP");
            pstmt1.setString(2, city1);
            pstmt1.executeUpdate();

            // 2. Aggiorna la seconda città con il nome della prima città
            pstmt2.setString(1, tempCity1Name);
            pstmt2.setString(2, city2);
            pstmt2.executeUpdate();

            // 3. Aggiorna la prima città con il nome della seconda città
            pstmt1.setString(1, tempCity2Name);
            pstmt1.setString(2, city1);
            pstmt1.executeUpdate();

            System.out.println("Scambio tra le città " + city1 + " e " + city2 + " completato.");
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

        System.out.println("TRIGGER AGGIUNTO CON SUCCESSO!!! BRAVO");
    }

    // Metodo di stampa tabella city_log
    public static void stampaTabellaCityLog(Connection conn) {
        try {
            String queryCityLog = "SELECT * FROM city_log";
            PreparedStatement pstmt = createPreparedStatement(conn, queryCityLog);
            ResultSet rs = pstmt.executeQuery();
            stampaDati(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo di stampa tabella country_log
    public static void stampaTabellaCountryLog(Connection conn) {
        try {
            String queryCountryLog = "SELECT * FROM country_log";
            PreparedStatement pstmt = createPreparedStatement(conn, queryCountryLog);
            ResultSet rs = pstmt.executeQuery();
            stampaDati(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Double surfaceArea = controlloInputDouble(input);

        System.out.print("Inserisci l'anno di indipendenza: ");
        Integer indepYear = controlloInputInteri(input);

        System.out.print("Inserisci il numero della popolazione: ");
        Integer population = controlloInputInteri(input);

        System.out.print("Inserisci l'aspettativa di vita: ");
        Integer lifeExpectancy = controlloInputInteri(input); 

        System.out.print("Inserisci GNP: ");
        Double gnp = controlloInputDouble(input);

        System.out.print("Inserisci GNP-OLD: ");
        Double gnpOld = controlloInputDouble(input);

        System.out.print("Inserisci il Local Name: ");
        String localName = controlloInputStringhe(input);

        System.out.print("Inserisci il governament: ");
        String governmentForm = controlloInputStringhe(input);

        System.out.print("Inserisci headOfState: ");
        String headOfState = controlloInputStringhe(input); 

        System.out.print("Inserisci capital (può essere null): ");
        Integer capital = controlloInputInteri(input);

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

    // Metodo per aggiungere una nuova città
    public static void aggiungiNuovaCitta(Connection conn, Scanner input) throws SQLException {

        System.out.print("Inserisci il nome della città: ");
        String name = controlloInputStringhe(input);

        System.out.print("Inserisci il countrycode della città: ");
        String countryCode = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE);

        System.out.print("Inserisci il distretto: ");
        String district = controlloInputStringhe(input);

        System.out.print("Inserisci il numero della popolazione: ");
        int population = controlloInputInteri(input);

        // SQL per inserire un nuovo paese nella tabella "country"
        String sql = "INSERT INTO city VALUES (null, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = createPreparedStatement(conn, sql)) {
            // Impostiamo i parametri nella query
            pstmt.setString(1, name);
            pstmt.setString(2, countryCode);
            pstmt.setString(3, district);
            pstmt.setInt(4, population);

            // Eseguiamo l'inserimento
            int rowsAffected = pstmt.executeUpdate();

            // Controllo se l'inserimento è andato a buon fine
            if (rowsAffected > 0) {
                System.out.println("Nuova città aggiunto con successo.");
            } else {
                System.out.println("Errore nell'inserimento della nuova città.");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento della città: " + e.getMessage());
            throw e;
        }
    }

    // Metodo per aggiornare il numero di abitanti di una città
    public static void aggiornaNumeroAbitantiCitta(Connection conn, Scanner input) throws SQLException {
        // SQL per aggiornare il numero di abitanti di una città in base al nome
        String sql = "UPDATE city SET population = ? WHERE name = ?";

        String name = "";
        do {
            System.out.print("Inserisci il nome della prima città: ");
            name = controlloInputStringhe(input);
        } while (!esisteCitta(conn, name)); // Verifica se la città esiste nel DB

        System.out.print("Inserisci la popolazione della città: ");
        int popolazione = controlloInputInteri(input);

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

    // Metodo per aggiornare il numero di abitanti di un paese
    public static void aggiornaNumeroAbitantiPaese(Connection conn, Scanner input) throws SQLException {
        // SQL per aggiornare il numero di abitanti di un paese in base al code
        String sql = "UPDATE country SET Population = ? WHERE Code = ?";

        String code = "";
        do {
            System.out.print("Inserisci il codice del primo paese (3 caratteri): ");
            code = controlloInputStringheConLunghezza(input, LUNGHEZZA_STRINGA_COUNTRYCODE);
        } while (!esistePaese(conn, code)); // Verifica se il paese esiste nel DB

        System.out.print("Inserisci la popolazione del paese: ");
        int popolazione = controlloInputInteri(input);

        try (PreparedStatement pstmt = createPreparedStatement(conn, sql)) {
            // Impostiamo i parametri: il nuovo numero di abitanti e il code del paese
            pstmt.setInt(1, popolazione);
            pstmt.setString(2, code);

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
            cityId = controlloInputInteri(input);

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

    // Metodo per creare o sovrascrivere la tabella di backup per 'city'
    public static void creazioneBackupCity(Connection conn) throws SQLException {
        Statement stmt = getStatement(conn);

        // Elimina la tabella di backup se esiste già
        String dropQuery = "DROP TABLE IF EXISTS city_backup;";
        stmt.executeUpdate(dropQuery);

        // Ricrea la tabella di backup con la stessa struttura di 'city'
        String createQuery = "CREATE TABLE city_backup LIKE city;";
        stmt.executeUpdate(createQuery);

        // Copia i dati attuali della tabella 'city' nella tabella di backup
        String insertQuery = "INSERT INTO city_backup SELECT * FROM city;";
        stmt.executeUpdate(insertQuery);

        System.out.println("Backup della tabella 'city' completato con successo.");
    }

    // Metodo per creare o sovrascrivere la tabella di backup per 'country'
    public static void creazioneBackupCountry(Connection conn) throws SQLException {
        Statement stmt = getStatement(conn);

        // Elimina la tabella di backup se esiste già
        String dropQuery = "DROP TABLE IF EXISTS country_backup;";
        stmt.executeUpdate(dropQuery);

        // Ricrea la tabella di backup con la stessa struttura di 'country'
        String createQuery = "CREATE TABLE country_backup LIKE country;";
        stmt.executeUpdate(createQuery);

        // Copia i dati attuali della tabella 'country' nella tabella di backup
        String insertQuery = "INSERT INTO country_backup SELECT * FROM country;";
        stmt.executeUpdate(insertQuery);

        System.out.println("Backup della tabella 'country' completato con successo.");
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
            scelta = controlloInputInteri(scanner);
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
            System.out.println("6. Creare una tabella backup per city. (cancella vecchio backupCity)");
            System.out.println("7. Creare una tabella backup per country. (cancella vecchio backupCountry)");
            System.out.println("8. Scambiare di posto due record nelle tabella country.");
            System.out.println("9. Scambiare di posto due record nelle tabella city.");
            System.out.println(
                    "10. Aggiungere un Trigger per quando viene eliminata una city o un country e stampare tabbelle prod.");
            System.out.println("0. Torna al menu principale :v ");
            System.out.print("Scegli un'opzione (0-9): ");
            sceltaSottomenu = controlloInputInteri(scanner);
            scanner.nextLine();

            switch (sceltaSottomenu) {
                case 1:
                    aggiornaNumeroAbitantiCitta(conn, scanner);
                    break;
                case 2:
                    aggiungiNuovaCitta(conn, scanner);
                    break;
                case 3:
                    eliminazioneCittaSpecifica(conn, scanner);
                    break;
                case 4:
                    aggiornaNumeroAbitantiPaese(conn, scanner);
                    break;
                case 5:
                    aggiungiNuovoPaese(conn, scanner);
                    break;
                case 6:
                    creazioneBackupCity(conn);
                    break;
                case 7:
                    creazioneBackupCountry(conn);
                    break;
                case 8:
                    scambiaRecordCountry(conn, scanner);
                    break;
                case 9:
                    scambiaRecordCity(conn, scanner);
                    break;
                case 10:
                    creaTrigger(conn);
                    stampaTabellaCityLog(conn);
                    stampaTabellaCountryLog(conn);
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